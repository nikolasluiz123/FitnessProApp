package br.com.fitnesspro.common.repository

import android.content.Context
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.SchedulerConfigDAO
import br.com.fitnesspro.mappers.getSchedulerConfig
import br.com.fitnesspro.mappers.getTOSchedulerConfig
import br.com.fitnesspro.model.scheduler.SchedulerConfig
import br.com.fitnesspro.to.TOSchedulerConfig

class SchedulerConfigRepository(
    context: Context,
    private val schedulerConfigDAO: SchedulerConfigDAO,
): FitnessProRepository(context) {

    suspend fun saveSchedulerConfig(toSchedulerConfig: TOSchedulerConfig) {
        val schedulerConfig = toSchedulerConfig.getSchedulerConfig()
        saveSchedulerConfigLocally(toSchedulerConfig, schedulerConfig)
    }

    private suspend fun saveSchedulerConfigLocally(
        toSchedulerConfig: TOSchedulerConfig,
        schedulerConfig: SchedulerConfig
    ) {
        if (toSchedulerConfig.id == null) {
            schedulerConfigDAO.insert(schedulerConfig)
            toSchedulerConfig.id = schedulerConfig.id
        } else {
            schedulerConfigDAO.update(schedulerConfig, true)
        }
    }

    suspend fun getTOSchedulerConfigByPersonId(personId: String): TOSchedulerConfig? {
         return schedulerConfigDAO.findSchedulerConfigByPersonId(personId)?.let(SchedulerConfig::getTOSchedulerConfig)
    }
}