package br.com.fitnesspro.ui.screen.workout.decorator

import java.time.DayOfWeek

data class CurrentWorkoutItemDecorator(
    val dayWeek: DayOfWeek,
    val muscularGroups: String
)