package br.com.fitnesspro.scheduler.injection

import br.com.fitnesspro.scheduler.repository.sync.exportation.SchedulerModuleExportationRepository
import br.com.fitnesspro.scheduler.repository.sync.importation.SchedulerModuleImportationRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface IScheduleWorkersEntryPoint {

    fun getSchedulerModuleImportationRepository(): SchedulerModuleImportationRepository

    fun getSchedulerModuleExportationRepository(): SchedulerModuleExportationRepository

}