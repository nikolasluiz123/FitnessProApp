package br.com.fitnesspro.workout.repository

import android.content.Context
import android.util.Log
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.ExerciseDAO
import br.com.fitnesspro.mappers.getExercise
import br.com.fitnesspro.mappers.getTOExercise
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.to.TOWorkoutGroup
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class ExerciseRepository(
    context: Context,
    private val exerciseDAO: ExerciseDAO,
    private val workoutGroupRepository: WorkoutGroupRepository,
    private val videoRepository: VideoRepository
): FitnessProRepository(context) {

    suspend fun findById(id: String): TOExercise = withContext(IO) {
        val exercise = exerciseDAO.findById(id)
        val workoutGroup = workoutGroupRepository.findWorkoutGroupById(exercise.workoutGroupId)

        exercise.getTOExercise(workoutGroup)
    }

    suspend fun saveExercise(toExercise: TOExercise) {
        runInTransaction {
            saveWorkoutGroupExerciseLocally(toExercise)
            saveExerciseLocally(toExercise)
        }
    }

    private suspend fun saveWorkoutGroupExerciseLocally(toExercise: TOExercise) {
        val toWorkoutGroup = if (toExercise.workoutGroupId == null) {
            TOWorkoutGroup(
                name = toExercise.workoutGroupName,
                workoutId = toExercise.workoutId,
                dayWeek = toExercise.dayWeek,
                order = toExercise.groupOrder
            )
        } else {
            workoutGroupRepository.findWorkoutGroupById(toExercise.workoutGroupId)?.apply {
                name = toExercise.workoutGroupName
                order = toExercise.groupOrder
            }
        }

        toWorkoutGroup?.let { to ->
            workoutGroupRepository.saveWorkoutGroupLocally(to)
            toExercise.workoutGroupId = to.id
        }
    }

    private suspend fun saveExerciseLocally(toExercise: TOExercise) {
        val exercise = toExercise.getExercise()

        if (toExercise.id == null) {
            exerciseDAO.insert(exercise)
        } else {
            exerciseDAO.update(exercise)
        }

        toExercise.id = exercise.id
        Log.i("Teste", "saveExerciseLocally: toExercise.id = ${toExercise.id}")
    }

    suspend fun inactivateExercisesFromWorkoutGroupLocally(listWorkoutGroupId: List<String>) {
        val exercises = exerciseDAO.findExercisesFromWorkoutGroup(listWorkoutGroupId).onEach {
            it.active = false
        }

        exerciseDAO.updateBatch(exercises, true)
        videoRepository.inactivateVideoExercise(exercises.map { it.id })
    }

    suspend fun inactivateExercise(exerciseId: String) {
        val exercise = exerciseDAO.findById(exerciseId).apply {
            active = false
        }

        exerciseDAO.update(exercise, true)
        videoRepository.inactivateVideoExercise(listOf(exerciseId))
    }

    suspend fun getCountExercisesFromWorkoutGroup(workoutGroupId: String): Int = withContext(IO) {
        exerciseDAO.getCountExercisesFromWorkoutGroup(workoutGroupId)
    }

}