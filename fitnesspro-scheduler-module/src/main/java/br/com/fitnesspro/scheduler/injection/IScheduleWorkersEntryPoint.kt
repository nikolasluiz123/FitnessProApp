package br.com.fitnesspro.scheduler.injection

import br.com.fitnesspro.scheduler.repository.sync.exportation.ReportFromSchedulerExportationRepository
import br.com.fitnesspro.scheduler.repository.sync.exportation.SchedulerExportationRepository
import br.com.fitnesspro.scheduler.repository.sync.exportation.SchedulerReportExportationRepository
import br.com.fitnesspro.scheduler.repository.sync.importation.ReportFromSchedulerImportationRepository
import br.com.fitnesspro.scheduler.repository.sync.importation.SchedulerImportationRepository
import br.com.fitnesspro.scheduler.repository.sync.importation.SchedulerReportImportationRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface IScheduleWorkersEntryPoint {
    
    fun getSchedulerImportationRepository(): SchedulerImportationRepository

    fun getSchedulerReportImportationRepository(): SchedulerReportImportationRepository

    fun getReportsFromSchedulerImportationRepository(): ReportFromSchedulerImportationRepository

    fun getSchedulerExportationRepository(): SchedulerExportationRepository

    fun getSchedulerReportExportationRepository(): SchedulerReportExportationRepository

    fun getReportFromSchedulerExportationRepository(): ReportFromSchedulerExportationRepository

}