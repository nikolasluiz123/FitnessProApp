package br.com.fitnesspro.common.workers.common

import android.content.Context
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.injection.ISyncEntryPoint
import br.com.fitnesspro.core.worker.onetime.FitnessProOneTimeCoroutineWorker
import br.com.fitnesspro.firebase.api.crashlytics.sendToFirebaseCrashlytics
import br.com.fitnesspro.model.enums.EnumSyncModule
import dagger.hilt.android.EntryPointAccessors
import java.time.Duration
import java.time.temporal.ChronoUnit

abstract class AbstractSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : FitnessProOneTimeCoroutineWorker(context, workerParams) {

    protected val commonEntryPoint: ISyncEntryPoint = EntryPointAccessors.fromApplication(context, ISyncEntryPoint::class.java)
    protected val userRepository = commonEntryPoint.getUserRepository()

    override fun getMaxRetryTimeMillis(): Long = Duration.of(DEFAULT_WORKER_DELAY * 3L, ChronoUnit.MINUTES).toMillis()

    protected suspend fun getValidUserTokenOrNull(): String? {
        return userRepository.getValidUserToken()
    }

    abstract suspend fun onSyncWithTransaction()

    protected abstract fun getModule(): EnumSyncModule

    override suspend fun onWorkOneTime() {
        userRepository.runInTransaction {
            onSyncWithTransaction()
        }
    }

    override suspend fun onError(e: Exception): Result {
        e.sendToFirebaseCrashlytics()
        return Result.retry()
    }

    companion object {
        const val DEFAULT_WORKER_DELAY = 3L
    }
}