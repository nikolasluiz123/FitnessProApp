package br.com.fitnesspro.scheduler.ui.state

import br.com.fitnesspro.compose.components.fields.state.PagedDialogListState
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.scheduler.ui.screen.scheduler.decorator.ChatHistoryDecorator
import br.com.fitnesspro.tuple.PersonTuple

data class ChatHistoryUIState(
    val title: String = "",
    val history: List<ChatHistoryDecorator> = emptyList(),
    val messageDialogState: MessageDialogState = MessageDialogState(),
    val membersDialogState: PagedDialogListState<PersonTuple> = PagedDialogListState(),
    val professionalsDialogState: PagedDialogListState<PersonTuple> = PagedDialogListState(),
    val userType: EnumUserType? = null
)