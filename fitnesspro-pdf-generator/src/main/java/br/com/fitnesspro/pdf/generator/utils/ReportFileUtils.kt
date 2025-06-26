package br.com.fitnesspro.pdf.generator.utils

import android.content.Context
import android.net.Uri
import br.com.fitnesspro.core.utils.FileUtils
import java.io.File

/**
 * Object utilitário para manipulação de arquivos de relatórios.
 */
object ReportFileUtils {
    const val REPORTS_FOLDER_NAME = "reports"
    const val DEFAULT_REPORT_EXTENSION = "pdf"

    fun createReportFile(context: Context, fileName: String? = null): File {
        val reportDir = File(context.getExternalFilesDir(REPORTS_FOLDER_NAME), "")
        if (!reportDir.exists()) reportDir.mkdirs()

        val nonNullFileName = if (!fileName.isNullOrEmpty()) {
            "$fileName.$DEFAULT_REPORT_EXTENSION"
        } else {
            getDefaultReportName()
        }

        return File(reportDir, nonNullFileName)
    }

    fun getDefaultReportName(): String {
        return "report_${System.currentTimeMillis()}.$DEFAULT_REPORT_EXTENSION"
    }

    fun getReportFileUri(context: Context, file: File): Uri {
        return FileUtils.getUriForFileUsingProvider(context, file)
    }

    fun deleteReportFile(file: File): Boolean {
        return file.delete()
    }
}