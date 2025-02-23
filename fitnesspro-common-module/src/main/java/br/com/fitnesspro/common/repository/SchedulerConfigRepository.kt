package br.com.fitnesspro.common.repository

import br.com.fitnesspor.service.data.access.webclient.scheduler.SchedulerWebClient
import br.com.fitnesspro.local.data.access.dao.SchedulerConfigDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.model.scheduler.SchedulerConfig
import br.com.fitnesspro.to.TOSchedulerConfig
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class SchedulerConfigRepository(
    private val schedulerConfigDAO: SchedulerConfigDAO,
    private val schedulerWebClient: SchedulerWebClient,
    private val userDAO: UserDAO
) {
    suspend fun saveSchedulerConfig(toSchedulerConfig: TOSchedulerConfig) = withContext(IO) {
        val schedulerConfig = toSchedulerConfig.getSchedulerConfig()

        saveSchedulerConfigLocally(toSchedulerConfig, schedulerConfig)
        saveSchedulerConfigRemote(schedulerConfig)
    }

    private suspend fun saveSchedulerConfigLocally(
        toSchedulerConfig: TOSchedulerConfig,
        schedulerConfig: SchedulerConfig
    ) {
        val userId = userDAO.getAuthenticatedUser()?.id!!

        if (toSchedulerConfig.id == null) {
            schedulerConfigDAO.insert(schedulerConfig, userId, true)
            toSchedulerConfig.id = schedulerConfig.id
        } else {
            schedulerConfigDAO.update(schedulerConfig, userId, true)
        }
    }

    private suspend fun saveSchedulerConfigRemote(schedulerConfig: SchedulerConfig) {
        val response = schedulerWebClient.saveSchedulerConfig(schedulerConfig = schedulerConfig)

        if (response.success) {
            schedulerConfigDAO.update(schedulerConfig.copy(transmissionDate = response.transmissionDate))
        }
    }

    suspend fun saveSchedulerConfigBatch(toSchedulerConfigs: List<TOSchedulerConfig>) = withContext(IO) {
        saveSchedulerConfigBatchLocally(toSchedulerConfigs)
        saveSchedulerConfigBatchRemote(toSchedulerConfigs)
    }

    private suspend fun saveSchedulerConfigBatchLocally(toSchedulerConfigs: List<TOSchedulerConfig>) {
        val insertionList = mutableListOf<SchedulerConfig>()
        val updateList = mutableListOf<SchedulerConfig>()

        toSchedulerConfigs.forEach { toSchedulerConfig ->
            val schedulerConfig = toSchedulerConfig.getSchedulerConfig()

            if (toSchedulerConfig.id == null) {
                insertionList.add(schedulerConfig)
            } else {
                updateList.add(schedulerConfig)
            }
        }

        if (insertionList.isNotEmpty()) {
            schedulerConfigDAO.insertBatch(insertionList)
        }

        if (updateList.isNotEmpty()) {
            schedulerConfigDAO.updateBatch(updateList)
        }
    }

    private suspend fun saveSchedulerConfigBatchRemote(toSchedulerConfigs: List<TOSchedulerConfig>) {
        userDAO.getAuthenticatedUser()?.serviceToken?.let { token ->
            schedulerWebClient.saveSchedulerConfigBatch(
                token = token,
                schedulerConfigList = toSchedulerConfigs.map { it.getSchedulerConfig() }
            )
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