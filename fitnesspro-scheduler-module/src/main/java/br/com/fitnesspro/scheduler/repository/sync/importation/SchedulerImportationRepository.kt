package br.com.fitnesspro.scheduler.repository.sync.importation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.scheduler.SchedulerWebClient
import br.com.fitnesspro.common.repository.sync.importation.common.AbstractImportationRepository
import br.com.fitnesspro.local.data.access.dao.SchedulerDAO
import br.com.fitnesspro.mappers.getScheduler
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.scheduler.Scheduler
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.shared.communication.dtos.scheduler.SchedulerDTO
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.CommonImportFilter
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse

class SchedulerImportationRepository(
    context: Context,
    private val schedulerDAO: SchedulerDAO,
    private val webClient: SchedulerWebClient,
): AbstractImportationRepository<SchedulerDTO, Scheduler, SchedulerDAO, CommonImportFilter>(context) {

    override fun getDescription(): String {
        return context.getString(R.string.scheduler_importation_descrition)
    }

    override fun getModule() = EnumSyncModule.SCHEDULER

    override suspend fun getImportationData(
        token: String,
        filter: CommonImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<SchedulerDTO> {
        return webClient.importSchedulers(token, filter, pageInfos)
    }

    override suspend fun hasEntityWithId(id: String): Boolean {
        return schedulerDAO.hasSchedulerWithId(id)
    }

    override fun getOperationDAO(): SchedulerDAO {
        return schedulerDAO
    }

    override suspend fun convertDTOToEntity(dto: SchedulerDTO): Scheduler {
        return dto.getScheduler()
    }
}