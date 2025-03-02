package br.com.fitnesspro.common.repository.common

import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.local.data.access.database.AppDatabase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface IFitnessProRepositoryEntryPoint {

    fun getUserDAO(): UserDAO

    fun getAppDatabase(): AppDatabase
}