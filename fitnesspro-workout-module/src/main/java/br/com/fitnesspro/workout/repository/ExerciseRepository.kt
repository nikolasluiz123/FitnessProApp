package br.com.fitnesspro.workout.repository

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.ExerciseWebClient
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.ExerciseDAO
import br.com.fitnesspro.mappers.getExercise
import br.com.fitnesspro.mappers.getTOExercise
import br.com.fitnesspro.mappers.getWorkoutGroup
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.to.TOWorkoutGroup

class ExerciseRepository(
    context: Context,
    private val exerciseDAO: ExerciseDAO,
    private val workoutGroupRepository: WorkoutGroupRepository,
    private val exerciseWebClient: ExerciseWebClient,
    private val videoRepository: VideoRepository
): FitnessProRepository(context) {

    suspend fun findById(id: String): TOExercise {
        val exercise = exerciseDAO.findById(id)
        val workoutGroup = workoutGroupRepository.findWorkoutGroupById(exercise.workoutGroupId)

        return exercise.getTOExercise(workoutGroup)
    }

    suspend fun saveExercise(toExercise: TOExercise) {
        runInTransaction {
            val toWorkoutGroup = saveWorkoutGroupExerciseLocally(toExercise)
            saveExerciseLocally(toExercise)
            saveExerciseRemote(toExercise, toWorkoutGroup)
        }
    }

    private suspend fun saveWorkoutGroupExerciseLocally(toExercise: TOExercise): TOWorkoutGroup? {
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

        return toWorkoutGroup
    }

    private suspend fun saveExerciseLocally(toExercise: TOExercise) {
        val exercise = toExercise.getExercise()

        if (toExercise.id == null) {
            exerciseDAO.insert(exercise)
        } else {
            exerciseDAO.update(exercise)
        }

        toExercise.id = exercise.id
    }

    private suspend fun saveExerciseRemote(toExercise: TOExercise, toWorkoutGroup: TOWorkoutGroup?) {
        exerciseWebClient.saveExercise(
            token = getValidToken(),
            exercise = toExercise.getExercise(),
            workoutGroup = toWorkoutGroup?.getWorkoutGroup()!!
        )
    }

    suspend fun inactivateExercisesFromWorkoutGroup(
        workoutGroupId: String,
        inactivateWorkoutGroupRemoteSuccess: Boolean
    ) {
        val exercises = exerciseDAO.findExerciesFromWorkoutGroup(workoutGroupId).onEach {
            it.active = false
        }

        exerciseDAO.updateBatch(exercises, true)

        if (inactivateWorkoutGroupRemoteSuccess) {
            exercises.forEach {
                it.transmissionState = EnumTransmissionState.TRANSMITTED
            }

            exerciseDAO.updateBatch(exercises)
            videoRepository.deleteVideos(exercises.map { it.id })
        }
    }

}