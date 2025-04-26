package br.com.fitnesspro.common.workers.common

import android.content.Context
import androidx.work.WorkerParameters
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.model.sync.ImportationHistory
import java.time.LocalDateTime

abstract class AbstractImportationWorker(
    context: Context,
    workerParams: WorkerParameters
) : AbstractSyncWorker(context, workerParams) {

    private val importationHistoryDAO = repositoryEntryPoint.getImportationHistoryDAO()

    abstract suspend fun onImport(serviceToken: String, lastUpdateDate: LocalDateTime?)

    override suspend fun onSyncWithTransaction() {
        getValidUserTokenOrNull()?.let { serviceToken ->
            insertImportationHistory()
            val lastUpdateDate = importationHistoryDAO.getImportationHistory(getModule())?.date

            onImport(serviceToken, lastUpdateDate)
            updateImportationDate()
        }
    }

    private suspend fun insertImportationHistory() {
        val model = ImportationHistory(getModule())
        importationHistoryDAO.insert(model)
    }

    private suspend fun updateImportationDate() {
        importationHistoryDAO.getImportationHistory(getModule())!!.apply {
            date = dateTimeNow()
            importationHistoryDAO.update(this)
        }
    }
}