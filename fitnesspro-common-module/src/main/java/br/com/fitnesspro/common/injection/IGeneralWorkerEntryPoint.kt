package br.com.fitnesspro.common.injection

import br.com.fitnesspro.common.repository.sync.exportation.GeneralModuleExportationRepository
import br.com.fitnesspro.common.repository.sync.importation.GeneralModuleImportationRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface IGeneralWorkerEntryPoint {

    fun getGeneralModuleImportationRepository(): GeneralModuleImportationRepository

    fun getGeneralModuleExportationRepository(): GeneralModuleExportationRepository

}