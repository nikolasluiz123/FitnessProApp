package br.com.fitnesspro.ui.state

import br.com.fitnesspro.ui.screen.schedule.decorator.SchedulerDecorator
import java.time.YearMonth

data class ScheduleUIState(
    val title: String = "",
    val schedules: List<SchedulerDecorator> = emptyList(),
    val selectedYearMonth: YearMonth = YearMonth.now(),
    val onSelectYearMonth: (newYearMonth: YearMonth) -> Unit = { }
)