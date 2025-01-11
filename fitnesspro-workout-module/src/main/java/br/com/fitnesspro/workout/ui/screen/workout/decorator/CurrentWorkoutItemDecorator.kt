package br.com.fitnesspro.workout.ui.screen.workout.decorator

import java.time.DayOfWeek

data class CurrentWorkoutItemDecorator(
    val dayWeek: DayOfWeek,
    val muscularGroups: String
)