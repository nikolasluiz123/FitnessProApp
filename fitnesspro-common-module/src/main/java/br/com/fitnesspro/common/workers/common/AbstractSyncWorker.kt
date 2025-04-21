package br.com.fitnesspro.common.workers.common

import android.content.Context
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.injection.ISyncRepositoryEntryPoint
import br.com.fitnesspro.core.worker.FitnessProOneTimeCoroutineWorker
import br.com.fitnesspro.firebase.api.crashlytics.sendToFirebaseCrashlytics
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.shared.communication.exception.ExpiredTokenException
import dagger.hilt.android.EntryPointAccessors

abstract class AbstractSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : FitnessProOneTimeCoroutineWorker(context, workerParams) {

    protected val repositoryEntryPoint: ISyncRepositoryEntryPoint = EntryPointAccessors.fromApplication(context, ISyncRepositoryEntryPoint::class.java)
    protected val userRepository = repositoryEntryPoint.getUserRepository()

    protected abstract fun getModule(): EnumSyncModule

    protected suspend fun getValidTokenOrNull(): String? {
        return try {
            userRepository.getValidToken()
        } catch (_: ExpiredTokenException) {
            null
        }
    }

    abstract suspend fun onSyncWithTransaction()

    override suspend fun onWorkOneTime() {
        userRepository.runInTransaction {
            onSyncWithTransaction()
        }
    }

    override fun onError(e: Exception) {
        e.sendToFirebaseCrashlytics()
    }
}