package br.com.fitnesspro.pdf.generator.report

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.net.Uri
import br.com.fitnesspro.pdf.generator.enums.EnumPageSize
import br.com.fitnesspro.pdf.generator.utils.ReportFileUtils
import java.io.File
import java.io.FileOutputStream

/**
 * Implementação responsável por gerar o relatório PDF.
 *
 * Essa classe deve conter todas as variações de geração dos relatórios, as mais comuns são a geração
 * de um [File], mas também é possível utilizar uma [Uri] para compartilhamento.
 *
 * @param context Contexto da aplicação.
 * @param report Instância do relatório a ser gerado.
 *
 * @author Nikolas Luiz Schmitt
 */
class PDFReportGenerator<FILTER : Any>(
    private val context: Context,
    private val report: AbstractPDFReport<FILTER>
) {

    suspend fun generatePdfFile(pageSize: EnumPageSize, fileName: String? = null): File {
        val document = PdfDocument()

        report.generate(document, pageSize)

        val file = ReportFileUtils.createReportFile(context, fileName)
        document.writeTo(FileOutputStream(file))
        document.close()

        return file
    }

    suspend fun generatePdfUri(pageSize: EnumPageSize, fileName: String? = null): Uri {
        val file = generatePdfFile(pageSize, fileName)
        return ReportFileUtils.getReportFileUri(context, file)
    }
}