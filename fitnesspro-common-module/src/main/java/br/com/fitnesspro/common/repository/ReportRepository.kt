package br.com.fitnesspro.common.repository

import android.content.Context
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.ReportDAO
import br.com.fitnesspro.model.enums.EnumReportContext
import br.com.fitnesspro.to.TOReport

class ReportRepository(
    context: Context,
    private val reportDAO: ReportDAO,
    private val personRepository: PersonRepository
): FitnessProRepository(context) {

    suspend fun getListReports(context: EnumReportContext, quickFilter: String? = null): List<TOReport> {
        val authenticatedPersonId = personRepository.getAuthenticatedTOPerson()?.id!!

        return reportDAO.getListGeneratedReports(context, authenticatedPersonId, quickFilter)
    }

    suspend fun deleteSchedulerReport(reportId: String) {

    }
}