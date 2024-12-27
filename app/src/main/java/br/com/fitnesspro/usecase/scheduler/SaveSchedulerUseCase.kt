package br.com.fitnesspro.usecase.scheduler

import br.com.fitnesspro.repository.SchedulerRepository
import br.com.fitnesspro.to.TOSchedulerConfig

class SaveSchedulerUseCase(
    private val schedulerRepository: SchedulerRepository
) {

    suspend fun saveConfig(personId: String, toSchedulerConfig: TOSchedulerConfig? = null) {
        val config = if (toSchedulerConfig != null) {
            schedulerRepository.getTOSchedulerConfigByPersonId(personId)!!.copy(
                alarm = toSchedulerConfig.alarm,
                notification = toSchedulerConfig.notification,
                minScheduleDensity = toSchedulerConfig.minScheduleDensity,
                maxScheduleDensity = toSchedulerConfig.maxScheduleDensity
            )
        } else {
            TOSchedulerConfig(personId = personId)
        }
        
        schedulerRepository.saveSchedulerConfig(config)
    }

}