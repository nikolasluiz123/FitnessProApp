package br.com.fitnesspro.scheduler.injection

import br.com.fitnesspro.service.data.access.webclient.sync.SchedulerModuleSyncWebClient
import br.com.fitnesspro.local.data.access.dao.ReportDAO
import br.com.fitnesspro.local.data.access.dao.SchedulerDAO
import br.com.fitnesspro.local.data.access.dao.SchedulerReportDAO
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ISchedulerModuleSyncRepositoryEntryPoint {

    fun getSchedulerSyncWebClient(): SchedulerModuleSyncWebClient

    fun getSchedulerDAO(): SchedulerDAO

    fun getReportDAO(): ReportDAO

    fun getSchedulerReportDAO(): SchedulerReportDAO
}