package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.workout.Exercise
import br.com.fitnesspro.to.TOExercise

fun Exercise.getTOExercise(): TOExercise {
    return TOExercise(
        id = id,
        name = name,
        duration = duration,
        repetitions = repetitions,
        sets = sets,
        rest = rest,
        observation = observation,
        workoutGroupId = workoutGroupId,
        active = active,
    )
}