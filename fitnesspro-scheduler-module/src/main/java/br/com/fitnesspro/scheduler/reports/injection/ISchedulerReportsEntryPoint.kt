package br.com.fitnesspro.scheduler.reports.injection

import br.com.fitnesspro.scheduler.repository.SchedulerReportRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ISchedulerReportsEntryPoint {

    fun getSchedulerReportsRepository(): SchedulerReportRepository

}