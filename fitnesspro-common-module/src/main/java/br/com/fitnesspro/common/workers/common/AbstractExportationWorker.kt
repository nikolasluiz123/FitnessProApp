package br.com.fitnesspro.common.workers.common

import android.content.Context
import androidx.work.WorkerParameters

abstract class AbstractExportationWorker(
    context: Context,
    workerParams: WorkerParameters
) : AbstractSyncWorker(context, workerParams) {

    abstract suspend fun onExport(serviceToken: String)

    override suspend fun onSyncWithTransaction() {
        getValidTokenOrNull()?.let { serviceToken ->
            onExport(serviceToken)
        }
    }
}