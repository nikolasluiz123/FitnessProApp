package br.com.fitnesspro.workout.ui.screen.currentworkout.decorator

import java.time.DayOfWeek

data class CurrentWorkoutItemDecorator(
    val dayWeek: DayOfWeek,
    val muscularGroups: String
)