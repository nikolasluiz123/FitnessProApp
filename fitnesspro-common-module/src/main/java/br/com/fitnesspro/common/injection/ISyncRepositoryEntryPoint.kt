package br.com.fitnesspro.common.injection

import br.com.fitnesspor.service.data.access.webclient.log.ExecutionLogWebClient
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.local.data.access.dao.ImportationHistoryDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ISyncRepositoryEntryPoint {

    fun getImportationHistoryDAO(): ImportationHistoryDAO

    fun getUserDAO(): UserDAO

    fun getExecutionLogWebClient(): ExecutionLogWebClient

    fun getUserRepository(): UserRepository
}