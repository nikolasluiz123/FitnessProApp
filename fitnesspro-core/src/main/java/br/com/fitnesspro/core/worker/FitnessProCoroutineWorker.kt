package br.com.fitnesspro.core.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

abstract class FitnessProCoroutineWorker(
    protected val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    abstract suspend fun onError(e: Exception): Result

    abstract suspend fun onWork()

    open fun getMaxRetryTimeMillis(): Long = 60_000L

    override suspend fun doWork(): Result {
        val startTime = inputData.getLong("startTime", System.currentTimeMillis())
        val elapsed = System.currentTimeMillis() - startTime

        if (elapsed > getMaxRetryTimeMillis()) {
            return Result.failure()
        }

        return try {
            onWork()
            Result.success()
        } catch (e: Exception) {
            Log.e("FitnessProCoroutineWorker", e.message, e)
            onError(e)
        }
    }
}