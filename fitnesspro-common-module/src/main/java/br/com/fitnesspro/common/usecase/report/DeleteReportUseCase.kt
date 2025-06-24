package br.com.fitnesspro.common.usecase.report

import android.content.Context
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.ReportRepository
import br.com.fitnesspro.core.exceptions.NoLoggingException
import br.com.fitnesspro.core.extensions.isNetworkAvailable
import br.com.fitnesspro.to.TOReport

class DeleteReportUseCase(
    private val context: Context,
    private val reportRepository: ReportRepository
) {
    suspend operator fun invoke(toReport: TOReport) {
        if (!context.isNetworkAvailable()) {
            throw NoLoggingException(context.getString(R.string.network_required_delete_report))
        }

        reportRepository.deleteSchedulerReport(toReport.id!!)
    }

}