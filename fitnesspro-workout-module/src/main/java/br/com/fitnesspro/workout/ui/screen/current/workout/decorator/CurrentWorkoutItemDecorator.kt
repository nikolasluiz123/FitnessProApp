package br.com.fitnesspro.workout.ui.screen.current.workout.decorator

import java.time.DayOfWeek

data class CurrentWorkoutItemDecorator(
    val dayWeek: DayOfWeek,
    val muscularGroups: String
)