package br.com.fitnesspro.common.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.injection.IStorageWorkersEntryPoint
import br.com.fitnesspro.common.workers.common.AbstractImportationWorker
import br.com.fitnesspro.model.enums.EnumSyncModule
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors

@HiltWorker
class StorageImportationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : AbstractImportationWorker(context, workerParams) {

    private val entryPoint = EntryPointAccessors.fromApplication(context, IStorageWorkersEntryPoint::class.java)

    override fun getClazz() = javaClass

    override fun getModule() = EnumSyncModule.STORAGE

    // TODO - Analisar storage depois
//    override suspend fun verifyShouldImport(lastUpdateDate: LocalDateTime?): Boolean {
//        val shouldImportList = listOf(
//            entryPoint.getReportStorageImportationRepository().getExistsModelsDownload(lastUpdateDate),
//            entryPoint.getVideoStorageImportationRepository().getExistsModelsDownload(lastUpdateDate)
//        )
//
//        return shouldImportList.any { it }
//    }

    override suspend fun onImport() {
//        entryPoint.getReportStorageImportationRepository().import(lastUpdateDate)
//        entryPoint.getVideoStorageImportationRepository().import(lastUpdateDate)
    }

    override fun getOneTimeWorkRequestBuilder(): OneTimeWorkRequest.Builder {
        return OneTimeWorkRequestBuilder<StorageImportationWorker>()
    }

    override fun getWorkerDelay(): Long = DEFAULT_WORKER_DELAY
}