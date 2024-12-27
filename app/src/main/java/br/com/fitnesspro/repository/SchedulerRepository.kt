package br.com.fitnesspro.repository

import br.com.fitnesspro.local.data.access.dao.SchedulerDAO
import br.com.fitnesspro.model.scheduler.SchedulerConfig
import br.com.fitnesspro.to.TOScheduler
import br.com.fitnesspro.to.TOSchedulerConfig
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.time.YearMonth

class SchedulerRepository(
    private val schedulerDAO: SchedulerDAO,
    private val userRepository: UserRepository
) {

    suspend fun saveSchedulerConfig(toSchedulerConfig: TOSchedulerConfig) = withContext(IO) {
        val schedulerConfig = toSchedulerConfig.getSchedulerConfig()
        schedulerDAO.saveConfig(schedulerConfig)
    }

    suspend fun getTOSchedulerConfigByPersonId(personId: String): TOSchedulerConfig? = withContext(IO) {
        schedulerDAO.findByPersonId(personId).getTOSchedulerConfig()
    }

    suspend fun getSchedulerList(yearMonth: YearMonth): List<TOScheduler> = withContext(IO) {
        val toPerson = userRepository.getAuthenticatedTOPerson()!!

        schedulerDAO.getSchedulerList(
            personId = toPerson.id!!,
            userType = toPerson.toUser?.type!!,
            yearMonth = yearMonth
        )
    }

    private fun SchedulerConfig?.getTOSchedulerConfig(): TOSchedulerConfig? {
        return this?.run {
            TOSchedulerConfig(
                id = id,
                personId = personId,
                alarm = alarm,
                notification = notification,
                minScheduleDensity = minScheduleDensity,
                maxScheduleDensity = maxScheduleDensity
            )
        }
    }

    private suspend fun TOSchedulerConfig.getSchedulerConfig(): SchedulerConfig {
        return if (id == null) {
            SchedulerConfig(
                personId = personId,
                alarm = alarm,
                notification = notification,
                minScheduleDensity = minScheduleDensity,
                maxScheduleDensity = maxScheduleDensity
            )
        } else {
            schedulerDAO.findById(id!!).copy(
                alarm = alarm,
                notification = notification,
                minScheduleDensity = minScheduleDensity,
                maxScheduleDensity = maxScheduleDensity
            )
        }
    }
}