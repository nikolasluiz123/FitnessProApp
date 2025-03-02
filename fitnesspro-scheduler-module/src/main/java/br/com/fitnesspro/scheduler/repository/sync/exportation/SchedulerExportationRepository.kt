package br.com.fitnesspro.scheduler.repository.sync.exportation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.scheduler.SchedulerWebClient
import br.com.fitnesspro.common.repository.sync.exportation.common.AbstractExportationRepository
import br.com.fitnesspro.local.data.access.dao.SchedulerDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.CommonExportFilter
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.enums.EnumUserType.NUTRITIONIST
import br.com.fitnesspro.model.enums.EnumUserType.PERSONAL_TRAINER
import br.com.fitnesspro.model.scheduler.Scheduler
import br.com.fitnesspro.model.sync.EnumSyncModule
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumSchedulerType.SUGGESTION
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumSchedulerType.UNIQUE
import br.com.fitnesspro.shared.communication.dtos.scheduler.SchedulerDTO
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse

class SchedulerExportationRepository(
    context: Context,
    private val schedulerDAO: SchedulerDAO,
    private val schedulerWebClient: SchedulerWebClient
): AbstractExportationRepository<SchedulerDTO, Scheduler, SchedulerDAO>(context) {

    override suspend fun getExportationData(
        filter: CommonExportFilter,
        pageInfos: ExportPageInfos
    ): List<Scheduler> {
        return schedulerDAO.getExportationData(filter, pageInfos)
    }

    override fun getOperationDAO(): SchedulerDAO {
        return schedulerDAO
    }

    override suspend fun callExportationService(
        modelList: List<Scheduler>,
        token: String
    ): PersistenceServiceResponse {
        val userType = getAuthenticatedUser()?.type!!
        val schedulerType = if (userType in listOf(PERSONAL_TRAINER, NUTRITIONIST)) UNIQUE else SUGGESTION

        return schedulerWebClient.saveSchedulerBatch(token, modelList, schedulerType.name)
    }

    override fun getDescription(): String {
        return context.getString(R.string.scheduler_exportation_description)
    }

    override fun getModule() = EnumSyncModule.SCHEDULER
}