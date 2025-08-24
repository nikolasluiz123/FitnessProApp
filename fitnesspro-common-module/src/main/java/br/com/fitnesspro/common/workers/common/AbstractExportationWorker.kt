package br.com.fitnesspro.common.workers.common

import android.content.Context
import android.util.Log
import androidx.work.WorkerParameters
import br.com.fitnesspro.core.worker.LogConstants

abstract class AbstractExportationWorker(
    context: Context,
    workerParams: WorkerParameters
) : AbstractSyncWorker(context, workerParams) {

    abstract suspend fun onExport(serviceToken: String)

    override suspend fun onSyncWithTransaction() {
        getValidUserTokenOrNull()?.let { serviceToken ->
            Log.i(LogConstants.WORKER_EXPORT, "${"-".repeat(50)} Iniciando Exportação ${javaClass.simpleName} ${"-".repeat(50)}")
            onExport(serviceToken)
            Log.i(LogConstants.WORKER_EXPORT, "${"-".repeat(50)} Finalizando Exportação ${javaClass.simpleName} ${"-".repeat(50)}")

        }
    }
}