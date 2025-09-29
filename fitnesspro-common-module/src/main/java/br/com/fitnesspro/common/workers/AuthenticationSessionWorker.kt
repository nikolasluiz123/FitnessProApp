package br.com.fitnesspro.common.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import br.com.android.firebase.toolkit.crashlytics.sendToFirebaseCrashlytics
import br.com.android.work.manager.toolkit.requester.PeriodicWorkerRequester.Companion.MIN_PERIODIC_WORKER_DELAY_MINS
import br.com.android.work.manager.toolkit.workers.coroutine.periodic.AbstractPeriodicCoroutineWorker
import br.com.fitnesspro.common.injection.IAuthenticationSessionWorkerEntryPoint
import br.com.fitnesspro.common.ui.event.GlobalEvent
import br.com.fitnesspro.core.worker.FitnessProLogConstants
import br.com.fitnesspro.shared.communication.exception.ExpiredTokenException
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import java.time.Duration
import java.time.temporal.ChronoUnit

@HiltWorker
class AuthenticationSessionWorker@AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
): AbstractPeriodicCoroutineWorker(context, workerParams) {

    private val entryPoint = EntryPointAccessors.fromApplication(context,IAuthenticationSessionWorkerEntryPoint::class.java)

    override fun getMaxRetryTimeMillis(): Long = Duration.of(MIN_PERIODIC_WORKER_DELAY_MINS * 3L, ChronoUnit.MINUTES).toMillis()

    override suspend fun onWorkPeriodic() {
        Log.i(FitnessProLogConstants.WORKER_AUTHENTICATION, "Verificando Token")
        entryPoint.getUserRepository().getValidToken()
    }

    override suspend fun onError(e: Exception): Result {
        return if (e is ExpiredTokenException) {
            entryPoint.getGlobalEvents().publish(GlobalEvent.TokenExpired)
            Result.success()
        } else {
            e.sendToFirebaseCrashlytics()
            Result.retry()
        }
    }
}