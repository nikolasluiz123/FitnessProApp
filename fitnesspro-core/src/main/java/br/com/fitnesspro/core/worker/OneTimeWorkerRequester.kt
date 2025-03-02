package br.com.fitnesspro.core.worker

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy.KEEP
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class OneTimeWorkerRequester(
    private val context: Context,
    private val clazz: Class<out FitnessProOneTimeCoroutineWorker>,
    builder: OneTimeWorkRequest.Builder,
    workerDelay: Long = 30
) {

    private val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

    private val request = builder.setConstraints(constraints)
        .setInitialDelay(workerDelay, TimeUnit.SECONDS)
        .build()

    fun enqueue() {
        WorkManager.getInstance(context).enqueueUniqueWork(clazz.simpleName, KEEP, request)
    }
}