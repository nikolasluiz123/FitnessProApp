package br.com.fitnesspro.scheduler.ui.state

import br.com.android.ui.compose.components.dialog.message.MessageDialogState
import br.com.android.ui.compose.components.states.ISuspendedLoadUIState
import br.com.android.ui.compose.components.states.IThrowableUIState
import br.com.core.utils.extensions.yearMonthNow
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.scheduler.ui.screen.scheduler.decorator.SchedulerDecorator
import br.com.fitnesspro.to.TOSchedulerConfig
import java.time.YearMonth

data class SchedulerUIState(
    val title: String = "",
    val userType: EnumUserType? = null,
    val toSchedulerConfig: TOSchedulerConfig? = null,
    val schedules: List<SchedulerDecorator> = emptyList(),
    val selectedYearMonth: YearMonth = yearMonthNow(),
    val onSelectYearMonth: (newYearMonth: YearMonth) -> Unit = { },
    val isVisibleFabRecurrentScheduler: Boolean = false,
    val newSchedulerReportDialogUIState: NewSchedulerReportDialogUIState = NewSchedulerReportDialogUIState(),
    override val messageDialogState: MessageDialogState = MessageDialogState(),
    override var executeLoad: Boolean = true
): IThrowableUIState, ISuspendedLoadUIState