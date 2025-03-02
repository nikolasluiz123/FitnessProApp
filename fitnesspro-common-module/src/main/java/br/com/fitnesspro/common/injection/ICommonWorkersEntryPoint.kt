package br.com.fitnesspro.common.injection

import br.com.fitnesspro.common.repository.sync.exportation.PersonExportationRepository
import br.com.fitnesspro.common.repository.sync.exportation.SchedulerConfigExportationRepository
import br.com.fitnesspro.common.repository.sync.importation.AcademyImportationRepository
import br.com.fitnesspro.common.repository.sync.importation.PersonImportationRepository
import br.com.fitnesspro.common.repository.sync.importation.SchedulerConfigImportationRepository
import br.com.fitnesspro.common.repository.sync.importation.UserImportationRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ICommonWorkersEntryPoint {

    fun getUserImportationRepository(): UserImportationRepository

    fun getAcademyImportationRepository(): AcademyImportationRepository

    fun getPersonImportationRepository(): PersonImportationRepository

    fun getSchedulerConfigImportationRepository(): SchedulerConfigImportationRepository

    fun getPersonExportationRepository(): PersonExportationRepository

    fun getSchedulerConfigExportationRepository(): SchedulerConfigExportationRepository

}