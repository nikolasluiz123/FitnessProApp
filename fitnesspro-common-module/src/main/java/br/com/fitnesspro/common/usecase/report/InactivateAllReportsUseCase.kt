package br.com.fitnesspro.common.usecase.report

import br.com.fitnesspro.common.repository.ReportRepository
import br.com.fitnesspro.mappers.getReport
import br.com.fitnesspro.model.enums.EnumReportContext
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class InactivateAllReportsUseCase(
    private val reportRepository: ReportRepository
) {

    suspend operator fun invoke(reportContext: EnumReportContext) = withContext(IO) {
        reportRepository.runInTransaction {
            val reports = reportRepository.getListReports(reportContext).map { it.getReport() }
            reportRepository.inactivateAllReports(reportContext, reports)
        }
    }

}