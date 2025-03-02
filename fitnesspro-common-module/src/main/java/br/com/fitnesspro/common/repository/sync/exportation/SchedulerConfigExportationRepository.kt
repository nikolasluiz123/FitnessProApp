package br.com.fitnesspro.common.repository.sync.exportation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.scheduler.SchedulerWebClient
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.sync.exportation.common.AbstractExportationRepository
import br.com.fitnesspro.local.data.access.dao.SchedulerConfigDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.scheduler.SchedulerConfig
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.shared.communication.dtos.scheduler.SchedulerConfigDTO
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse

class SchedulerConfigExportationRepository(
    context: Context,
    private val schedulerConfigDAO: SchedulerConfigDAO,
    private val schedulerWebClient: SchedulerWebClient
): AbstractExportationRepository<SchedulerConfigDTO, SchedulerConfig, SchedulerConfigDAO>(context) {

    override suspend fun getExportationData(
        pageInfos: ExportPageInfos
    ): List<SchedulerConfig> {
        return schedulerConfigDAO.getExportationData(pageInfos)
    }

    override fun getOperationDAO(): SchedulerConfigDAO {
        return schedulerConfigDAO
    }

    override suspend fun callExportationService(
        modelList: List<SchedulerConfig>,
        token: String
    ): PersistenceServiceResponse {
        return schedulerWebClient.saveSchedulerConfigBatch(
            token = token,
            schedulerConfigList = modelList
        )
    }

    override fun getDescription(): String {
        return context.getString(R.string.sync_module_scheduler_config)
    }

    override fun getModule() = EnumSyncModule.SCHEDULER
}