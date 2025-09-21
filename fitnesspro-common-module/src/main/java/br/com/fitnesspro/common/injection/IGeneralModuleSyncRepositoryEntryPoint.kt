package br.com.fitnesspro.common.injection

import br.com.fitnesspro.local.data.access.dao.AcademyDAO
import br.com.fitnesspro.local.data.access.dao.PersonAcademyTimeDAO
import br.com.fitnesspro.local.data.access.dao.PersonDAO
import br.com.fitnesspro.local.data.access.dao.SchedulerConfigDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.service.data.access.webclient.sync.GeneralModuleSyncWebClient
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface IGeneralModuleSyncRepositoryEntryPoint {

    fun getGeneralSyncWebClient(): GeneralModuleSyncWebClient

    fun getAcademyDAO(): AcademyDAO

    fun getPersonDAO(): PersonDAO

    fun getUserDAO(): UserDAO

    fun getPersonAcademyTimeDAO(): PersonAcademyTimeDAO

    fun getSchedulerConfigDAO(): SchedulerConfigDAO
}