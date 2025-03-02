package br.com.fitnesspro.common.injection

import br.com.fitnesspro.local.data.access.dao.SyncHistoryDAO
import br.com.fitnesspro.local.data.access.dao.SyncLogDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ISyncRepositoryEntryPoint {

    fun getSyncHistoryDAO(): SyncHistoryDAO

    fun getSyncLogDAO(): SyncLogDAO

    fun getUserDAO(): UserDAO
}