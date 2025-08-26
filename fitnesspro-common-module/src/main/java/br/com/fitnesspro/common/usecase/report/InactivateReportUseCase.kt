package br.com.fitnesspro.common.usecase.report

import android.content.Context
import br.com.fitnesspro.common.repository.ReportRepository
import br.com.fitnesspro.model.enums.EnumReportContext
import br.com.fitnesspro.pdf.generator.utils.ReportFileUtils
import br.com.fitnesspro.to.TOReport
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class InactivateReportUseCase(
    private val reportRepository: ReportRepository,
    private val context: Context
) {
    suspend operator fun invoke(reportContext: EnumReportContext, toReport: TOReport) = withContext(IO) {
        reportRepository.runInTransaction {
            reportRepository.inactivateReport(reportContext,toReport.id!!)
            ReportFileUtils.deleteReportFile(context, toReport.filePath!!)
        }
    }

}