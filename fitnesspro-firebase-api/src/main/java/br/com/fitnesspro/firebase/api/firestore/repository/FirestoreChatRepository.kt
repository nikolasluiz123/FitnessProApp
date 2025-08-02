package br.com.fitnesspro.firebase.api.firestore.repository

import br.com.fitnesspro.firebase.api.firestore.documents.ChatDocument
import br.com.fitnesspro.firebase.api.firestore.documents.MessageDocument
import br.com.fitnesspro.firebase.api.firestore.documents.PersonDocument
import br.com.fitnesspro.firebase.api.firestore.service.FirestoreChatService
import br.com.fitnesspro.to.TOPerson
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class FirestoreChatRepository(
    private val firestoreChatService: FirestoreChatService
) {
    private var chatHistoryListener: ListenerRegistration? = null
    private var messagesListListener: ListenerRegistration? = null
    private var messagesReadListener: ListenerRegistration? = null

    suspend fun startChat(senderPerson: TOPerson, receiverPerson: TOPerson) = withContext(IO) {
        firestoreChatService.startChat(
            senderPerson = senderPerson.getDocument(),
            receiverPerson = receiverPerson.getDocument()
        )
    }

    suspend fun getPersonNameFromChat(personId: String, chatId: String): String = withContext(IO) {
        firestoreChatService.getPersonNameFromChat(personId, chatId)
    }

    suspend fun sendMessage(
        message: String,
        senderPersonId: String,
        receiverPersonId: String,
        chatId: String
    ) {
        val messageDocument = MessageDocument(text = message, personSenderId = senderPersonId)

        firestoreChatService.sendMessage(
            messageDocument = messageDocument,
            senderPersonId = senderPersonId,
            receiverPersonId = receiverPersonId,
            chatId = chatId
        )
    }

    suspend fun getChatDocument(personId: String, chatId: String): ChatDocument {
        return firestoreChatService.getChatDocument(personId, chatId)
    }

    suspend fun getChatIdFromPerson(senderPerson: TOPerson, receiverPerson: TOPerson): String = withContext(IO) {
        firestoreChatService.getChatIdFromPerson(
            senderPersonId = senderPerson.id!!,
            receiverPersonId = receiverPerson.id!!
        ) ?: startChatAndReturnChatId(senderPerson, receiverPerson)
    }

    suspend fun deleteNotificationsAfterReceive(authenticatedPersonId: String, ids: List<String>) = withContext(IO) {
        firestoreChatService.deleteNotificationsAfterReceive(authenticatedPersonId, ids)
    }

    private suspend fun startChatAndReturnChatId(senderPerson: TOPerson, receiverPerson: TOPerson): String {
        return startChat(senderPerson, receiverPerson)
    }

    fun addChatListListener(
        authenticatedPersonId: String,
        onSuccess: (List<ChatDocument>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        chatHistoryListener = firestoreChatService.addChatListListener(
            authenticatedPersonId = authenticatedPersonId,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun addMessagesListListener(
        authenticatedPersonId: String,
        chatId: String,
        onSuccess: (List<MessageDocument>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        messagesListListener = firestoreChatService.addMessagesListListener(
            authenticatedPersonId = authenticatedPersonId,
            chatId = chatId,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    suspend fun addMessagesReadListener(
        authenticatedPersonId: String,
        chatId: String,
        onError: (Exception) -> Unit
    ) = withContext(IO) {
        messagesReadListener = firestoreChatService.addMessagesReadListener(
            authenticatedPersonId = authenticatedPersonId,
            chatId = chatId,
            onError = onError
        )
    }

    fun removeChatListListener() {
        chatHistoryListener?.remove()
    }

    fun removeMessagesListListener() {
        messagesListListener?.remove()
    }

    fun removeMessagesReadListener() {
        messagesReadListener?.remove()
    }

    private fun TOPerson.getDocument(): PersonDocument {
        return PersonDocument(id = id!!, name = name!!)
    }
}