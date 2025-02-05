package br.com.fitnesspro.scheduler.ui.state

import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.firebase.api.firestore.documents.MessageDocument

data class ChatUIState(
    val title: String = "",
    val subtitle: String? = null,
    val messages: List<MessageDocument> = emptyList(),
    val messageDialogState: MessageDialogState = MessageDialogState(),
    val messageTextField: TextField = TextField(),
    val authenticatedPersonId: String = "",
)