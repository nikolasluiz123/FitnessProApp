package br.com.fitnesspro.scheduler.ui.state

import br.com.android.ui.compose.components.dialog.list.paged.PagedDialogListState
import br.com.android.ui.compose.components.dialog.message.MessageDialogState
import br.com.android.ui.compose.components.states.ISuspendedLoadUIState
import br.com.android.ui.compose.components.states.IThrowableUIState
import br.com.fitnesspro.firebase.api.firestore.documents.ChatDocument
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.tuple.PersonTuple

data class ChatHistoryUIState(
    val title: String = "",
    val history: List<ChatDocument> = emptyList(),
    val membersDialogState: PagedDialogListState<PersonTuple> = PagedDialogListState(),
    val professionalsDialogState: PagedDialogListState<PersonTuple> = PagedDialogListState(),
    val userType: EnumUserType? = null,
    val authenticatedPerson: TOPerson = TOPerson(),
    override val messageDialogState: MessageDialogState = MessageDialogState(),
    override var executeLoad: Boolean = true
): IThrowableUIState, ISuspendedLoadUIState