package br.com.fitnesspro.scheduler.ui.state

import br.com.fitnesspro.core.state.IThrowableUIState
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.to.TOScheduler

data class SchedulerDetailsUIState(
    val title: String = "",
    val subtitle: String = "",
    val userType: EnumUserType? = null,
    val schedules: List<TOScheduler> = emptyList(),
    val isVisibleFabAdd: Boolean = true,
    override val messageDialogState: MessageDialogState = MessageDialogState()
): IThrowableUIState