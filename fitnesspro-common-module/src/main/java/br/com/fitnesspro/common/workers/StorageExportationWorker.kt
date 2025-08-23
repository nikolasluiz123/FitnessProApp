package br.com.fitnesspro.common.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.injection.IStorageWorkersEntryPoint
import br.com.fitnesspro.common.workers.common.AbstractExportationWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors

@HiltWorker
class StorageExportationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : AbstractExportationWorker(context, workerParams) {

    private val entryPoint = EntryPointAccessors.fromApplication(context, IStorageWorkersEntryPoint::class.java)

    override suspend fun onExport(serviceToken: String) {
        entryPoint.getReportStorageExportationRepository().export(serviceToken)
        entryPoint.getVideoStorageExportationRepository().export(serviceToken)
    }

    override fun getClazz() = javaClass

    override fun getOneTimeWorkRequestBuilder(): OneTimeWorkRequest.Builder {
        return OneTimeWorkRequestBuilder<StorageExportationWorker>()
    }

}