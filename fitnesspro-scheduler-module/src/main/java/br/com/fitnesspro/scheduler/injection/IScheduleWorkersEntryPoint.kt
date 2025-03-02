package br.com.fitnesspro.scheduler.injection

import br.com.fitnesspro.scheduler.repository.sync.exportation.SchedulerExportationRepository
import br.com.fitnesspro.scheduler.repository.sync.importation.SchedulerImportationRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface IScheduleWorkersEntryPoint {
    
    fun getSchedulerImportationRepository(): SchedulerImportationRepository

    fun getSchedulerExportationRepository(): SchedulerExportationRepository
}