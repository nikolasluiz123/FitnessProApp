package br.com.fitnesspro

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.BackoffPolicy
import androidx.work.Configuration
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import br.com.fitnesspro.scheduler.worker.NewChatMessageNotificationWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class FitnessProApplication : Application() {

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        WorkManager.initialize(
            this,
            Configuration.Builder()
                .setWorkerFactory(hiltWorkerFactory)
                .build()
        )

        enqueueChatNotificationWorker()
    }

    private fun enqueueChatNotificationWorker() {
        val workRequest = OneTimeWorkRequestBuilder<NewChatMessageNotificationWorker>()
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)
    }
}