package br.com.fitnesspro.workout.ui.state.reports

import br.com.fitnesspro.tuple.WorkoutTuple
import java.time.LocalDate

val defaultWorkoutTuple = WorkoutTuple(
    id = "1",
    dateStart = LocalDate.now(),
    dateEnd = LocalDate.now().plusDays(90),
)