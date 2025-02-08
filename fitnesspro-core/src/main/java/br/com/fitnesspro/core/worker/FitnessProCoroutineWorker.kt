package br.com.fitnesspro.core.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

abstract class FitnessProCoroutineWorker(
    protected val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    abstract fun onError(e: Exception)

    abstract suspend fun onWork()

    override suspend fun doWork(): Result {
        try {
            onWork()

            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            onError(e)
            return Result.retry()
        }
    }
}