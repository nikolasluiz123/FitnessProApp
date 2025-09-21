package br.com.fitnesspro.common.workers.common

import android.content.Context
import android.util.Log
import androidx.work.WorkerParameters
import br.com.fitnesspro.core.worker.LogConstants

abstract class AbstractImportationWorker(
    context: Context,
    workerParams: WorkerParameters
) : AbstractSyncWorker(context, workerParams) {

    open suspend fun verifyShouldImport(): Boolean  = true

    override suspend fun onSyncWithTransaction() {
        Log.i(LogConstants.WORKER_IMPORT, "${"-".repeat(50)} Iniciando Importação ${javaClass.simpleName} ${"-".repeat(50)}")

        if (verifyShouldImport()) {
            onImport()
        }

        Log.i(LogConstants.WORKER_IMPORT, "${"-".repeat(50)} Finalizando Importação ${javaClass.simpleName} ${"-".repeat(50)}")
    }

    abstract suspend fun onImport()
}