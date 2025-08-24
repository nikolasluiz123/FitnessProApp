package br.com.fitnesspro.common.workers.common

import android.content.Context
import android.util.Log
import androidx.work.WorkerParameters
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.core.worker.LogConstants
import br.com.fitnesspro.model.enums.EnumImportationModule
import br.com.fitnesspro.model.sync.ImportationHistory
import java.time.LocalDateTime
import java.time.ZoneOffset

abstract class AbstractImportationWorker(
    context: Context,
    workerParams: WorkerParameters
) : AbstractSyncWorker(context, workerParams) {

    private val importationHistoryDAO = repositoryEntryPoint.getImportationHistoryDAO()

    protected abstract fun getModule(): EnumImportationModule

    open suspend fun verifyShouldImport(lastUpdateDate: LocalDateTime?): Boolean  = true

    private suspend fun insertImportationHistory() {
        val model = ImportationHistory(getModule())
        importationHistoryDAO.insert(model)
    }

    private suspend fun updateImportationDate() {
        importationHistoryDAO.getImportationHistory(getModule())!!.apply {
            date = dateTimeNow(ZoneOffset.UTC)
            importationHistoryDAO.update(this)
        }
    }

    override suspend fun onSyncWithTransaction() {
        Log.i(LogConstants.WORKER_IMPORT, "${"-".repeat(50)} Iniciando Importação ${javaClass.simpleName} ${"-".repeat(50)}")

        insertImportationHistory()
        val lastUpdateDate = importationHistoryDAO.getImportationHistory(getModule())?.date

        if (verifyShouldImport(lastUpdateDate)) {
            onImport(lastUpdateDate)
            updateImportationDate()
        }

        Log.i(LogConstants.WORKER_IMPORT, "${"-".repeat(50)} Finalizando Importação ${javaClass.simpleName} ${"-".repeat(50)}")
    }

    abstract suspend fun onImport(lastUpdateDate: LocalDateTime?)
}