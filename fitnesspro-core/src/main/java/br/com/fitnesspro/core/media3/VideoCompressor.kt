package br.com.fitnesspro.core.media3

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.ScaleAndRotateTransformation
import androidx.media3.transformer.DefaultEncoderFactory
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.Effects
import androidx.media3.transformer.Transformer
import androidx.media3.transformer.VideoEncoderSettings
import br.com.fitnesspro.core.utils.FileUtils
import br.com.fitnesspro.core.utils.VideoUtils
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File

@OptIn(UnstableApi::class)
class VideoCompressor(private val context: Context) {

    suspend fun compress(params: CompressionParams): File = suspendCancellableCoroutine { continuation ->
        val originalFileName = FileUtils.getFileNameWithExtensionFromFilePath(params.file.absolutePath)
        val compressedFileName = VideoUtils.getCompressedVideoFileName(originalFileName)
        val outputFile = VideoUtils.createVideoFile(context, compressedFileName)

        val durationInSeconds = VideoUtils.getVideoDurationInSeconds(params.file)
        val targetBitrate = ((params.targetMaxSizeMb * 1024 * 1024 * 8) / durationInSeconds).toInt()
        val mediaItem = MediaItem.fromUri(Uri.fromFile(params.file))
        val editedMediaItem = getEditedMediaItem(params, mediaItem)

        val encoderFactory = getEncoderFactory(targetBitrate)

        val transformerBuilder = Transformer.Builder(context)
            .setEncoderFactory(encoderFactory)
            .setVideoMimeType(MimeTypes.VIDEO_H264)
            .setAudioMimeType(MimeTypes.AUDIO_AAC)
            .addListener(DefaultVideoCompressorTransformerListener(continuation, outputFile))

        val transformer = transformerBuilder.build()

        transformer.start(editedMediaItem, outputFile.absolutePath)

        continuation.invokeOnCancellation {
            transformer.cancel()
        }
    }

    private fun getEditedMediaItem(params: CompressionParams, mediaItem: MediaItem): EditedMediaItem {
        return if (params.resolutionHeight != null) {
            val originalResolution = VideoUtils.getVideoResolution(params.file)

            if (originalResolution != null && originalResolution.second > params.resolutionHeight) {
                val scale = params.resolutionHeight.toFloat() / originalResolution.second.toFloat()

                val scaleEffect = ScaleAndRotateTransformation.Builder()
                    .setScale(scale, scale)
                    .build()

                val effects = Effects(listOf(), listOf(scaleEffect))

                EditedMediaItem.Builder(mediaItem).setEffects(effects).build()
            } else {
                EditedMediaItem.Builder(mediaItem).build()
            }
        } else {
            EditedMediaItem.Builder(mediaItem).build()
        }
    }

    private fun getEncoderFactory(targetBitrate: Int): DefaultEncoderFactory {
        val videoEncoderSettings = VideoEncoderSettings.Builder()
            .setBitrate(targetBitrate)
            .build()

        return DefaultEncoderFactory.Builder(context)
            .setRequestedVideoEncoderSettings(videoEncoderSettings)
            .build()
    }
}