package br.com.fitnesspro.common.injection

import br.com.fitnesspro.common.repository.sync.exportation.storage.ReportStorageExportationRepository
import br.com.fitnesspro.common.repository.sync.exportation.storage.VideoStorageExportationRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface IStorageWorkersEntryPoint {

    fun getReportStorageExportationRepository(): ReportStorageExportationRepository

    fun getVideoStorageExportationRepository(): VideoStorageExportationRepository

}