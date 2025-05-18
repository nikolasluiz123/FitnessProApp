package br.com.fitnesspro.workout.ui.screen.members.workout.decorator

import java.time.LocalDate

data class MemberWorkoutDecorator(
    val memberName: String,
    val workoutEndDate: LocalDate,
)
