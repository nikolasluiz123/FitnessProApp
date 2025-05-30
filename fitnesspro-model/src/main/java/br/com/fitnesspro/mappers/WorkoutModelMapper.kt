package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.workout.Workout
import br.com.fitnesspro.model.workout.WorkoutGroup
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutDTO
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutGroupDTO
import br.com.fitnesspro.to.TOWorkout
import br.com.fitnesspro.to.TOWorkoutGroup

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

fun WorkoutGroup.getTOWorkoutGroup(): TOWorkoutGroup {
    return TOWorkoutGroup(
        id = id,
        name = name,
        active = active,
        workoutId = workoutId,
        dayWeek = dayWeek,
    )
}

fun Workout.geTOWorkout(memberName: String?, professionalName: String?): TOWorkout {
    return TOWorkout(
        id = id,
        active = active,
        academyMemberPersonId = academyMemberPersonId,
        professionalPersonId = professionalPersonId,
        dateStart = dateStart,
        dateEnd = dateEnd,
        memberName = memberName,
        professionalName = professionalName
    )
}

fun TOWorkout.getWorkout(): Workout {
    val model = Workout(
        active = active,
        academyMemberPersonId = academyMemberPersonId,
        professionalPersonId = professionalPersonId,
        dateStart = dateStart,
        dateEnd = dateEnd,
    )

    id?.let { model.id = it }

    return model
}

fun TOWorkoutGroup.getWorkoutGroup(): WorkoutGroup {
    val model = WorkoutGroup(
        name = name,
        active = active,
        workoutId = workoutId,
        dayWeek = dayWeek,
    )

    id?.let { model.id = it }

    return model
}