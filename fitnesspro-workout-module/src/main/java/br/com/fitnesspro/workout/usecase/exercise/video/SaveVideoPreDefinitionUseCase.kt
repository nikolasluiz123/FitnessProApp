package br.com.fitnesspro.workout.usecase.exercise.video

import android.content.Context
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.core.utils.FileUtils
import br.com.fitnesspro.core.utils.VideoUtils
import br.com.fitnesspro.core.validation.ValidationError
import br.com.fitnesspro.to.TOVideo
import br.com.fitnesspro.to.TOVideoExercisePreDefinition
import br.com.fitnesspro.workout.repository.VideoRepository
import br.com.fitnesspro.workout.usecase.exercise.exception.VideoException
import br.com.fitnesspro.workout.usecase.exercise.video.common.AbstractSaveVideoUseCase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.io.File
import java.time.ZoneId

class SaveVideoPreDefinitionUseCase(
    context: Context,
    private val videoRepository: VideoRepository
): AbstractSaveVideoUseCase(context) {

    suspend operator fun invoke(exercisePreDefinitionId: String, videoFile: File): ValidationError? = withContext(IO) {
         try {
            val toVideoExercisePreDefinition = getTOVideoPreDefinition(videoFile, exercisePreDefinitionId)

            validateVideosLimit(toVideoExercisePreDefinition)
            validateVideoDuration(toVideoExercisePreDefinition.toVideo!!)
            validateVideoSize(toVideoExercisePreDefinition.toVideo!!, videoFile)

            videoRepository.saveVideoExercisePreDefinitionLocally(
                toVideoExercisePreDefinitionList = listOf(toVideoExercisePreDefinition)
            )

            null
        } catch (ex: VideoException) {
            ValidationError(ex.message!!)
        }
    }

    private fun getTOVideoPreDefinition(videoFile: File, exercisePreDefinitionId: String?): TOVideoExercisePreDefinition {
        val (width, height) = VideoUtils.getVideoResolution(videoFile)!!

        val toVideoExercisePreDefinition = TOVideoExercisePreDefinition(
            exercisePreDefinitionId = exercisePreDefinitionId,
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

        return toVideoExercisePreDefinition
    }

    @Throws(VideoException::class)
    private suspend fun validateVideosLimit(toVideoExercisePreDefinition: TOVideoExercisePreDefinition) {
        val count = videoRepository.getCountVideosPreDefinition(toVideoExercisePreDefinition.exercisePreDefinitionId!!)
        validateVideosLimit(count)
    }

    @Throws(VideoException::class)
    suspend fun validateVideo(toVideoExercisePreDefinition: TOVideoExercisePreDefinition, videoFile: File) {
        validateVideosLimit(toVideoExercisePreDefinition)
        validateVideoDuration(toVideoExercisePreDefinition.toVideo!!)
        validateVideoSize(toVideoExercisePreDefinition.toVideo!!, videoFile)
    }

    @Throws(VideoException::class)
    suspend fun validateVideoFile(videoFile: File) {
        val toVideoExerciseExecution = getTOVideoPreDefinition(videoFile, null)

        validateVideoDuration(toVideoExerciseExecution.toVideo!!)
        validateVideoSize(toVideoExerciseExecution.toVideo!!, videoFile)
    }
}