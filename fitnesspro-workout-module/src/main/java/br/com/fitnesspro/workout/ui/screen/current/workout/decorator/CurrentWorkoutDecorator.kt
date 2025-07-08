package br.com.fitnesspro.workout.ui.screen.current.workout.decorator

import java.time.DayOfWeek

data class CurrentWorkoutDecorator(
    val dayWeek: DayOfWeek,
    val muscularGroups: String
)