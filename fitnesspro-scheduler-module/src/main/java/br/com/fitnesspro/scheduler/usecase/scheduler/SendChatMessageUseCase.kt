package br.com.fitnesspro.scheduler.usecase.scheduler

import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.firebase.api.firestore.repository.FirestoreChatRepository

class SendChatMessageUseCase(
    private val firestoreChatRepository: FirestoreChatRepository,
    private val personRepository: PersonRepository
) {

    suspend operator fun invoke(message: String, chatId: String) {
        if (message.trim().isEmpty()) return

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