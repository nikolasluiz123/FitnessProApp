package br.com.fitnesspro.workout.repository

import android.content.Context
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.ExerciseDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupDAO
import br.com.fitnesspro.mappers.getTOExercise
import br.com.fitnesspro.to.TOExercise

class ExerciseRepository(
    context: Context,
    private val exerciseDAO: ExerciseDAO,
    private val workoutGroupDAO: WorkoutGroupDAO
): FitnessProRepository(context) {

    suspend fun findById(id: String): TOExercise {
        val exercise = exerciseDAO.findById(id)
        val workoutGroupName = workoutGroupDAO.findById(exercise.workoutGroupId)?.name

        return exercise.getTOExercise(workoutGroupName)
    }
}