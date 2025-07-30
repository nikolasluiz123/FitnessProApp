package br.com.fitnesspro.common.usecase.report

import br.com.fitnesspro.common.repository.ReportRepository
import br.com.fitnesspro.mappers.getReport
import br.com.fitnesspro.model.enums.EnumReportContext

class InactivateAllReportsUseCase(
    private val reportRepository: ReportRepository
) {

    suspend operator fun invoke(reportContext: EnumReportContext) {
        reportRepository.runInTransaction {
            val reports = reportRepository.getListReports(reportContext).map { it.getReport() }
            reportRepository.inactivateAllReports(reportContext, reports)
        }
    }

}