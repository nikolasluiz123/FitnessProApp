package br.com.fitnesspro.common.workers.common

import android.content.Context
import androidx.work.WorkerParameters

abstract class AbstractAuthenticatedImportationWorker(
    context: Context,
    workerParams: WorkerParameters
) : AbstractImportationWorker(context, workerParams) {

    abstract suspend fun onImport(serviceToken: String)

    final override suspend fun onImport() {
        getValidUserTokenOrNull()?.let { serviceToken ->
            onImport(serviceToken)
        }
    }

    final override suspend fun onSyncWithTransaction() {
        if (getValidUserTokenOrNull().isNullOrEmpty().not()) {
            super.onSyncWithTransaction()
        }
    }
}