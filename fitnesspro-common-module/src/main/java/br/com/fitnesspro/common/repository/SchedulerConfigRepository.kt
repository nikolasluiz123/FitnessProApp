package br.com.fitnesspro.common.repository

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.scheduler.SchedulerWebClient
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.SchedulerConfigDAO
import br.com.fitnesspro.mappers.getSchedulerConfig
import br.com.fitnesspro.mappers.getTOSchedulerConfig
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.scheduler.SchedulerConfig
import br.com.fitnesspro.to.TOSchedulerConfig
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class SchedulerConfigRepository(
    context: Context,
    private val schedulerConfigDAO: SchedulerConfigDAO,
    private val schedulerWebClient: SchedulerWebClient,
): FitnessProRepository(context) {
    suspend fun saveSchedulerConfig(toSchedulerConfig: TOSchedulerConfig) = withContext(IO) {
        val schedulerConfig = toSchedulerConfig.getSchedulerConfig()

        saveSchedulerConfigLocally(toSchedulerConfig, schedulerConfig)
        saveSchedulerConfigRemote(schedulerConfig)
    }

    private suspend fun saveSchedulerConfigLocally(
        toSchedulerConfig: TOSchedulerConfig,
        schedulerConfig: SchedulerConfig
    ) {
        if (toSchedulerConfig.id == null) {
            schedulerConfigDAO.insert(schedulerConfig)
            toSchedulerConfig.id = schedulerConfig.id
        } else {
            schedulerConfigDAO.update(schedulerConfig)
        }
    }

    private suspend fun saveSchedulerConfigRemote(schedulerConfig: SchedulerConfig) {
        val response = schedulerWebClient.saveSchedulerConfig(
            token = getValidToken(),
            schedulerConfig = schedulerConfig
        )

        if (response.success) {
            schedulerConfigDAO.update(schedulerConfig.copy(transmissionState = EnumTransmissionState.TRANSMITTED))
        }
    }

    suspend fun getTOSchedulerConfigByPersonId(personId: String): TOSchedulerConfig? = withContext(IO) {
         schedulerConfigDAO.findSchedulerConfigByPersonId(personId)?.let(SchedulerConfig::getTOSchedulerConfig)
    }
}