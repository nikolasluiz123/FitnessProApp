package br.com.fitnesspro.workout.repository

import android.content.Context
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.ExerciseDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupDAO
import br.com.fitnesspro.mappers.getExercise
import br.com.fitnesspro.mappers.getTOExercise
import br.com.fitnesspro.mappers.getTOWorkoutGroup
import br.com.fitnesspro.to.TOExercise

class ExerciseRepository(
    context: Context,
    private val exerciseDAO: ExerciseDAO,
    private val workoutGroupDAO: WorkoutGroupDAO
): FitnessProRepository(context) {

    suspend fun findById(id: String): TOExercise {
        val exercise = exerciseDAO.findById(id)
        val workoutGroup = workoutGroupDAO.findById(exercise.workoutGroupId)?.getTOWorkoutGroup()

        return exercise.getTOExercise(workoutGroup)
    }

    suspend fun saveExercise(toExercise: TOExercise) {
        saveExerciseLocally(toExercise)
        saveExerciseRemote(toExercise)
    }

    suspend fun inactivateExercisesFromWorkoutGroup(workoutGroupId: String) {
        val exercises = exerciseDAO.findExerciesFromWorkoutGroup(workoutGroupId).onEach {
            it.active = false
        }

        exerciseDAO.updateBatch(exercises)
    }

    private suspend fun saveExerciseLocally(toExercise: TOExercise) {
        val exercise = toExercise.getExercise()

        if (toExercise.id == null) {
            exerciseDAO.insert(exercise)
        } else {
            exerciseDAO.update(exercise)
        }
    }

    private suspend fun saveExerciseRemote(toExercise: TOExercise) {


    }

}