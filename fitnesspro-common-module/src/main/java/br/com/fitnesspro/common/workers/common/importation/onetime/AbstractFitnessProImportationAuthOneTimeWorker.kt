package br.com.fitnesspro.common.workers.common.importation.onetime

import android.content.Context
import androidx.work.WorkerParameters
import br.com.android.firebase.toolkit.crashlytics.sendToFirebaseCrashlytics
import br.com.android.work.manager.toolkit.workers.coroutine.onetime.sync.AbstractImportationTokenAuthOneTimeWorker
import br.com.fitnesspro.common.injection.ISyncEntryPoint
import br.com.fitnesspro.core.worker.WorkerDelay.FITNESS_PRO_DEFAULT_SYNC_WORKER_DELAY_MINS
import dagger.hilt.android.EntryPointAccessors
import java.time.Duration
import java.time.temporal.ChronoUnit

abstract class AbstractFitnessProImportationAuthOneTimeWorker(
    context: Context,
    workerParams: WorkerParameters
) : AbstractImportationTokenAuthOneTimeWorker(context, workerParams) {

    protected val commonEntryPoint: ISyncEntryPoint = EntryPointAccessors.fromApplication(context, ISyncEntryPoint::class.java)
    protected val userRepository = commonEntryPoint.getUserRepository()

    override fun getMaxRetryTimeMillis(): Long = Duration.of(FITNESS_PRO_DEFAULT_SYNC_WORKER_DELAY_MINS * 3L, ChronoUnit.MINUTES).toMillis()

    override suspend fun getValidTokenOrNull(): String? {
        return userRepository.getValidUserToken()
    }

    override fun getTransactionManager(): suspend (suspend () -> Unit) -> Unit {
        return userRepository::runInTransaction
    }

    override fun onBeforeErrorRetry(e: Exception) {
        super.onBeforeErrorRetry(e)
        e.sendToFirebaseCrashlytics()
    }

    override fun getWorkerDelay(): Long = FITNESS_PRO_DEFAULT_SYNC_WORKER_DELAY_MINS
}