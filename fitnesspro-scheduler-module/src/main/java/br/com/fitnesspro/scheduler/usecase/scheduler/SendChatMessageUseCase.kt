package br.com.fitnesspro.scheduler.usecase.scheduler

import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.firebase.api.firestore.repository.FirestoreChatRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class SendChatMessageUseCase(
    private val firestoreChatRepository: FirestoreChatRepository,
    private val personRepository: PersonRepository
) {

    suspend operator fun invoke(message: String, chatId: String) = withContext(IO) {
        if (message.trim().isEmpty()) return@withContext

        val person = personRepository.getAuthenticatedTOPerson()!!
        val chat = firestoreChatRepository.getChatDocument(
            personId = person.id!!,
            chatId = chatId
        )

        firestoreChatRepository.sendMessage(
            message = message,
            senderPersonId = person.id!!,
            receiverPersonId = chat.receiverPersonId,
            chatId = chatId
        )
    }
}