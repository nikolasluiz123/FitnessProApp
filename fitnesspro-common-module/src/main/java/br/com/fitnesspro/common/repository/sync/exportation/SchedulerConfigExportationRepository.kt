package br.com.fitnesspro.common.repository.sync.exportation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.scheduler.SchedulerWebClient
import br.com.fitnesspro.common.repository.sync.exportation.common.AbstractExportationRepository
import br.com.fitnesspro.local.data.access.dao.SchedulerConfigDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.scheduler.SchedulerConfig
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse

class SchedulerConfigExportationRepository(
    context: Context,
    private val schedulerConfigDAO: SchedulerConfigDAO,
    private val schedulerWebClient: SchedulerWebClient
): AbstractExportationRepository<SchedulerConfig, SchedulerConfigDAO>(context) {

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
    ): ExportationServiceResponse {
        return schedulerWebClient.saveSchedulerConfigBatch(
            token = token,
            schedulerConfigList = modelList
        )
    }

}