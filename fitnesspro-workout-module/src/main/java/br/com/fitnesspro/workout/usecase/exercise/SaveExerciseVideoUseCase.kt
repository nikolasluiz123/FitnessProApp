package br.com.fitnesspro.workout.usecase.exercise

import android.content.Context
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.core.media3.CompressionParams
import br.com.fitnesspro.core.media3.VideoCompressor
import br.com.fitnesspro.core.utils.FileUtils
import br.com.fitnesspro.core.utils.VideoUtils
import br.com.fitnesspro.core.validation.ValidationError
import br.com.fitnesspro.to.TOVideo
import br.com.fitnesspro.to.TOVideoExercise
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.repository.VideoRepository
import br.com.fitnesspro.workout.usecase.exercise.exception.VideoException
import java.io.File
import java.time.ZoneId

class SaveExerciseVideoUseCase(
    private val context: Context,
    private val videoRepository: VideoRepository
) {

    suspend operator fun invoke(exerciseId: String, videoFile: File): ValidationError? {
        return try {
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
            validateVideoDuration(toVideoExercise)
            validateVideoSize(toVideoExercise, videoFile)

            videoRepository.saveVideoExercise(toVideoExercise)

            null
        } catch (ex: VideoException) {
            ValidationError(ex.message!!)
        }
    }

    @Throws(VideoException::class)
    private suspend fun validateVideosLimit(toVideoExercise: TOVideoExercise) {
        val count = videoRepository.getCountVideosExercise(toVideoExercise.exerciseId!!)
        val maxVideosCount = 3

        if (count >= maxVideosCount) {
            throw VideoException(context.getString(R.string.exercise_video_message_max_video_count))
        }
    }

    @Throws(VideoException::class)
    private fun validateVideoDuration(toVideoExercise: TOVideoExercise) {
        if (toVideoExercise.toVideo?.seconds!! > 60) {
            throw VideoException(context.getString(R.string.exercise_video_message_max_video_duration))
        }
    }

    @Throws(VideoException::class)
    private suspend fun validateVideoSize(toVideoExercise: TOVideoExercise, videoFile: File) {
        val maxVideoSizeKB = 15000

        if (toVideoExercise.toVideo?.kbSize!! > maxVideoSizeKB) {
            val compressor = VideoCompressor(context)
            val params = CompressionParams(file = videoFile, targetMaxSizeMb = 7, resolutionHeight = 480)

            val compressedFile = compressor.compress(params)

            val sizeCompressedFile = FileUtils.getFileSizeInKB(compressedFile)

            if (sizeCompressedFile > maxVideoSizeKB) {
                throw VideoException(context.getString(R.string.exercise_video_message_max_video_size))
            } else {
                val (width, height) = VideoUtils.getVideoResolution(compressedFile)!!

                toVideoExercise.toVideo?.filePath = compressedFile.absolutePath
                toVideoExercise.toVideo?.kbSize = sizeCompressedFile
                toVideoExercise.toVideo?.width = width
                toVideoExercise.toVideo?.height = height
                toVideoExercise.toVideo?.seconds = VideoUtils.getVideoDurationInSeconds(compressedFile)
            }
        }
    }
}