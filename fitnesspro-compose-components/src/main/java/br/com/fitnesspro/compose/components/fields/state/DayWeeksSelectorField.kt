package br.com.fitnesspro.compose.components.fields.state

import androidx.compose.runtime.mutableStateListOf
import java.time.DayOfWeek

data class DayWeeksSelectorField(
    val selected: MutableList<DayOfWeek> = mutableStateListOf(),
    val onSelect: (DayOfWeek) -> Unit = { }
)