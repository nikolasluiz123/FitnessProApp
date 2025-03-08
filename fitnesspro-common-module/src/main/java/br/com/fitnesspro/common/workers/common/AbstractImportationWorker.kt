package br.com.fitnesspro.common.workers.common

import android.content.Context
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.injection.ISyncRepositoryEntryPoint
import br.com.fitnesspro.core.worker.FitnessProOneTimeCoroutineWorker
import br.com.fitnesspro.firebase.api.crashlytics.sendToFirebaseCrashlytics
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.sync.ImportationHistory
import dagger.hilt.android.EntryPointAccessors
import java.time.LocalDateTime

abstract class AbstractImportationWorker(
    context: Context,
    workerParams: WorkerParameters
) : FitnessProOneTimeCoroutineWorker(context, workerParams) {

    private val entryPoint: ISyncRepositoryEntryPoint = EntryPointAccessors.fromApplication(context, ISyncRepositoryEntryPoint::class.java)
    private val importationHistoryDAO = entryPoint.getImportationHistoryDAO()
    private val userRepository = entryPoint.getUserRepository()

    abstract suspend fun onImport(serviceToken: String, lastUpdateDate: LocalDateTime?)

    abstract fun getModule(): EnumSyncModule

    override suspend fun onWorkOneTime() {
        userRepository.runInTransaction {
            userRepository.getAuthenticatedUser()?.serviceToken?.let { serviceToken ->
                insertImportationHistory()
                val lastUpdateDate = importationHistoryDAO.getImportationHistory(getModule())?.date

                onImport(serviceToken, lastUpdateDate)
                updateImportationDate()
            }
        }
    }

    override fun onError(e: Exception) {
        e.sendToFirebaseCrashlytics()
    }

    private suspend fun insertImportationHistory() {
        val model = ImportationHistory(getModule())
        importationHistoryDAO.insert(model)
    }

    private suspend fun updateImportationDate() {
        importationHistoryDAO.getImportationHistory(getModule())!!.apply {
            date = LocalDateTime.now()
            importationHistoryDAO.update(this)
        }
    }
}