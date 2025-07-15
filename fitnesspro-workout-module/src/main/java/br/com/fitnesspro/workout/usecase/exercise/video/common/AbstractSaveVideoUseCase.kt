package br.com.fitnesspro.workout.usecase.exercise.video.common

import android.content.Context
import br.com.fitnesspro.core.media3.CompressionParams
import br.com.fitnesspro.core.media3.VideoCompressor
import br.com.fitnesspro.core.utils.FileUtils
import br.com.fitnesspro.core.utils.VideoUtils
import br.com.fitnesspro.to.TOVideo
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.usecase.exercise.exception.VideoException
import java.io.File

abstract class AbstractSaveVideoUseCase(
    protected val context: Context,
) {

    @Throws(VideoException::class)
    fun validateVideosLimit(videoCount: Int) {
        val maxVideosCount = 3

        if (videoCount >= maxVideosCount) {
            throw VideoException(context.getString(R.string.exercise_video_message_max_video_count))
        }
    }

    @Throws(VideoException::class)
    protected fun validateVideoDuration(toVideo: TOVideo) {
        if (toVideo.seconds!! > 60) {
            throw VideoException(context.getString(R.string.exercise_video_message_max_video_duration))
        }
    }

    @Throws(VideoException::class)
    protected suspend fun validateVideoSize(toVideo: TOVideo, videoFile: File) {
        val maxVideoSizeKB = 15000

        if (toVideo.kbSize!! > maxVideoSizeKB) {
            val compressor = VideoCompressor(context)
            val params =
                CompressionParams(file = videoFile, targetMaxSizeMb = 7, resolutionHeight = 480)

            val compressedFile = compressor.compress(params)

            val sizeCompressedFile = FileUtils.getFileSizeInKB(compressedFile)

            if (sizeCompressedFile > maxVideoSizeKB) {
                throw VideoException(context.getString(R.string.exercise_video_message_max_video_size))
            } else {
                val (width, height) = VideoUtils.getVideoResolution(compressedFile)!!

                toVideo.filePath = compressedFile.absolutePath
                toVideo.kbSize = sizeCompressedFile
                toVideo.width = width
                toVideo.height = height
                toVideo.seconds = VideoUtils.getVideoDurationInSeconds(compressedFile)
            }
        }
    }
}