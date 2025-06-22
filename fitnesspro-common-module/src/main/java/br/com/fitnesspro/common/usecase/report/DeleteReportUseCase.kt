package br.com.fitnesspro.common.usecase.report

import br.com.fitnesspro.common.repository.ReportRepository
import br.com.fitnesspro.model.enums.EnumReportContext
import br.com.fitnesspro.to.TOReport

class DeleteReportUseCase(
    private val context: EnumReportContext,
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(toReport: TOReport, reportContext: EnumReportContext) {
    }

}