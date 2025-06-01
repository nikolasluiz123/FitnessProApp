package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.workout.Exercise
import br.com.fitnesspro.model.workout.predefinition.ExercisePreDefinition
import br.com.fitnesspro.to.TOExercise
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
    )
}

fun ExercisePreDefinition.getTOExercisePreDefinition(): TOExercisePreDefinition {
    return TOExercisePreDefinition(
        id = id,
        name = name,
        duration = duration,
        repetitions = this@getTOExercisePreDefinition.repetitions,
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
    )

    id?.let { model.id = it }

    return model
}