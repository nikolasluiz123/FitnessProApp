package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.workout.Exercise
import br.com.fitnesspro.model.workout.predefinition.ExercisePreDefinition
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.to.TOExercisePreDefinition

fun Exercise.getTOExercise(workoutGroupName: String? = null): TOExercise {
    return TOExercise(
        id = id,
        name = name,
        duration = duration,
        repetitions = repetitions,
        sets = this@getTOExercise.sets,
        rest = rest,
        observation = observation,
        workoutGroupId = workoutGroupId,
        workoutGroupName = workoutGroupName,
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