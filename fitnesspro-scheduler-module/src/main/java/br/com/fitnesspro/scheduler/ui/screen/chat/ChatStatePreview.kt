package br.com.fitnesspro.scheduler.ui.screen.chat

import br.com.core.utils.extensions.toEpochMillis
import br.com.fitnesspro.firebase.api.firestore.documents.ChatDocument
import br.com.fitnesspro.firebase.api.firestore.documents.MessageDocument
import br.com.fitnesspro.firebase.api.firestore.enums.EnumMessageState.READ
import br.com.fitnesspro.scheduler.ui.state.ChatHistoryUIState
import br.com.fitnesspro.scheduler.ui.state.ChatUIState
import java.time.LocalDateTime

internal val chatHistoryEmptyState = ChatHistoryUIState(
    title = "Histórico de Conversas"
)

internal val chatHistoryState = ChatHistoryUIState(
    title = "Histórico de Conversas",
    history = listOf(
        ChatDocument(
            receiverPersonName = "João",
            notReadMessagesCount = 2,
            lastMessage = "Olá, tudo bem?",
            lastMessageDate = LocalDateTime.now().toEpochMillis()
        ),
        ChatDocument(
            receiverPersonName = "Maria",
            notReadMessagesCount = 0,
            lastMessage = "Tudo bem, e você?",
            lastMessageDate = LocalDateTime.now().minusDays(1).toEpochMillis()
        ),
        ChatDocument(
            receiverPersonName = "Pedro",
            notReadMessagesCount = 1,
            lastMessage = "Estou bem, obrigado!",
            lastMessageDate = LocalDateTime.now().minusDays(2).toEpochMillis()
        )
    )
)

internal val chatHistoryItemState = ChatDocument(
    receiverPersonName = "João",
    notReadMessagesCount = 2,
    lastMessage = "Olá, tudo bem?",
    lastMessageDate = LocalDateTime.now().toEpochMillis()
)

internal val chatWithMessagesState = ChatUIState(
    title = "Mensagens",
    subtitle = "Nikolas Luiz Schmitt",
    authenticatedPersonId = "user_1",
    messages = listOf(
        MessageDocument(
            id = "5",
            text = "Tenho trabalhado bastante, mas está sendo produtivo!",
            personSenderId = "user_1",
            date = LocalDateTime.now().toEpochMillis(),
            state = READ.name,
        ),
        MessageDocument(
            id = "4",
            text = "Que bom! O que você tem feito ultimamente?",
            personSenderId = "user_2",
            date = LocalDateTime.now().minusDays(1).toEpochMillis(),
            state = READ.name,
        ),
        MessageDocument(
            id = "3",
            text = "Também estou bem, obrigado por perguntar!",
            personSenderId = "user_1",
            date = LocalDateTime.now().minusDays(1).toEpochMillis(),
            state = READ.name,
        ),
        MessageDocument(
            id = "2",
            text = "Oi! Estou bem, e você?",
            personSenderId = "user_2",
            date = LocalDateTime.now().minusDays(2).toEpochMillis(),
            state = READ.name,
        ),
        MessageDocument(
            id = "1",
            text = "Olá! Como você está?",
            personSenderId = "user_1",
            date = LocalDateTime.now().minusDays(2).toEpochMillis(),
            state = READ.name,
        )
    )
)