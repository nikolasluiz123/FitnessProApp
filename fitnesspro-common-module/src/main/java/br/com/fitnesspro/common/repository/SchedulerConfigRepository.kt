package br.com.fitnesspro.common.repository

import br.com.fitnesspor.service.data.access.webclient.scheduler.SchedulerWebClient
import br.com.fitnesspro.local.data.access.dao.SchedulerConfigDAO
import br.com.fitnesspro.local.data.access.dao.SchedulerDAO
import br.com.fitnesspro.model.scheduler.SchedulerConfig
import br.com.fitnesspro.to.TOSchedulerConfig
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class SchedulerConfigRepository(
    private val schedulerDAO: SchedulerDAO,
    private val schedulerConfigDAO: SchedulerConfigDAO,
    private val schedulerWebClient: SchedulerWebClient
) {
    suspend fun saveSchedulerConfig(toSchedulerConfig: TOSchedulerConfig) = withContext(IO) {
        val schedulerConfig = toSchedulerConfig.getSchedulerConfig()

        if (toSchedulerConfig.id == null) {
            schedulerConfigDAO.insert(schedulerConfig)
            toSchedulerConfig.id = schedulerConfig.id
        } else {
            schedulerConfigDAO.update(schedulerConfig)
        }

        schedulerWebClient.saveSchedulerConfig(schedulerConfig = schedulerConfig)
    }

    suspend fun saveSchedulerConfigBatch(toSchedulerConfigs: List<TOSchedulerConfig>) = withContext(IO) {
        val schedulerConfigs = toSchedulerConfigs.map { it.getSchedulerConfig() }

        if (toSchedulerConfigs.first().id == null) {
            schedulerConfigDAO.insertBatch(schedulerConfigs)
        } else {
            schedulerConfigDAO.updateBatch(schedulerConfigs)
        }
    }

    suspend fun getTOSchedulerConfigByPersonId(personId: String): TOSchedulerConfig? = withContext(IO) {
        schedulerConfigDAO.findSchedulerConfigByPersonId(personId).getTOSchedulerConfig()
    }

    private fun SchedulerConfig?.getTOSchedulerConfig(): TOSchedulerConfig? {
        return this?.run {
            TOSchedulerConfig(
                id = id,
                personId = personId,
                alarm = alarm,
                notification = notification,
                minScheduleDensity = minScheduleDensity,
                maxScheduleDensity = maxScheduleDensity,
            )
        }
    }

    private suspend fun TOSchedulerConfig.getSchedulerConfig(): SchedulerConfig {
        return if (id == null) {
            SchedulerConfig(
                personId = personId,
                alarm = alarm,
                notification = notification,
                minScheduleDensity = minScheduleDensity!!,
                maxScheduleDensity = maxScheduleDensity!!
            )
        } else {
            schedulerConfigDAO.findSchedulerConfigById(id!!).copy(
                alarm = alarm,
                notification = notification,
                minScheduleDensity = minScheduleDensity!!,
                maxScheduleDensity = maxScheduleDensity!!
            )
        }
    }
}