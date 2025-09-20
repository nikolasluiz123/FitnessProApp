package br.com.fitnesspro.workout.repository.sync.importation.integration

import android.content.Context
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.common.FitnessProRepository

class HealthConnectIntegrationRepository(
    context: Context,
    private val personRepository: PersonRepository,
    private val caloriesIntegrationRepository: CaloriesIntegrationRepository,
    private val heartRateIntegrationRepository: HeartRateIntegrationRepository,
    private val sleepIntegrationRepository: SleepIntegrationRepository,
    private val stepsIntegrationRepository: StepsIntegrationRepository,
): FitnessProRepository(context) {

    suspend fun runIntegration() {
        personRepository.getAuthenticatedTOPerson()?.id?.let {
            caloriesIntegrationRepository.runIntegration(it)
            heartRateIntegrationRepository.runIntegration(it)
            sleepIntegrationRepository.runIntegration(it)
            stepsIntegrationRepository.runIntegration(it)
        }
    }
}