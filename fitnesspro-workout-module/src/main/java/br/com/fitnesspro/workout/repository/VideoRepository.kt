package br.com.fitnesspro.workout.repository

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.ExerciseWebClient
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.VideoDAO
import br.com.fitnesspro.local.data.access.dao.VideoExerciseDAO
import br.com.fitnesspro.local.data.access.dao.VideoExerciseExecutionDAO
import br.com.fitnesspro.local.data.access.dao.VideoExercisePreDefinitionDAO
import br.com.fitnesspro.mappers.getVideo
import br.com.fitnesspro.mappers.getVideoExercise
import br.com.fitnesspro.mappers.getVideoExerciseExecution
import br.com.fitnesspro.mappers.getVideoExercisePreDefinition
import br.com.fitnesspro.to.TOVideoExercise
import br.com.fitnesspro.to.TOVideoExerciseExecution
import br.com.fitnesspro.to.TOVideoExercisePreDefinition

class VideoRepository(
    context: Context,
    private val videoDAO: VideoDAO,
    private val videoExerciseDAO: VideoExerciseDAO,
    private val videoExerciseExecutionDAO: VideoExerciseExecutionDAO,
    private val videoExercisePreDefinitionDAO: VideoExercisePreDefinitionDAO,
    private val exerciseWebClient: ExerciseWebClient
): FitnessProRepository(context) {

    suspend fun saveVideoExercise(toVideoExercise: TOVideoExercise) {
        runInTransaction {
            saveVideoExerciseLocally(toVideoExercise)
        }
    }

    private suspend fun saveVideoExerciseLocally(toVideoExercise: TOVideoExercise) {
        val video = toVideoExercise.toVideo?.getVideo()!!

        if (toVideoExercise.toVideo?.id == null) {
            videoDAO.insert(video)
            toVideoExercise.toVideo?.id = video.id
        } else {
            videoDAO.update(video)
        }

        val videoExercise = toVideoExercise.getVideoExercise()

        if (toVideoExercise.id == null) {
            videoExerciseDAO.insert(videoExercise)
            toVideoExercise.id = videoExercise.id
        } else {
            videoExerciseDAO.update(videoExercise)
        }
    }

    suspend fun getVideoExercises(exerciseId: String): List<String> {
        return videoExerciseDAO.getListVideoFilePathsFromExercise(exerciseId)
    }

    suspend fun getVideoExerciseExecution(exerciseExecutionId: String): List<String> {
        return videoExerciseDAO.getListVideoFilePathsFromExecution(exerciseExecutionId)
    }

    suspend fun getVideoExercisePreDefinition(exercisePreDefinitionId: String): List<String> {
        return videoExerciseDAO.getListVideoFilePathsFromPreDefinition(exercisePreDefinitionId)
    }

    suspend fun getCountVideosExercise(exerciseId: String): Int {
        return videoExerciseDAO.getCountVideosExercise(exerciseId)
    }

    suspend fun getCountVideosExecution(exerciseExecutionId: String): Int {
        return videoExerciseExecutionDAO.getCountVideosExecution(exerciseExecutionId)
    }

    suspend fun getCountVideosPreDefinition(exercisePreDefinitionId: String): Int {
        return videoExercisePreDefinitionDAO.getCountVideosPreDefinition(exercisePreDefinitionId)
    }

    suspend fun inactivateVideoExercise(exerciseIds: List<String>) {
        val videos = videoDAO.getListVideosActiveFromExercise(exerciseIds).onEach {
            it.active = false
        }
        val videoExerciseList = videoExerciseDAO.getListVideoExerciseActiveFromExercises(exerciseIds).onEach {
            it.active = false
        }

        videoDAO.updateBatch(videos, true)
        videoExerciseDAO.updateBatch(videoExerciseList, true)
    }

    suspend fun saveVideoExerciseExecutionLocally(toVideoExerciseExecutionList: List<TOVideoExerciseExecution>) {
        val (inserts, updates) = toVideoExerciseExecutionList.partition { it.id == null }

        if (inserts.isNotEmpty()) saveVideoExerciseExecutionLocally(inserts, isInsert = true)
        if (updates.isNotEmpty()) saveVideoExerciseExecutionLocally(updates, isInsert = false)
    }

    private suspend fun saveVideoExerciseExecutionLocally(list: List<TOVideoExerciseExecution>, isInsert: Boolean) {
        val videoExecutionList = list.map {
            val videoExecution = it.getVideoExerciseExecution()
            if (isInsert) it.id = videoExecution.id
            videoExecution
        }

        val videoList = list.mapNotNull {
            it.toVideo?.getVideo()?.also { video ->
                if (isInsert) it.toVideo?.id = video.id
            }
        }

        if (isInsert) {
            videoDAO.insertBatch(videoList)
            videoExerciseExecutionDAO.insertBatch(videoExecutionList)
        } else {
            videoDAO.updateBatch(videoList)
            videoExerciseExecutionDAO.updateBatch(videoExecutionList)
        }
    }

    suspend fun createExecutionVideo(toVideoExerciseExecution: TOVideoExerciseExecution) {
        runInTransaction {
            saveVideoExerciseExecutionLocally(listOf(toVideoExerciseExecution), isInsert = true)
            createExecutionVideoRemote(toVideoExerciseExecution)
        }
    }

    private suspend fun createExecutionVideoRemote(toVideoExerciseExecution: TOVideoExerciseExecution) {
        exerciseWebClient.createExecutionVideo(
            token = getValidToken(),
            videoExecution = toVideoExerciseExecution.getVideoExerciseExecution(),
            video = toVideoExerciseExecution.toVideo!!.getVideo()
        )
    }

    suspend fun saveVideoExercisePreDefinitionLocally(toVideoExercisePreDefinitionList: List<TOVideoExercisePreDefinition>) {
        val (inserts, updates) = toVideoExercisePreDefinitionList.partition { it.id == null }

        if (inserts.isNotEmpty()) saveVideoExercisePreDefinitionLocally(inserts, isInsert = true)
        if (updates.isNotEmpty()) saveVideoExercisePreDefinitionLocally(updates, isInsert = false)
    }

    private suspend fun saveVideoExercisePreDefinitionLocally(list: List<TOVideoExercisePreDefinition>, isInsert: Boolean) {
        val videoPreDefinitionList = list.map {
            val videoPreDefinition = it.getVideoExercisePreDefinition()
            if (isInsert) it.id = videoPreDefinition.id
            videoPreDefinition
        }

        val videoList = list.mapNotNull {
            it.toVideo?.getVideo()?.also { video ->
                if (isInsert) it.toVideo?.id = video.id
            }
        }

        if (isInsert) {
            videoDAO.insertBatch(videoList)
            videoExercisePreDefinitionDAO.insertBatch(videoPreDefinitionList)
        } else {
            videoDAO.updateBatch(videoList)
            videoExercisePreDefinitionDAO.updateBatch(videoPreDefinitionList)
        }
    }
}