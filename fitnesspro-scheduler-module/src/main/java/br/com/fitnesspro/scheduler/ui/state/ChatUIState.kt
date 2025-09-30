package br.com.fitnesspro.scheduler.ui.state

import br.com.android.ui.compose.components.dialog.message.MessageDialogState
import br.com.android.ui.compose.components.fields.text.state.TextField
import br.com.android.ui.compose.components.states.ISuspendedLoadUIState
import br.com.android.ui.compose.components.states.IThrowableUIState
import br.com.fitnesspro.firebase.api.firestore.documents.MessageDocument

data class ChatUIState(
    val title: String = "",
    val subtitle: String? = null,
    val messages: List<MessageDocument> = emptyList(),
    val messageTextField: TextField = TextField(),
    val authenticatedPersonId: String = "",
    override val messageDialogState: MessageDialogState = MessageDialogState(),
    override var executeLoad: Boolean = true,
): IThrowableUIState, ISuspendedLoadUIState