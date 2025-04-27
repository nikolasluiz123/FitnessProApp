package br.com.fitnesspro.common.workers.common

import android.content.Context
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.injection.ISyncRepositoryEntryPoint
import br.com.fitnesspro.core.worker.onetime.FitnessProOneTimeCoroutineWorker
import br.com.fitnesspro.firebase.api.crashlytics.sendToFirebaseCrashlytics
import br.com.fitnesspro.model.enums.EnumSyncModule
import dagger.hilt.android.EntryPointAccessors

abstract class AbstractSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : FitnessProOneTimeCoroutineWorker(context, workerParams) {

    protected val repositoryEntryPoint: ISyncRepositoryEntryPoint = EntryPointAccessors.fromApplication(context, ISyncRepositoryEntryPoint::class.java)
    protected val userRepository = repositoryEntryPoint.getUserRepository()

    protected abstract fun getModule(): EnumSyncModule

    protected suspend fun getValidUserTokenOrNull(): String? {
        return userRepository.getValidUserToken()
    }

    abstract suspend fun onSyncWithTransaction()

    override suspend fun onWorkOneTime() {
        userRepository.runInTransaction {
            onSyncWithTransaction()
        }
    }

    override suspend fun onError(e: Exception): Result {
        e.sendToFirebaseCrashlytics()
        return Result.retry()
    }
}