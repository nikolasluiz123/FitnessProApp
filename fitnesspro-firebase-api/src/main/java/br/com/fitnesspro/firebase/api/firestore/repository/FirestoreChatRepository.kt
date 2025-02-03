package br.com.fitnesspro.firebase.api.firestore.repository

import br.com.fitnesspro.firebase.api.firestore.documents.ChatDocument
import br.com.fitnesspro.firebase.api.firestore.documents.MessageDocument
import br.com.fitnesspro.firebase.api.firestore.documents.PersonDocument
import br.com.fitnesspro.firebase.api.firestore.service.FirestoreChatService
import br.com.fitnesspro.to.TOPerson
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class FirestoreChatRepository(
    private val firestoreChatService: FirestoreChatService
) {

    suspend fun startChat(senderPerson: TOPerson, receiverPerson: TOPerson) = withContext(IO) {
        firestoreChatService.startChat(
            senderPerson = senderPerson.getDocument(),
            receiverPerson = receiverPerson.getDocument()
        )
    }

    suspend fun getChatList(authenticatedPersonId: String): List<ChatDocument> = withContext(IO) {
        firestoreChatService.getChatList(authenticatedPersonId)
    }

    suspend fun getPersonNameFromChat(personId: String, chatId: String): String = withContext(IO) {
        firestoreChatService.getPersonNameFromChat(personId, chatId)
    }

    suspend fun sendMessage(
        message: String,
        senderPersonId: String,
        receiverPersonId: String,
        chatId: String
    ) = withContext(IO) {
        val messageDocument = MessageDocument(text = message, personSenderId = senderPersonId)

        firestoreChatService.sendMessage(
            messageDocument = messageDocument,
            senderPersonId = senderPersonId,
            receiverPersonId = receiverPersonId,
            chatId = chatId
        )
    }

    suspend fun getChatDocument(personId: String, chatId: String): ChatDocument = withContext(IO) {
        firestoreChatService.getChatDocument(personId, chatId)
    }

    private fun TOPerson.getDocument(): PersonDocument {
        return PersonDocument(id = id!!, name = name!!)
    }
}