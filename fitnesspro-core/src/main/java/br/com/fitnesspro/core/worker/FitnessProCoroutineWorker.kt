package br.com.fitnesspro.core.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

abstract class FitnessProCoroutineWorker(
    protected val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    abstract suspend fun onError(e: Exception): Result

    abstract suspend fun onWork()

    override suspend fun doWork(): Result {
        return try {
            onWork()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            onError(e)
        }
    }
}