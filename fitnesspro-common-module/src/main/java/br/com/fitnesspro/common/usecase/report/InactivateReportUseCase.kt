package br.com.fitnesspro.common.usecase.report

import br.com.fitnesspro.common.repository.ReportRepository
import br.com.fitnesspro.model.enums.EnumReportContext
import br.com.fitnesspro.to.TOReport
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class InactivateReportUseCase(
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(reportContext: EnumReportContext, toReport: TOReport) = withContext(IO) {
        reportRepository.runInTransaction {
            reportRepository.inactivateReport(reportContext,toReport.id!!)
        }
    }

}