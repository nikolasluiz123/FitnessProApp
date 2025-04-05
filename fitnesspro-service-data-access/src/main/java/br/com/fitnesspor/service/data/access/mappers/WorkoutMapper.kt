package br.com.fitnesspor.service.data.access.mappers

import br.com.fitnesspro.model.workout.Workout
import br.com.fitnesspro.model.workout.WorkoutGroup
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutDTO
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutGroupDTO

fun Workout.toWorkoutDTO(): WorkoutDTO {
    return WorkoutDTO(
        id = id,
        active = active,
        academyMemberPersonId = academyMemberPersonId,
        professionalPersonId = professionalPersonId,
        dateStart = start,
        dateEnd = end
    )
}

fun WorkoutGroup.toWorkoutGroupDTO(): WorkoutGroupDTO {
    return WorkoutGroupDTO(
        id = id,
        active = active,
        workoutId = workoutId,
        dayWeek = dayWeek,
        name = name
    )
}