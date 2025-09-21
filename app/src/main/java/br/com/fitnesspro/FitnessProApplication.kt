package br.com.fitnesspro

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import br.com.fitnesspro.common.workers.AuthenticationSessionWorker
import br.com.fitnesspro.common.workers.GeneralModuleExportationWorker
import br.com.fitnesspro.common.workers.GeneralModuleImportationWorker
import br.com.fitnesspro.common.workers.StorageExportationWorker
import br.com.fitnesspro.common.workers.StorageImportationWorker
import br.com.fitnesspro.common.workers.common.AbstractSyncWorker
import br.com.fitnesspro.core.worker.onetime.OneTimeWorkerRequester
import br.com.fitnesspro.core.worker.periodic.PeriodicWorkerRequester
import br.com.fitnesspro.scheduler.workers.SchedulerModuleExportationWorker
import br.com.fitnesspro.scheduler.workers.SchedulerModuleImportationWorker
import br.com.fitnesspro.workout.workers.WorkoutHealthConnectIntegrationWorker
import br.com.fitnesspro.workout.workers.WorkoutModuleExportationWorker
import br.com.fitnesspro.workout.workers.WorkoutModuleImportationWorker
import dagger.hilt.android.HiltAndroidApp
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class FitnessProApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .setMinimumLoggingLevel(android.util.Log.ERROR)
            .setExecutor(Dispatchers.IO.asExecutor())
            .build()

    override fun onCreate() {
        super.onCreate()

        PeriodicWorkerRequester(
            context = this,
            clazz = AuthenticationSessionWorker::class.java,
            builder = PeriodicWorkRequestBuilder<AuthenticationSessionWorker>(
                repeatInterval = AuthenticationSessionWorker.DEFAULT_WORKER_DELAY,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            )
        ).enqueue()

        PeriodicWorkerRequester(
            context = this,
            clazz = WorkoutHealthConnectIntegrationWorker::class.java,
            builder = PeriodicWorkRequestBuilder<WorkoutHealthConnectIntegrationWorker>(
                repeatInterval = WorkoutHealthConnectIntegrationWorker.DEFAULT_WORKER_DELAY,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
        ).enqueue()

        OneTimeWorkerRequester(
            context = this,
            clazz = GeneralModuleImportationWorker::class.java,
            builder = OneTimeWorkRequestBuilder<GeneralModuleImportationWorker>(),
            workerDelay = AbstractSyncWorker.DEFAULT_WORKER_DELAY
        ).enqueue()

        OneTimeWorkerRequester(
            context = this,
            clazz = SchedulerModuleImportationWorker::class.java,
            builder = OneTimeWorkRequestBuilder<SchedulerModuleImportationWorker>(),
            workerDelay = AbstractSyncWorker.DEFAULT_WORKER_DELAY
        ).enqueue()

        OneTimeWorkerRequester(
            context = this,
            clazz = WorkoutModuleImportationWorker::class.java,
            builder = OneTimeWorkRequestBuilder<WorkoutModuleImportationWorker>(),
            workerDelay = AbstractSyncWorker.DEFAULT_WORKER_DELAY
        ).enqueue()

        OneTimeWorkerRequester(
            context = this,
            clazz = StorageImportationWorker::class.java,
            builder = OneTimeWorkRequestBuilder<StorageImportationWorker>(),
            workerDelay = AbstractSyncWorker.DEFAULT_WORKER_DELAY
        ).enqueue()

        OneTimeWorkerRequester(
            context = this,
            clazz = GeneralModuleExportationWorker::class.java,
            builder = OneTimeWorkRequestBuilder<GeneralModuleExportationWorker>(),
            workerDelay = AbstractSyncWorker.DEFAULT_WORKER_DELAY
        ).enqueue()

        OneTimeWorkerRequester(
            context = this,
            clazz = SchedulerModuleExportationWorker::class.java,
            builder = OneTimeWorkRequestBuilder<SchedulerModuleExportationWorker>(),
            workerDelay = AbstractSyncWorker.DEFAULT_WORKER_DELAY
        ).enqueue()

        OneTimeWorkerRequester(
            context = this,
            clazz = WorkoutModuleExportationWorker::class.java,
            builder = OneTimeWorkRequestBuilder<WorkoutModuleExportationWorker>(),
            workerDelay = AbstractSyncWorker.DEFAULT_WORKER_DELAY
        ).enqueue()

        OneTimeWorkerRequester(
            context = this,
            clazz = StorageExportationWorker::class.java,
            builder = OneTimeWorkRequestBuilder<StorageExportationWorker>(),
            workerDelay = AbstractSyncWorker.DEFAULT_WORKER_DELAY
        ).enqueue()

    }
}