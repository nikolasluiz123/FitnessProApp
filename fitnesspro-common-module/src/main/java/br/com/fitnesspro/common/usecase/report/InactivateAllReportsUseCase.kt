package br.com.fitnesspro.common.usecase.report

import android.content.Context
import br.com.fitnesspro.common.repository.ReportRepository
import br.com.fitnesspro.mappers.getReport
import br.com.fitnesspro.model.enums.EnumReportContext
import br.com.fitnesspro.pdf.generator.utils.ReportFileUtils
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class InactivateAllReportsUseCase(
    private val reportRepository: ReportRepository,
    private val context: Context
) {

    suspend operator fun invoke(reportContext: EnumReportContext) = withContext(IO) {
        reportRepository.runInTransaction {
            val reports = reportRepository.getListReports(reportContext).map { it.getReport() }
            reportRepository.inactivateAllReports(reportContext, reports)

            reports.forEach {
                ReportFileUtils.deleteReportFile(context, it.filePath!!)
            }
        }
    }

}