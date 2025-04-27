package br.com.fitnesspro.core.worker.periodic

import android.content.Context
import androidx.work.WorkerParameters
import br.com.fitnesspro.core.worker.FitnessProCoroutineWorker

abstract class FitnessProPeriodicCoroutineWorker(
    context: Context,
    workerParams: WorkerParameters
): FitnessProCoroutineWorker(context, workerParams) {

    abstract suspend fun onWorkOneTime()

    override suspend fun onWork() {
        onWorkOneTime()
    }
}