package br.com.fitnesspro.core.worker.onetime

import android.content.Context
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkerParameters
import br.com.fitnesspro.core.worker.FitnessProCoroutineWorker

abstract class FitnessProOneTimeCoroutineWorker(
    context: Context,
    workerParams: WorkerParameters
): FitnessProCoroutineWorker(context, workerParams) {

    protected lateinit var requester: OneTimeWorkerRequester

    abstract suspend fun onWorkOneTime()

    abstract fun getClazz(): Class<out FitnessProOneTimeCoroutineWorker>

    abstract fun getOneTimeWorkRequestBuilder(): OneTimeWorkRequest.Builder

    override suspend fun onWork() {
        onWorkOneTime()
        scheduleNext()
    }

    private fun scheduleNext() {
        if (!this::requester.isInitialized) {
            requester = OneTimeWorkerRequester(
                context = context,
                clazz = getClazz(),
                builder = getOneTimeWorkRequestBuilder()
            )
        }

        requester.enqueue()
    }
}