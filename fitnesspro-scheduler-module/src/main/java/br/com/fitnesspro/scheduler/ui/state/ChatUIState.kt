package br.com.fitnesspro.scheduler.ui.state

import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.scheduler.ui.screen.scheduler.decorator.MessageDecorator

data class ChatUIState(
    val title: String = "",
    val subtitle: String? = null,
    val messages: List<MessageDecorator> = emptyList(),
    val messageDialogState: MessageDialogState = MessageDialogState(),
    val messageTextField: TextField = TextField()
)