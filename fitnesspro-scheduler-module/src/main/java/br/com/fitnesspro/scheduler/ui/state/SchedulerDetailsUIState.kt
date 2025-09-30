package br.com.fitnesspro.scheduler.ui.state

import br.com.android.ui.compose.components.dialog.message.MessageDialogState
import br.com.android.ui.compose.components.states.ISuspendedLoadUIState
import br.com.android.ui.compose.components.states.IThrowableUIState
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.to.TOScheduler

data class SchedulerDetailsUIState(
    val title: String = "",
    val subtitle: String = "",
    val userType: EnumUserType? = null,
    val schedules: List<TOScheduler> = emptyList(),
    val isVisibleFabAdd: Boolean = true,
    override val messageDialogState: MessageDialogState = MessageDialogState(),
    override var executeLoad: Boolean = true
): IThrowableUIState, ISuspendedLoadUIState