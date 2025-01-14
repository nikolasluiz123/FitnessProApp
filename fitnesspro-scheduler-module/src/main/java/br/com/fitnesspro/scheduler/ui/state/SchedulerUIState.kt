package br.com.fitnesspro.scheduler.ui.state

import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.scheduler.ui.screen.scheduler.decorator.SchedulerDecorator
import br.com.fitnesspro.to.TOSchedulerConfig
import java.time.YearMonth

data class SchedulerUIState(
    val title: String = "",
    val userType: EnumUserType? = null,
    val toSchedulerConfig: TOSchedulerConfig? = null,
    val schedules: List<SchedulerDecorator> = emptyList(),
    val selectedYearMonth: YearMonth = YearMonth.now(),
    val onSelectYearMonth: (newYearMonth: YearMonth) -> Unit = { },
    val isVisibleFabRecurrentScheduler: Boolean = false,
)