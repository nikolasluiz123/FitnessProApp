package br.com.fitnesspro.firebase.api.firestore.service

import br.com.fitnesspro.firebase.api.firestore.documents.ChatDocument
import br.com.fitnesspro.firebase.api.firestore.documents.MessageDocument
import br.com.fitnesspro.firebase.api.firestore.documents.MessageNotificationDocument
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

    suspend fun startChat(senderPerson: PersonDocument, receiverPerson: PersonDocument): String {
        val personsCollectionRef = db.collection(PersonDocument.COLLECTION_NAME)
        val senderPersonChatsRef = db.collection(getPersonChatsPath(senderPerson.id!!))
        val receiverPersonChatsRef = db.collection(getPersonChatsPath(receiverPerson.id!!))
        val chatId = UUID.randomUUID().toString()

        db.runTransaction { transaction ->
            transaction.set(personsCollectionRef.document(senderPerson.id), senderPerson)
            transaction.set(personsCollectionRef.document(receiverPerson.id), receiverPerson)

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

        return chatId
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
        val senderChatDocumentRef = db.collection(getPersonChatsPath(senderPersonId)).document(chatId)
        val receiverChatDocumentRef = db.collection(getPersonChatsPath(receiverPersonId)).document(chatId)

        val senderMessagesCollectionRef = db.collection(getChatMessagesPath(senderPersonId, chatId))
        val receiverMessagesCollectionRef = db.collection(getChatMessagesPath(receiverPersonId, chatId))

        val senderPerson = db
            .collection(PersonDocument.COLLECTION_NAME)
            .document(senderPersonId)
            .get().await().toObject(PersonDocument::class.java)!!

        val notificationDocumentRef = db
            .collection(getPersonNotificationsPath(receiverPersonId))
            .document(messageDocument.id)

        val serverTime = getServerTime()

        db.runTransaction { transaction->
            val senderChat = transaction.get(senderChatDocumentRef).toObject(ChatDocument::class.java)!!
            val receiverChat = transaction.get(receiverChatDocumentRef).toObject(ChatDocument::class.java)!!

            senderChat.apply {
                lastMessage = messageDocument.text
                lastMessageDate = serverTime
            }

            receiverChat.apply {
                lastMessage = messageDocument.text
                lastMessageDate = serverTime
                notReadMessagesCount++
            }

            messageDocument.apply {
                date = serverTime
            }

            val messageNotificationDocument = MessageNotificationDocument(
                id = messageDocument.id,
                text = messageDocument.text,
                personReceiverId = receiverPersonId,
                personSenderName = senderPerson.name!!,
                date = serverTime,
                chatId = chatId
            )

            transaction.update(senderChatDocumentRef, senderChat.toMap())
            transaction.update(receiverChatDocumentRef, receiverChat.toMap())

            transaction.set(notificationDocumentRef, messageNotificationDocument)
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

        val notificationsPath = getPersonNotificationsPath(authenticatedPersonId)
        val notificationsRef = db.collection(notificationsPath)

        return messagesRef.addSnapshotListener { value, error ->
            if (error != null) {
                onError(error)
                return@addSnapshotListener
            }

            if (value != null && !value.isEmpty) {
                CoroutineScope(IO).launch {
                    val messages = value.documents.map { it.toObject(MessageDocument::class.java)!! }
                    val notifications = notificationsRef
                        .whereIn(MessageNotificationDocument::id.name, messages.map { it.id })
                        .get().await().documents.map { it.reference }

                    readMessages(
                        messages = messages,
                        messagesDocumentsRef = value.documents.map { it.reference },
                        chatDocumentRef = chatRef,
                        notifications = notifications
                    )
                }
            }
        }
    }

    suspend fun getChatIdFromPerson(senderPersonId: String, receiverPersonId: String): String? {
        val chatsPath = getPersonChatsPath(senderPersonId)
        val queryDocumentSnapshot = db.collection(chatsPath)
            .whereEqualTo(ChatDocument::receiverPersonId.name, receiverPersonId)
            .get().await().firstOrNull()

        return queryDocumentSnapshot?.id
    }

    suspend fun deleteNotificationsAfterReceive(authenticatedPersonId: String, ids: List<String>) {
        val notificationsPath = getPersonNotificationsPath(authenticatedPersonId)
        val notificationsRef = db.collection(notificationsPath)

        val notifications = notificationsRef
            .whereIn(MessageNotificationDocument::id.name, ids)
            .get().await().documents.map { it.reference }

        db.runTransaction { transaction ->
            transaction.deleteNotifications(notifications)
        }
    }

    private suspend fun readMessages(
        messages: List<MessageDocument>,
        messagesDocumentsRef: List<DocumentReference>,
        chatDocumentRef: DocumentReference,
        notifications: List<DocumentReference>
    ) {
        db.runTransaction { transaction ->
            transaction.updateMessagesStateRead(messages, messagesDocumentsRef)
            transaction.deleteNotifications(notifications)
            transaction.updateNotReadMessagesCount(chatDocumentRef)
        }.await()
    }

    private fun Transaction.updateNotReadMessagesCount(chatDocumentRef: DocumentReference) {
        update(chatDocumentRef, ChatDocument::notReadMessagesCount.name, 0)
    }

    private fun Transaction.updateMessagesStateRead(
        messages: List<MessageDocument>,
        messagesDocuments: List<DocumentReference>
    ) {
        messages.forEach { messageDocument ->
            if (messageDocument.state != EnumMessageState.READ.name) {
                val documentReference = messagesDocuments.first { it.id == messageDocument.id }

                messageDocument.state = EnumMessageState.READ.name
                update(documentReference, messageDocument.toMap())
            }
        }
    }

    private fun Transaction.deleteNotifications(notifications: List<DocumentReference>) {
        notifications.forEach(::delete)
    }

    private fun getPersonNotificationsPath(personId: String): String {
        return "${PersonDocument.COLLECTION_NAME}/$personId/${MessageNotificationDocument.COLLECTION_NAME}"
    }

    private fun getChatMessagesPath(personId: String, chatId: String): String {
        return "${getPersonChatsPath(personId)}/$chatId/${MessageDocument.COLLECTION_NAME}"
    }

    private fun getPersonChatsPath(personId: String): String {
        return "${PersonDocument.COLLECTION_NAME}/$personId/${ChatDocument.COLLECTION_NAME}"
    }
}