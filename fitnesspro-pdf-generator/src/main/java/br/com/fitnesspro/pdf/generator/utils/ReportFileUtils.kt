package br.com.fitnesspro.pdf.generator.utils

import android.content.Context
import android.net.Uri
import br.com.fitnesspro.core.utils.FileUtils
import br.com.fitnesspro.pdf.generator.R
import java.io.File
import java.io.FileNotFoundException

/**
 * Object utilitário para manipulação de arquivos de relatórios.
 */
object ReportFileUtils {
    const val DEFAULT_REPORT_EXTENSION = "pdf"

    fun createReportFile(context: Context, fileName: String? = null): File {
        val reportDir = File(context.getExternalFilesDir(null), "")
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

    fun deleteReportFile(context: Context, filePath: String) {
        val successDeleteFile = FileUtils.deleteFile(filePath)

        if (!successDeleteFile) {
            throw FileNotFoundException(context.getString(R.string.report_file_not_found_message))
        }
    }
}