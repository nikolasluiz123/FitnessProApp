package br.com.fitnesspro.workout.repository

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.ExerciseWebClient
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.VideoDAO
import br.com.fitnesspro.local.data.access.dao.VideoExerciseDAO
import br.com.fitnesspro.mappers.getVideo
import br.com.fitnesspro.mappers.getVideoExercise
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.to.TOVideoExercise

class VideoRepository(
    context: Context,
    private val videoDAO: VideoDAO,
    private val videoExerciseDAO: VideoExerciseDAO,
    private val exerciseWebClient: ExerciseWebClient
): FitnessProRepository(context) {

    suspend fun saveVideoExercise(toVideoExercise: TOVideoExercise) {
        runInTransaction {
            saveVideoExerciseLocally(toVideoExercise)
            saveVideoExerciseRemote(toVideoExercise)
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

    private suspend fun saveVideoExerciseRemote(toVideoExercise: TOVideoExercise) {
        val video = toVideoExercise.toVideo!!.getVideo()
        val videoExercise = toVideoExercise.getVideoExercise()

        val response = exerciseWebClient.createVideo(
            token = getValidToken(),
            video = video,
            videoExercise = videoExercise
        )

        if (response.success) {
            videoDAO.update(
                model = video.copy(transmissionState = EnumTransmissionState.TRANSMITTED),
                writeTransmissionState = true
            )

            videoExerciseDAO.update(
                model = videoExercise.copy(transmissionState = EnumTransmissionState.TRANSMITTED),
                writeTransmissionState = true
            )
        }
    }

    suspend fun getVideoExercises(exerciseId: String): List<String> {
        return videoExerciseDAO.getListVideoFilePathsFromExercise(exerciseId)
    }

    suspend fun getCountVideosExercise(exerciseId: String): Int {
        return videoExerciseDAO.getCountVideosExercise(exerciseId)
    }

    suspend fun deleteVideos(exerciseIds: List<String>) {
        val videoExerciseList = videoExerciseDAO.getListVideoExerciseFromExercises(exerciseIds)

        videoDAO.deleteVideos(videoExerciseList.map { it.videoId!! })
        videoExerciseDAO.deleteVideosExercise(videoExerciseList)
    }

    suspend fun getExistsVideoExerciseTransmitted(
        workoutId: String? = null,
        workoutGroupId: String? = null,
        exerciseId: String? = null
        ): Boolean {
        return videoExerciseDAO.getExistsVideoExerciseTransmitted(workoutId, workoutGroupId, exerciseId)
    }

}