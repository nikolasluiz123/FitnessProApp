package br.com.fitnesspro.common.injection.health

import br.com.fitnesspro.common.repository.PersonRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface IHealthConnectIntegrationWorkerEntryPoint {

    fun getPersonRepository(): PersonRepository
}