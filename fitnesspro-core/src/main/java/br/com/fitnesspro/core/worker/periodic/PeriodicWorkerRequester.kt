package br.com.fitnesspro.core.worker.periodic

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import br.com.fitnesspro.core.worker.FitnessProCoroutineWorker
import java.util.concurrent.TimeUnit

class PeriodicWorkerRequester(
    private val context: Context,
    private val clazz: Class<out FitnessProCoroutineWorker>,
    builder: PeriodicWorkRequest.Builder,
    workerDelay: Long = 15
) {

    private val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

    private val request = builder.setConstraints(constraints)
        .setInitialDelay(workerDelay, TimeUnit.MINUTES)
        .build()

    fun enqueue() {
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            uniqueWorkName = clazz.simpleName,
            existingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
            request = request
        )
    }
}