package br.com.fitnesspro.common.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.injection.IStorageWorkersEntryPoint
import br.com.fitnesspro.common.workers.common.importation.onetime.AbstractFitnessProImportationOneTimeWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors

@HiltWorker
class StorageImportationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : AbstractFitnessProImportationOneTimeWorker(context, workerParams) {

    private val entryPoint = EntryPointAccessors.fromApplication(context, IStorageWorkersEntryPoint::class.java)

    override fun getClazz() = javaClass

    override suspend fun shouldRunWorker(): Boolean {
        val shouldRunWorker = super.shouldRunWorker()

        val shouldImportList = listOf(
            entryPoint.getReportStorageImportationRepository().getExistsModelsDownload(),
            entryPoint.getVideoStorageImportationRepository().getExistsModelsDownload()
        )

        return shouldRunWorker && shouldImportList.any { it }
    }

    override suspend fun onImport() {
        entryPoint.getReportStorageImportationRepository().import()
        entryPoint.getVideoStorageImportationRepository().import()
    }

    override fun getOneTimeWorkRequestBuilder(): OneTimeWorkRequest.Builder {
        return OneTimeWorkRequestBuilder<StorageImportationWorker>()
    }
}