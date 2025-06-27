package br.com.fitnesspro.scheduler.repository.sync.importation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.general.ReportWebClient
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.sync.importation.common.AbstractImportationRepository
import br.com.fitnesspro.local.data.access.dao.ReportDAO
import br.com.fitnesspro.mappers.getReport
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.general.report.Report
import br.com.fitnesspro.shared.communication.dtos.general.ReportDTO
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.SchedulerReportImportFilter
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import java.time.LocalDateTime

class ReportFromSchedulerImportationRepository(
    context: Context,
    private val webClient: ReportWebClient,
    private val reportDAO: ReportDAO,
    private val personRepository: PersonRepository
): AbstractImportationRepository<ReportDTO, Report, ReportDAO, SchedulerReportImportFilter>(context) {

    override suspend fun getImportFilter(lastUpdateDate: LocalDateTime?): SchedulerReportImportFilter {
        val baseFilter = super.getImportFilter(lastUpdateDate)
        val authenticatedPersonId = personRepository.getAuthenticatedTOPerson()?.id!!

        return SchedulerReportImportFilter(
            lastUpdateDate = baseFilter.lastUpdateDate,
            personId = authenticatedPersonId
        )
    }

    override suspend fun getImportationData(
        token: String,
        filter: SchedulerReportImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<ReportDTO> {
        return webClient.importReportsFromScheduler(token, filter, pageInfos)
    }

    override suspend fun hasEntityWithId(id: String): Boolean {
        return reportDAO.hasEntityWithId(id)
    }

    override suspend fun convertDTOToEntity(dto: ReportDTO): Report {
        return dto.getReport()
    }

    override fun getOperationDAO(): ReportDAO {
        return reportDAO
    }

    override fun getDescription(): String {
        return context.getString(R.string.reports_from_scheduler_importation_descrition)
    }

    override fun getModule() = EnumSyncModule.SCHEDULER
}