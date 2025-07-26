package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.Workout
import br.com.fitnesspro.model.workout.WorkoutGroup
import br.com.fitnesspro.model.workout.predefinition.WorkoutGroupPreDefinition
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutDTO
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutGroupDTO
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutGroupPreDefinitionDTO
import br.com.fitnesspro.to.TOWorkout
import br.com.fitnesspro.to.TOWorkoutGroup
import br.com.fitnesspro.to.TOWorkoutGroupPreDefinition

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
        groupOrder = groupOrder
    )
}

fun WorkoutGroup.getTOWorkoutGroup(): TOWorkoutGroup {
    return TOWorkoutGroup(
        id = id,
        name = name,
        active = active,
        workoutId = workoutId,
        dayWeek = dayWeek,
        order = groupOrder
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
        groupOrder = order ?: 1
    )

    id?.let { model.id = it }

    return model
}

fun WorkoutDTO.getWorkout(): Workout {
    return Workout(
        id = id!!,
        active = active,
        academyMemberPersonId = academyMemberPersonId,
        professionalPersonId = professionalPersonId,
        dateStart = dateStart,
        dateEnd = dateEnd,
        transmissionState = EnumTransmissionState.TRANSMITTED,
    )
}

fun WorkoutGroupDTO.getWorkoutGroup(): WorkoutGroup {
    return WorkoutGroup(
        id = id!!,
        name = name,
        active = active,
        workoutId = workoutId,
        dayWeek = dayWeek,
        groupOrder = groupOrder,
        transmissionState = EnumTransmissionState.TRANSMITTED,
    )
}

fun TOWorkoutGroupPreDefinition.getWorkoutGroupPreDefinition(): WorkoutGroupPreDefinition {
    val model = WorkoutGroupPreDefinition(
        name = name,
        personalTrainerPersonId = personalTrainerPersonId,
        active = active
    )

    id?.let { model.id = it }

    return model
}

fun WorkoutGroupPreDefinition.getWorkoutGroupPreDefinitionDTO(): WorkoutGroupPreDefinitionDTO {
    return WorkoutGroupPreDefinitionDTO(
        id = id,
        name = name,
        personalTrainerPersonId = personalTrainerPersonId,
        active = active
    )
}

fun WorkoutGroupPreDefinitionDTO.getWorkoutGroupPreDefinition(): WorkoutGroupPreDefinition {
    return WorkoutGroupPreDefinition(
        id = id!!,
        name = name,
        personalTrainerPersonId = personalTrainerPersonId,
        active = active,
        transmissionState = EnumTransmissionState.TRANSMITTED,
    )
}

fun WorkoutGroupPreDefinition.getTOWorkoutGroupPreDefinition(): TOWorkoutGroupPreDefinition {
    return TOWorkoutGroupPreDefinition(
        id = id,
        name = name,
        personalTrainerPersonId = personalTrainerPersonId,
        active = active
    )
}