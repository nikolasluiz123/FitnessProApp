package br.com.fitnesspro.ui.state

import br.com.fitnesspro.ui.screen.workout.decorator.DayWeekWorkoutGroupDecorator

data class DayWeekWorkoutUIState(
    val title: String,
    val subtitle: String,
    val dayWeekWorkoutGroups: List<DayWeekWorkoutGroupDecorator>,
)