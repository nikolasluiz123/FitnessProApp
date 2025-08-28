package br.com.fitnesspro.workout.repository

import android.content.Context
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.core.utils.VideoUtils
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
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class VideoRepository(
    context: Context,
    private val videoDAO: VideoDAO,
    private val videoExerciseDAO: VideoExerciseDAO,
    private val videoExerciseExecutionDAO: VideoExerciseExecutionDAO,
    private val videoExercisePreDefinitionDAO: VideoExercisePreDefinitionDAO,
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

    suspend fun getVideoExercises(exerciseId: String): List<String> = withContext(IO) {
        videoExerciseDAO.getListVideoFilePathsFromExercise(exerciseId)
    }

    suspend fun getVideoExerciseExecution(exerciseExecutionId: String): List<String> = withContext(IO) {
        videoExerciseExecutionDAO.getListVideoFilePathsFromExecution(exerciseExecutionId)
    }

    suspend fun getVideoExercisePreDefinition(exercisePreDefinitionId: String): List<String> = withContext(IO) {
        videoExercisePreDefinitionDAO.getListVideoFilePathsFromPreDefinition(exercisePreDefinitionId)
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

    suspend fun getListExerciseExecutionIdsFromVideoFilePaths(filePaths: List<String>): List<String> {
        return videoExerciseExecutionDAO.getListExerciseExecutionIdsFromVideoFilePaths(filePaths)
    }

    suspend fun getListExerciseIdsFromVideoFilePaths(filePaths: List<String>): List<String> {
        return videoExerciseDAO.getListExerciseIdsFromVideoFilePaths(filePaths)
    }

    suspend fun getListExercisePreDefinitionIdsFromVideoFilePaths(filePaths: List<String>): List<String> {
        return videoExercisePreDefinitionDAO.getListExercisePreDefinitionIdsFromVideoFilePaths(filePaths)
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

        videos.forEach {
            VideoUtils.deleteVideoFile(context, it.filePath!!)
        }
    }

    suspend fun inactivateVideoExercisePreDefinition(exercisePreDefinitionIds: List<String>) {
        val videos = videoDAO.getListVideosActiveFromPreDefinition(exercisePreDefinitionIds).onEach {
            it.active = false
        }
        val videoExercisePreDefinitionList = videoExercisePreDefinitionDAO.getListVideoPreDefinitionActiveFromExercises(exercisePreDefinitionIds).onEach {
            it.active = false
        }

        videoDAO.updateBatch(videos, true)
        videoExercisePreDefinitionDAO.updateBatch(videoExercisePreDefinitionList, true)

        videos.forEach {
            VideoUtils.deleteVideoFile(context, it.filePath!!)
        }
    }

    suspend fun inactivateVideoExerciseExecution(exerciseExecutionIds: List<String>) {
        val videos = videoDAO.getListVideosActiveFromExecution(exerciseExecutionIds).onEach {
            it.active = false
        }
        val videoExerciseExecutionList = videoExerciseExecutionDAO.getListVideoExecutionActiveFromExercises(exerciseExecutionIds).onEach {
            it.active = false
        }

        videoDAO.updateBatch(videos, true)
        videoExerciseExecutionDAO.updateBatch(videoExerciseExecutionList, true)

        videos.forEach {
            VideoUtils.deleteVideoFile(context, it.filePath!!)
        }
    }

    suspend fun saveVideoExerciseExecutionLocally(toVideoExerciseExecutionList: List<TOVideoExerciseExecution>) {
        val inserts = toVideoExerciseExecutionList.filter { it.id == null }

        if (inserts.isNotEmpty()) {
            val videoList = inserts.map {
                val video = it.toVideo?.getVideo()!!
                it.toVideo?.id = video.id
                video
            }

            val videoExecutionList = inserts.map {
                val videoExecution = it.getVideoExerciseExecution()
                it.id = videoExecution.id
                videoExecution
            }

            videoDAO.insertBatch(videoList)
            videoExerciseExecutionDAO.insertBatch(videoExecutionList)
        }
    }

    suspend fun saveVideoExercisePreDefinitionLocally(toVideoExercisePreDefinitionList: List<TOVideoExercisePreDefinition>) {
        val inserts = toVideoExercisePreDefinitionList.filter { it.id == null }

        if (inserts.isNotEmpty()) {
            val videoList = inserts.map {
                val video = it.toVideo?.getVideo()!!
                it.toVideo?.id = video.id
                video
            }

            val videoPreDefinitionList = inserts.map {
                val videoPreDefinition = it.getVideoExercisePreDefinition()
                it.id = videoPreDefinition.id
                videoPreDefinition
            }

            videoDAO.insertBatch(videoList)
            videoExercisePreDefinitionDAO.insertBatch(videoPreDefinitionList)
        }
    }
}