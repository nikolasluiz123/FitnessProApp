package br.com.fitnesspro.common.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.injection.IAuthenticationSessionWorkerEntryPoint
import br.com.fitnesspro.common.ui.event.GlobalEvent
import br.com.fitnesspro.core.worker.LogConstants
import br.com.fitnesspro.core.worker.periodic.FitnessProPeriodicCoroutineWorker
import br.com.fitnesspro.firebase.api.crashlytics.sendToFirebaseCrashlytics
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
): FitnessProPeriodicCoroutineWorker(context, workerParams) {

    private val entryPoint = EntryPointAccessors.fromApplication(context,IAuthenticationSessionWorkerEntryPoint::class.java)

    override fun getMaxRetryTimeMillis(): Long = Duration.of(DEFAULT_WORKER_DELAY * 3L, ChronoUnit.MINUTES).toMillis()

    override suspend fun onWorkOneTime() {
        Log.i(LogConstants.WORKER_AUTHENTICATION, "Verificando Token")
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

    companion object {
        const val DEFAULT_WORKER_DELAY = 15L
    }
}