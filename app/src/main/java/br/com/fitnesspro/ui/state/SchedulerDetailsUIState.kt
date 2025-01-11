package br.com.fitnesspro.ui.state

import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.to.TOScheduler

data class SchedulerDetailsUIState(
    val title: String = "",
    val subtitle: String = "",
    val userType: EnumUserType? = null,
    val schedules: List<TOScheduler> = emptyList(),
    val isVisibleFabAdd: Boolean = true
)