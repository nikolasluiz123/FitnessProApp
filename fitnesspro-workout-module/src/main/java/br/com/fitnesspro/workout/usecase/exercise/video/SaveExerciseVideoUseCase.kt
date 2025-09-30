package br.com.fitnesspro.workout.usecase.exercise.video

import android.content.Context
import br.com.android.ui.compose.components.fields.validation.ValidationError
import br.com.core.android.utils.media.FileUtils
import br.com.core.android.utils.media.VideoUtils
import br.com.core.utils.extensions.dateTimeNow
import br.com.fitnesspro.to.TOVideo
import br.com.fitnesspro.to.TOVideoExercise
import br.com.fitnesspro.workout.repository.VideoRepository
import br.com.fitnesspro.workout.usecase.exercise.exception.VideoException
import br.com.fitnesspro.workout.usecase.exercise.video.common.AbstractSaveVideoUseCase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.io.File
import java.time.ZoneId

class SaveExerciseVideoUseCase(
    context: Context,
    private val videoRepository: VideoRepository
): AbstractSaveVideoUseCase(context) {

    suspend operator fun invoke(exerciseId: String, videoFile: File): ValidationError? = withContext(IO) {
        try {
            val (width, height) = VideoUtils.getVideoResolution(videoFile)!!

            val toVideoExercise = TOVideoExercise(
                exerciseId = exerciseId,
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

            validateVideosLimit(toVideoExercise)
            validateVideoDuration(toVideoExercise.toVideo!!)
            validateVideoSize(toVideoExercise.toVideo!!, videoFile)

            videoRepository.saveVideoExercise(toVideoExercise)

            null
        } catch (ex: VideoException) {
            ValidationError(ex.message!!)
        }
    }

    @Throws(VideoException::class)
    private suspend fun validateVideosLimit(toVideoExercise: TOVideoExercise) {
        val count = videoRepository.getCountVideosExercise(toVideoExercise.exerciseId!!)
        validateVideosLimit(count)
    }
}