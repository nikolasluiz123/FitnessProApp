package br.com.fitnesspro.pdf.generator.utils

import android.content.Context
import android.net.Uri
import android.os.FileUtils
import java.io.File

object ReportFileUtils {

    fun createReportFile(context: Context, fileName: String): File {
        val directory = File(context.getExternalFilesDir(null), "reports")
        if (!directory.exists()) directory.mkdirs()

        return File(directory, fileName)
    }

    fun getReportFileUri(context: Context, file: File): Uri {
        return FileUtils.getUriForFileUsingProvider(context, file)
    }

    fun deleteReportFile(file: File): Boolean {
        return file.delete()
    }
}