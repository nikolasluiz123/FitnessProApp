package br.com.fitnesspro.workout.ui.screen.workout.decorator

data class DayWeekWorkoutItemDecorator(
    val id: String,
    val exercise: String,
    val duration: Long?,
    val sets: Int?,
    val repetitions: Int?,
    val rest: Long?,
    val observation: String?,
)