package br.com.fitnesspro.workout.injection

import br.com.fitnesspro.workout.repository.sync.importation.integration.HealthConnectIntegrationRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface IWorkoutHealthConnectIntegrationWorkersEntryPoint {

    fun getHealthConnectIntegrationRepository(): HealthConnectIntegrationRepository
}