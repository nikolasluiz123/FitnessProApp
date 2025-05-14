package br.com.fitnesspro.workout.ui.screen.membersworkout.decorator

import java.time.LocalDate

data class MemberWorkoutDecorator(
    val memberName: String,
    val workoutEndDate: LocalDate,
)
