package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.workout.Workout
import br.com.fitnesspro.model.workout.WorkoutGroup
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutDTO
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutGroupDTO

fun Workout.getWorkoutDTO(): WorkoutDTO {
    return WorkoutDTO(
        id = id,
        active = active,
        academyMemberPersonId = academyMemberPersonId,
        professionalPersonId = professionalPersonId,
        dateStart = dateStart,
        dateEnd = dateEnd,
    )
}

fun WorkoutGroup.getWorkoutGroupDTO(): WorkoutGroupDTO {
    return WorkoutGroupDTO(
        id = id,
        name = name,
        active = active,
        workoutId = workoutId,
        dayWeek = dayWeek,
    )
}