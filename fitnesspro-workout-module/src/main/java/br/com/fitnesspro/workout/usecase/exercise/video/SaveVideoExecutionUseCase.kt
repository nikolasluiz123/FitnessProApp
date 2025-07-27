package br.com.fitnesspro.workout.usecase.exercise.video

import android.content.Context
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.core.utils.FileUtils
import br.com.fitnesspro.core.utils.VideoUtils
import br.com.fitnesspro.core.validation.ValidationError
import br.com.fitnesspro.to.TOVideo
import br.com.fitnesspro.to.TOVideoExerciseExecution
import br.com.fitnesspro.workout.repository.VideoRepository
import br.com.fitnesspro.workout.usecase.exercise.exception.VideoException
import br.com.fitnesspro.workout.usecase.exercise.video.common.AbstractSaveVideoUseCase
import java.io.File
import java.time.ZoneId

class SaveVideoExecutionUseCase(
    context: Context,
    private val videoRepository: VideoRepository
): AbstractSaveVideoUseCase(context) {

    suspend operator fun invoke(exerciseExecutionId: String, videoFile: File): ValidationError? {
        return try {
            val toVideoExerciseExecution = getTOVideoExecution(videoFile, exerciseExecutionId)

            validateVideosLimit(toVideoExerciseExecution)
            validateVideoDuration(toVideoExerciseExecution.toVideo!!)
            validateVideoSize(toVideoExerciseExecution.toVideo!!, videoFile)

            videoRepository.saveVideoExerciseExecutionLocally(
                toVideoExerciseExecutionList = listOf(toVideoExerciseExecution)
            )

            null
        } catch (ex: VideoException) {
            ValidationError(ex.message!!)
        }
    }

    private fun getTOVideoExecution(videoFile: File, exerciseExecutionId: String?): TOVideoExerciseExecution {
        val (width, height) = VideoUtils.getVideoResolution(videoFile)!!

        val toVideoExerciseExecution = TOVideoExerciseExecution(
            exerciseExecutionId = exerciseExecutionId,
            toVideo = TOVideo(
                filePath = videoFile.absolutePath,
                extension = videoFile.extension,
                date = dateTimeNow(ZoneId.systemDefault()),
                kbSize = FileUtils.getFileSizeInKB(videoFile),
                seconds = VideoUtils.getVideoDurationInSeconds(videoFile),
                width = width,
                height = height
            )
        )
        return toVideoExerciseExecution
    }

    @Throws(VideoException::class)
    private suspend fun validateVideosLimit(toVideoExerciseExecution: TOVideoExerciseExecution) {
        val count = videoRepository.getCountVideosExecution(toVideoExerciseExecution.exerciseExecutionId!!)
        validateVideosLimit(count)
    }

    @Throws(VideoException::class)
    suspend fun validateVideo(toVideoExerciseExecution: TOVideoExerciseExecution, videoFile: File) {
        validateVideosLimit(toVideoExerciseExecution)
        validateVideoDuration(toVideoExerciseExecution.toVideo!!)
        validateVideoSize(toVideoExerciseExecution.toVideo!!, videoFile)
    }

    @Throws(VideoException::class)
    suspend fun validateVideoFile(videoFile: File) {
        val toVideoExerciseExecution = getTOVideoExecution(videoFile, null)

        validateVideoDuration(toVideoExerciseExecution.toVideo!!)
        validateVideoSize(toVideoExerciseExecution.toVideo!!, videoFile)
    }
}