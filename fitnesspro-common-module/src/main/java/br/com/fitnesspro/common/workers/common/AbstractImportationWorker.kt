package br.com.fitnesspro.common.workers.common

import android.content.Context
import android.util.Log
import androidx.work.WorkerParameters
import br.com.fitnesspro.core.extensions.dataStore
import br.com.fitnesspro.core.extensions.getRunImportWorker
import br.com.fitnesspro.core.worker.LogConstants

abstract class AbstractImportationWorker(
    context: Context,
    workerParams: WorkerParameters
) : AbstractSyncWorker(context, workerParams) {

    override suspend fun shouldRunWorker(): Boolean {
        return context.dataStore.getRunImportWorker()
    }

    override suspend fun onSyncWithTransaction() {
        Log.i(LogConstants.WORKER_IMPORT, "${"-".repeat(50)} Iniciando Importação ${javaClass.simpleName} ${"-".repeat(50)}")
        onImport()
        Log.i(LogConstants.WORKER_IMPORT, "${"-".repeat(50)} Finalizando Importação ${javaClass.simpleName} ${"-".repeat(50)}")
    }

    abstract suspend fun onImport()
}