package br.com.fitnesspro.common.repository

import br.com.fitnesspro.local.data.access.dao.SchedulerDAO
import br.com.fitnesspro.model.scheduler.SchedulerConfig
import br.com.fitnesspro.to.TOSchedulerConfig
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class SchedulerConfigRepository(
    private val schedulerDAO: SchedulerDAO
) {
    suspend fun saveSchedulerConfig(toSchedulerConfig: TOSchedulerConfig) = withContext(IO) {
        val schedulerConfig = toSchedulerConfig.getSchedulerConfig()
        schedulerDAO.saveConfig(schedulerConfig)
    }

    suspend fun saveSchedulerConfigBatch(toSchedulerConfigs: List<TOSchedulerConfig>) = withContext(IO) {
        val schedulerConfigs = toSchedulerConfigs.map { it.getSchedulerConfig() }
        schedulerDAO.saveConfigBatch(schedulerConfigs)
    }

    suspend fun getTOSchedulerConfigByPersonId(personId: String): TOSchedulerConfig? = withContext(IO) {
        schedulerDAO.findSchedulerConfigByPersonId(personId).getTOSchedulerConfig()
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
                startBreakTime = startBreakTime,
                endBreakTime = endBreakTime,
                startWorkTime = startWorkTime,
                endWorkTime = endWorkTime
            )
        }
    }

    private suspend fun TOSchedulerConfig.getSchedulerConfig(): SchedulerConfig {
        return if (id == null) {
            val model = SchedulerConfig(
                personId = personId,
                alarm = alarm,
                notification = notification,
                minScheduleDensity = minScheduleDensity!!,
                maxScheduleDensity = maxScheduleDensity!!
            )

            this.id = model.id

            model
        } else {
            schedulerDAO.findSchedulerConfigById(id!!).copy(
                alarm = alarm,
                notification = notification,
                minScheduleDensity = minScheduleDensity!!,
                maxScheduleDensity = maxScheduleDensity!!
            )
        }
    }
}