package br.com.fitnesspro.common.workers.common

import android.content.Context
import androidx.work.WorkerParameters
import java.time.LocalDateTime

abstract class AbstractAuthenticatedImportationWorker(
    context: Context,
    workerParams: WorkerParameters
) : AbstractImportationWorker(context, workerParams) {

    abstract suspend fun onImport(serviceToken: String, lastUpdateDate: LocalDateTime?)

    final override suspend fun onImport(lastUpdateDate: LocalDateTime?) {
        getValidUserTokenOrNull()?.let { serviceToken ->
            onImport(serviceToken, lastUpdateDate)
        }
    }
}