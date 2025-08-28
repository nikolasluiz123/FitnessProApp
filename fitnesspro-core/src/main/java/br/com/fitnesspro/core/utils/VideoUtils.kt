package br.com.fitnesspro.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import br.com.fitnesspro.core.R
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

object VideoUtils {
    const val DEFAULT_VIDEO_EXTENSION = "mp4"

    fun getDefaultVideoName(): String {
        return "video_${System.currentTimeMillis()}.$DEFAULT_VIDEO_EXTENSION"
    }

    fun createVideoFile(context: Context, fileName: String? = null): File {
        val videoDir = File(context.getExternalFilesDir(null), "")
        if (!videoDir.exists()) videoDir.mkdirs()

        return File(videoDir, fileName ?: getDefaultVideoName())
    }

    fun getCompressedVideoFileName(videoFileName: String): String {
        val nameWithoutExtension = FileUtils.getFileNameWithoutExtension(videoFileName)

        return "${nameWithoutExtension}_compressed.${DEFAULT_VIDEO_EXTENSION}"
    }

    suspend fun createVideoFileFromUri(context: Context, uri: Uri): File? = withContext(IO) {
        val fileName = FileUtils.getFileNameFromUri(context, uri)!!
        val videoFile = createVideoFile(context, fileName)

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(videoFile).use { outputStream ->
                val buffer = ByteArray(1024)
                var length: Int

                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }

                outputStream.flush()
            }
        }

        videoFile
    }

    suspend fun generateVideoThumbnail(filePath: String, timeUs: Long = 1_000_000): Bitmap? = withContext(IO) {
        if (FileUtils.getFileExists(filePath)) {
            val retriever = MediaMetadataRetriever()

            try {
                retriever.setDataSource(filePath)
                val bitmap = retriever.getFrameAtTime(timeUs, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)

                bitmap
            } finally {
                retriever.release()
            }
        } else {
            null
        }
    }

    fun getVideoDurationInSeconds(file: File): Long {
        val retriever = MediaMetadataRetriever()

        return try {
            retriever.setDataSource(file.absolutePath)
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val durationMs = durationStr?.toLongOrNull() ?: 0L
            durationMs / 1000
        } finally {
            retriever.release()
        }
    }

    fun getVideoResolution(file: File): Pair<Int, Int>? {
        val retriever = MediaMetadataRetriever()

        return try {
            retriever.setDataSource(file.absolutePath)

            val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toIntOrNull()
            val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull()

            if (width != null && height != null) Pair(width, height) else null
        } finally {
            retriever.release()
        }
    }

    fun deleteVideoFile(context: Context, filePath: String) {
        val successDeleteFile = FileUtils.deleteFile(filePath)

        if (!successDeleteFile) {
            throw FileNotFoundException(context.getString(R.string.video_file_not_found_message))
        }
    }
}