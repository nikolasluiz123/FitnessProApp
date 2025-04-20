package br.com.fitnesspro.common.repository.sync.importation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.scheduler.SchedulerWebClient
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.sync.importation.common.AbstractImportationRepository
import br.com.fitnesspro.local.data.access.dao.SchedulerConfigDAO
import br.com.fitnesspro.mappers.SchedulerModelMapper
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.scheduler.SchedulerConfig
import br.com.fitnesspro.shared.communication.dtos.scheduler.SchedulerConfigDTO
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.CommonImportFilter
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse

class SchedulerConfigImportationRepository(
    context: Context,
    private val schedulerConfigDAO: SchedulerConfigDAO,
    private val webClient: SchedulerWebClient,
    private val schedulerModelMapper: SchedulerModelMapper
): AbstractImportationRepository<SchedulerConfigDTO, SchedulerConfig, SchedulerConfigDAO>(context) {

    override fun getDescription(): String {
        return context.getString(R.string.scheduler_config_importation_descrition)
    }

    override fun getModule() = EnumSyncModule.SCHEDULER

    override suspend fun getImportationData(
        token: String,
        filter: CommonImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<SchedulerConfigDTO> {
        return webClient.importSchedulerConfigs(token, filter, pageInfos)
    }

    override suspend fun hasEntityWithId(id: String): Boolean {
        return schedulerConfigDAO.hasSchedulerConfigWithId(id)
    }

    override fun getOperationDAO(): SchedulerConfigDAO {
        return schedulerConfigDAO
    }

    override suspend fun convertDTOToEntity(dto: SchedulerConfigDTO): SchedulerConfig {
        return schedulerModelMapper.getSchedulerConfig(dto).copy(
            transmissionState = EnumTransmissionState.TRANSMITTED
        )
    }
}