package br.com.fitnesspro.workout.repository

import android.content.Context
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.ExerciseDAO
import br.com.fitnesspro.mappers.getExercise
import br.com.fitnesspro.mappers.getTOExercise
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.to.TOWorkoutGroup

class ExerciseRepository(
    context: Context,
    private val exerciseDAO: ExerciseDAO,
    private val workoutGroupRepository: WorkoutGroupRepository
): FitnessProRepository(context) {

    suspend fun findById(id: String): TOExercise {
        val exercise = exerciseDAO.findById(id)
        val workoutGroup = workoutGroupRepository.findWorkoutGroupById(exercise.workoutGroupId)

        return exercise.getTOExercise(workoutGroup)
    }

    suspend fun saveExercise(toExercise: TOExercise) {
        runInTransaction {
            saveExerciseLocally(toExercise)
            saveExerciseRemote(toExercise)
        }
    }

    private suspend fun saveExerciseLocally(toExercise: TOExercise) {
        saveExerciseWorkoutGroup(toExercise)
        insertOrUpdateExercise(toExercise)
    }

    private suspend fun insertOrUpdateExercise(toExercise: TOExercise) {
        val exercise = toExercise.getExercise()

        if (toExercise.id == null) {
            exerciseDAO.insert(exercise)
        } else {
            exerciseDAO.update(exercise)
        }
    }

    private suspend fun saveExerciseWorkoutGroup(toExercise: TOExercise) {
        val toWorkoutGroup = if (toExercise.workoutGroupId == null) {
            TOWorkoutGroup(
                name = toExercise.workoutGroupName,
                workoutId = toExercise.workoutId,
                dayWeek = toExercise.dayWeek
            )
        } else {
            workoutGroupRepository.findWorkoutGroupById(toExercise.workoutGroupId)?.apply {
                name = toExercise.workoutGroupName
            }
        }

        toWorkoutGroup?.let { to ->
            workoutGroupRepository.saveWorkoutGroup(to)
            toExercise.workoutGroupId = to.id
        }
    }

    private suspend fun saveExerciseRemote(toExercise: TOExercise) {


    }

}