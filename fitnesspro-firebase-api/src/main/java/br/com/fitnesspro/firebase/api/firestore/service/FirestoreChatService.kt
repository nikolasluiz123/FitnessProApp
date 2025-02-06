package br.com.fitnesspro.firebase.api.firestore.service

import br.com.fitnesspro.firebase.api.firestore.documents.ChatDocument
import br.com.fitnesspro.firebase.api.firestore.documents.MessageDocument
import br.com.fitnesspro.firebase.api.firestore.documents.PersonDocument
import br.com.fitnesspro.firebase.api.firestore.enums.EnumMessageState
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirestoreChatService: FirestoreService() {

    suspend fun startChat(senderPerson: PersonDocument, receiverPerson: PersonDocument) {
        val personsCollectionRef = db.collection(PersonDocument.COLLECTION_NAME)
        val senderPersonChatsRef = db.collection(getPersonChatsPath(senderPerson.id!!))
        val receiverPersonChatsRef = db.collection(getPersonChatsPath(receiverPerson.id!!))

        db.runTransaction { transaction ->
            transaction.set(personsCollectionRef.document(senderPerson.id), senderPerson)
            transaction.set(personsCollectionRef.document(receiverPerson.id), receiverPerson)

            val chatId = UUID.randomUUID().toString()

            val chatSender = ChatDocument(
                id = chatId,
                receiverPersonName = receiverPerson.name!!,
                receiverPersonId = receiverPerson.id
            )

            val chatReceiver = ChatDocument(
                id = chatId,
                receiverPersonName = senderPerson.name!!,
                receiverPersonId = senderPerson.id
            )

            transaction.set(senderPersonChatsRef.document(chatSender.id), chatSender)
            transaction.set(receiverPersonChatsRef.document(chatReceiver.id), chatReceiver)
        }.await()
    }

    suspend fun getPersonNameFromChat(personId: String, chatId: String): String {
        return getChatDocument(personId, chatId).receiverPersonName
    }

    suspend fun sendMessage(
        messageDocument: MessageDocument,
        senderPersonId: String,
        receiverPersonId: String,
        chatId: String
    ) {
        val senderChatDocument = db.collection(getPersonChatsPath(senderPersonId)).document(chatId)
        val receiverChatDocument = db.collection(getPersonChatsPath(receiverPersonId)).document(chatId)

        val senderMessagesCollectionRef = db.collection(getChatMessagesPath(senderPersonId, chatId))
        val receiverMessagesCollectionRef = db.collection(getChatMessagesPath(receiverPersonId, chatId))

        val serverTime = getServerTime()

        db.runTransaction { transaction->
            val senderChat = transaction.get(senderChatDocument).toObject(ChatDocument::class.java)!!
            val receiverChat = transaction.get(receiverChatDocument).toObject(ChatDocument::class.java)!!

            senderChat.apply {
                lastMessage = messageDocument.text
                lastMessageDate = serverTime
            }

            receiverChat.apply {
                lastMessage = messageDocument.text
                lastMessageDate = serverTime
                notReadMessagesCount++
            }

            transaction.set(senderChatDocument, senderChat)
            transaction.set(receiverChatDocument, receiverChat)

            messageDocument.apply {
                date = serverTime
            }

            transaction.set(senderMessagesCollectionRef.document(messageDocument.id), messageDocument)
            transaction.set(receiverMessagesCollectionRef.document(messageDocument.id), messageDocument)
        }.await()
    }

    suspend fun getChatDocument(personId: String, chatId: String): ChatDocument {
        val chatRef = db.collection(getPersonChatsPath(personId)).document(chatId)
        return chatRef.get().await().toObject(ChatDocument::class.java)!!
    }

    fun addChatListListener(
        authenticatedPersonId: String,
        onSuccess: (List<ChatDocument>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        val chatsPath = getPersonChatsPath(authenticatedPersonId)
        val chatsQuery = db.collection(chatsPath)
            .orderBy(ChatDocument::lastMessageDate.name, Query.Direction.DESCENDING)

        return chatsQuery.addSnapshotListener { value, error ->
            if (error != null) {
                onError(error)
                return@addSnapshotListener
            }

            if (value != null && !value.isEmpty) {
                val chats = value.documents.map { it.toObject(ChatDocument::class.java)!! }
                onSuccess(chats)
            }
        }
    }

    fun addMessagesListListener(
        authenticatedPersonId: String,
        chatId: String,
        onSuccess: (List<MessageDocument>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        val messagesPath = getChatMessagesPath(authenticatedPersonId, chatId)
        val messagesQuery = db.collection(messagesPath)
            .orderBy(MessageDocument::date.name, Query.Direction.DESCENDING)

        return messagesQuery.addSnapshotListener { value, error ->
            if (error != null) {
                onError(error)
                return@addSnapshotListener
            }

            if (value != null && !value.isEmpty) {
                val messages = value.documents.map { it.toObject(MessageDocument::class.java)!! }
                onSuccess(messages)
            }
        }
    }

    suspend fun addMessagesReadListener(
        authenticatedPersonId: String,
        chatId: String,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        val chatRef = db.collection(getPersonChatsPath(authenticatedPersonId)).document(chatId)
        val chatDocument = getChatDocument(authenticatedPersonId, chatId)
        val messagesPath = getChatMessagesPath(chatDocument.receiverPersonId, chatId)
        val messagesRef = db.collection(messagesPath)

        return messagesRef.addSnapshotListener { value, error ->
            if (error != null) {
                onError(error)
                return@addSnapshotListener
            }

            if (value != null && !value.isEmpty) {
                CoroutineScope(IO).launch {
                    val messages = value.documents.map { it.toObject(MessageDocument::class.java)!! }

                    readMessages(
                        messages = messages,
                        messagesDocumentsRef = value.documents.map { it.reference },
                        chatDocument = chatDocument,
                        chatDocumentRef = chatRef
                    )
                }
            }
        }
    }

    private suspend fun readMessages(
        messages: List<MessageDocument>,
        messagesDocumentsRef: List<DocumentReference>,
        chatDocument: ChatDocument,
        chatDocumentRef: DocumentReference
    ) {
        db.runTransaction { transaction ->
            transaction.updateMessagesStateRead(messages, messagesDocumentsRef)
            transaction.updateNotReadMessagesCount(chatDocument, chatDocumentRef)
        }.await()
    }

    private fun Transaction.updateNotReadMessagesCount(
        chatDocument: ChatDocument,
        chatDocumentRef: DocumentReference
    ) {
        chatDocument.notReadMessagesCount = 0
        set(chatDocumentRef, chatDocument)
    }

    private fun Transaction.updateMessagesStateRead(
        messages: List<MessageDocument>,
        messagesDocuments: List<DocumentReference>
    ) {
        messages.forEach { messageDocument ->
            if (messageDocument.state != EnumMessageState.READ.name) {
                messageDocument.state = EnumMessageState.READ.name
                val documentReference = messagesDocuments.first { it.id == messageDocument.id }
                set(documentReference, messageDocument)
            }
        }
    }

    private fun getChatMessagesPath(personId: String, chatId: String): String {
        return "${getPersonChatsPath(personId)}/$chatId/${MessageDocument.COLLECTION_NAME}"
    }

    private fun getPersonChatsPath(personId: String): String {
        return "${PersonDocument.COLLECTION_NAME}/$personId/${ChatDocument.COLLECTION_NAME}"
    }
}