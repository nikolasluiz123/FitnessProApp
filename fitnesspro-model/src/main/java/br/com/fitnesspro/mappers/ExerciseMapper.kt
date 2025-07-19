package br.com.fitnesspro.mappers

import br.com.fitnesspro.core.extensions.bestChronoUnit
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.Exercise
import br.com.fitnesspro.model.workout.WorkoutGroup
import br.com.fitnesspro.model.workout.execution.ExerciseExecution
import br.com.fitnesspro.model.workout.predefinition.ExercisePreDefinition
import br.com.fitnesspro.shared.communication.dtos.workout.ExerciseDTO
import br.com.fitnesspro.shared.communication.dtos.workout.ExerciseExecutionDTO
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.to.TOExerciseExecution
import br.com.fitnesspro.to.TOExercisePreDefinition
import br.com.fitnesspro.to.TOWorkoutGroup

fun Exercise.getTOExercise(toWorkoutGroup: TOWorkoutGroup? = null): TOExercise {
    return TOExercise(
        id = id,
        name = name,
        duration = duration,
        repetitions = repetitions,
        sets = sets,
        rest = rest,
        observation = observation,
        workoutId = toWorkoutGroup?.workoutId,
        workoutGroupId = workoutGroupId,
        workoutGroupName = toWorkoutGroup?.name,
        dayWeek = toWorkoutGroup?.dayWeek,
        active = active,
        exerciseOrder = exerciseOrder,
        groupOrder = toWorkoutGroup?.order ?: 1,
        unitRest = rest?.bestChronoUnit(),
        unitDuration = duration?.bestChronoUnit()
    )
}

fun ExercisePreDefinition.getTOExercisePreDefinition(): TOExercisePreDefinition {
    return TOExercisePreDefinition(
        id = id,
        name = name,
        duration = duration,
        repetitions = repetitions,
        sets = sets,
        rest = rest,
        personalTrainerPersonId = personalTrainerPersonId,
        workoutGroupPreDefinitionId = workoutGroupPreDefinitionId,
        active = active,
    )
}

fun TOExercise.getExercise(): Exercise {
    val model = Exercise(
        name = name,
        duration = duration,
        repetitions = repetitions,
        sets = sets,
        rest = rest,
        observation = observation,
        workoutGroupId = workoutGroupId,
        active = active,
        exerciseOrder = exerciseOrder
    )

    id?.let { model.id = it }

    return model
}

fun Exercise.getExerciseDTO(workoutGroup: WorkoutGroup?): ExerciseDTO {
    return ExerciseDTO(
        id = id,
        name = name,
        duration = duration,
        repetitions = repetitions,
        sets = sets,
        rest = rest,
        observation = observation,
        workoutGroupDTO = workoutGroup?.getWorkoutGroupDTO(),
        active = active,
        exerciseOrder = exerciseOrder
    )
}

fun ExerciseDTO.getExercise(): Exercise {
    return Exercise(
        id = id!!,
        name = name,
        duration = duration,
        repetitions = repetitions,
        sets = sets,
        rest = rest,
        observation = observation,
        workoutGroupId = workoutGroupDTO?.id,
        active = active,
        exerciseOrder = exerciseOrder,
        transmissionState = EnumTransmissionState.TRANSMITTED
    )
}

fun TOExerciseExecution.getExerciseExecution(): ExerciseExecution {
    val model = ExerciseExecution(
        exerciseId = exerciseId,
        repetitions = repetitions,
        actualSet = set,
        duration = duration,
        rest = rest,
        weight = weight,
        date = date!!,
        active = active,
    )

    id?.let { model.id = it }

    return model
}

fun ExerciseExecution.getTOExerciseExecution(): TOExerciseExecution {
    return TOExerciseExecution(
        id = id,
        exerciseId = exerciseId,
        repetitions = repetitions,
        set = actualSet,
        duration = duration,
        rest = rest,
        weight = weight,
        date = date,
        active = active,
        restUnit = rest?.bestChronoUnit(),
        durationUnit = duration?.bestChronoUnit()
    )
}

fun ExerciseExecution.getExerciseExecutionDTO(): ExerciseExecutionDTO {
    return ExerciseExecutionDTO(
        id = id,
        exerciseId = exerciseId,
        repetitions = repetitions,
        set = actualSet,
        duration = duration,
        rest = rest,
        weight = weight,
        date = date,
        active = active,
    )
}