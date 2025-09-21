package br.com.fitnesspro.core.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

abstract class FitnessProCoroutineWorker(
    protected val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    abstract suspend fun onError(e: Exception): Result

    abstract suspend fun onWork()

    /**
     * Define o tempo máximo de espera para a execução do Worker. Isso serve para limitar o número
     * de reexecuções em caso de falha.
     */
    abstract fun getMaxRetryTimeMillis(): Long

    override suspend fun doWork() = withContext(IO) {
        val startTime = inputData.getLong("startTime", System.currentTimeMillis())
        val elapsed = System.currentTimeMillis() - startTime

        if (elapsed > getMaxRetryTimeMillis()) {
            Result.failure()
        }

        try {
            onWork()
            Result.success()
        } catch (e: Exception) {
            Log.e("FitnessProCoroutineWorker", e.message, e)
            onError(e)
        }
    }
}