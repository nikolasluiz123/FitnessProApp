package br.com.fitnesspro.core.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.content.FileProvider
import java.io.File

object FileUtils {

    fun getFileProviderAuthority(context: Context): String {
        return "${context.packageName}.fileprovider"
    }

    fun getUriForFileUsingProvider(context: Context, file: File): Uri {
        val authority = getFileProviderAuthority(context)
        return FileProvider.getUriForFile(context, authority, file)
    }

    fun getFileNameWithExtensionFromFilePath(filePath: String): String {
        return filePath.substringAfterLast("/")
    }

    fun getFileNameWithoutExtension(fileName: String): String {
        return fileName.substringBeforeLast(".")
    }

    fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var name: String? = null
        val returnCursor = context.contentResolver.query(uri, null, null, null, null)

        returnCursor?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && cursor.moveToFirst()) {
                name = cursor.getString(nameIndex)
            }
        }

        return name
    }

    fun getFileSizeInKB(file: File): Long {
        return file.length() / 1024
    }

    fun getFileExists(filePath: String): Boolean {
        return File(filePath).exists()
    }
}