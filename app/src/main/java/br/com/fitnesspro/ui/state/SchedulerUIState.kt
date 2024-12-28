package br.com.fitnesspro.ui.state

import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.to.TOSchedulerConfig
import br.com.fitnesspro.ui.screen.scheduler.decorator.SchedulerDecorator
import java.time.YearMonth

data class SchedulerUIState(
    val title: String = "",
    val userType: EnumUserType? = null,
    val toSchedulerConfig: TOSchedulerConfig? = null,
    val schedules: List<SchedulerDecorator> = emptyList(),
    val selectedYearMonth: YearMonth = YearMonth.now(),
    val onSelectYearMonth: (newYearMonth: YearMonth) -> Unit = { }
)