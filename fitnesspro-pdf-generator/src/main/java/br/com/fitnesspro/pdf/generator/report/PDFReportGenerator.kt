package br.com.fitnesspro.pdf.generator.report

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

class PDFReportGenerator<FILTER : Any>(
    private val context: Context,
    private val report: AbstractPDFReport<FILTER>
) {

    /**
     * Gera o PDF, salva no arquivo e retorna o File.
     */
    suspend fun generatePdfFile(
        fileName: String = "relatorio.pdf"
    ): File {
        val document = PdfDocument()

        // Manda o report desenhar no document
        report.generate(document)

        // Cria e salva o arquivo
        val file = ReportFileUtils.createReportFile(context, fileName)
        document.writeTo(FileOutputStream(file))
        document.close()

        return file
    }

    /**
     * Gera o PDF, salva no arquivo e retorna um Uri usando FileProvider.
     */
    suspend fun generatePdfUri(
        fileName: String = "relatorio.pdf"
    ): Uri {
        val file = generatePdfFile(fileName)
        return ReportFileUtils.getReportFileUri(context, file)
    }
}