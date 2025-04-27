package br.com.fitnesspro.common.injection

import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.ui.event.GlobalEvents
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface IAuthenticationSessionWorkerEntryPoint {

    fun getUserRepository(): UserRepository

    fun getGlobalEvents(): GlobalEvents
}