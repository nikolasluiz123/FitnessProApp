package br.com.fitnesspro.scheduler.repository.sync.importation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.general.ReportWebClient
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.sync.importation.common.AbstractImportationRepository
import br.com.fitnesspro.local.data.access.dao.SchedulerReportDAO
import br.com.fitnesspro.mappers.getSchedulerReport
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.general.report.SchedulerReport
import br.com.fitnesspro.shared.communication.dtos.general.SchedulerReportDTO
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.SchedulerReportImportFilter
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import java.time.LocalDateTime

class SchedulerReportImportationRepository(
    context: Context,
    private val webClient: ReportWebClient,
    private val schedulerReportDAO: SchedulerReportDAO,
    private val personRepository: PersonRepository
): AbstractImportationRepository<SchedulerReportDTO, SchedulerReport, SchedulerReportDAO, SchedulerReportImportFilter>(context) {

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
    ): ImportationServiceResponse<SchedulerReportDTO> {
        return webClient.importSchedulerReports(token, filter, pageInfos)
    }

    override suspend fun hasEntityWithId(id: String): Boolean {
        return schedulerReportDAO.hasEntityWithId(id)
    }

    override suspend fun convertDTOToEntity(dto: SchedulerReportDTO): SchedulerReport {
        return dto.getSchedulerReport()
    }

    override fun getOperationDAO(): SchedulerReportDAO {
        return schedulerReportDAO
    }

    override fun getDescription(): String {
        return context.getString(R.string.scheduler_report_importation_descrition)
    }

    override fun getModule() = EnumSyncModule.SCHEDULER
}