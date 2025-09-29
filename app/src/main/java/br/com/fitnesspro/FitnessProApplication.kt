package br.com.fitnesspro

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import br.com.android.work.manager.toolkit.requester.OneTimeWorkerRequester
import br.com.android.work.manager.toolkit.requester.PeriodicWorkerRequester
import br.com.fitnesspro.common.workers.AuthenticationSessionWorker
import br.com.fitnesspro.common.workers.GeneralModuleExportationWorker
import br.com.fitnesspro.common.workers.GeneralModuleImportationWorker
import br.com.fitnesspro.common.workers.StorageExportationWorker
import br.com.fitnesspro.common.workers.StorageImportationWorker
import br.com.fitnesspro.core.worker.WorkerDelay
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
                repeatInterval = PeriodicWorkerRequester.MIN_PERIODIC_WORKER_DELAY_MINS,
                repeatIntervalTimeUnit = TimeUnit.MINUTES
            )
        ).enqueue()

        PeriodicWorkerRequester(
            context = this,
            clazz = WorkoutHealthConnectIntegrationWorker::class.java,
            builder = PeriodicWorkRequestBuilder<WorkoutHealthConnectIntegrationWorker>(
                repeatInterval = WorkerDelay.FITNESS_PRO_DEFAULT_HC_IMPORT_WORKER_DELAY_HOURS,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
        ).enqueue()

        OneTimeWorkerRequester(
            context = this,
            clazz = GeneralModuleImportationWorker::class.java,
            builder = OneTimeWorkRequestBuilder<GeneralModuleImportationWorker>(),
            workerDelay = WorkerDelay.FITNESS_PRO_DEFAULT_SYNC_WORKER_DELAY_MINS
        ).enqueue()

        OneTimeWorkerRequester(
            context = this,
            clazz = SchedulerModuleImportationWorker::class.java,
            builder = OneTimeWorkRequestBuilder<SchedulerModuleImportationWorker>(),
            workerDelay = WorkerDelay.FITNESS_PRO_DEFAULT_SYNC_WORKER_DELAY_MINS
        ).enqueue()

        OneTimeWorkerRequester(
            context = this,
            clazz = WorkoutModuleImportationWorker::class.java,
            builder = OneTimeWorkRequestBuilder<WorkoutModuleImportationWorker>(),
            workerDelay = WorkerDelay.FITNESS_PRO_DEFAULT_SYNC_WORKER_DELAY_MINS
        ).enqueue()

        OneTimeWorkerRequester(
            context = this,
            clazz = StorageImportationWorker::class.java,
            builder = OneTimeWorkRequestBuilder<StorageImportationWorker>(),
            workerDelay = WorkerDelay.FITNESS_PRO_DEFAULT_SYNC_WORKER_DELAY_MINS
        ).enqueue()

        OneTimeWorkerRequester(
            context = this,
            clazz = GeneralModuleExportationWorker::class.java,
            builder = OneTimeWorkRequestBuilder<GeneralModuleExportationWorker>(),
            workerDelay = WorkerDelay.FITNESS_PRO_DEFAULT_SYNC_WORKER_DELAY_MINS
        ).enqueue()

        OneTimeWorkerRequester(
            context = this,
            clazz = SchedulerModuleExportationWorker::class.java,
            builder = OneTimeWorkRequestBuilder<SchedulerModuleExportationWorker>(),
            workerDelay = WorkerDelay.FITNESS_PRO_DEFAULT_SYNC_WORKER_DELAY_MINS
        ).enqueue()

        OneTimeWorkerRequester(
            context = this,
            clazz = WorkoutModuleExportationWorker::class.java,
            builder = OneTimeWorkRequestBuilder<WorkoutModuleExportationWorker>(),
            workerDelay = WorkerDelay.FITNESS_PRO_DEFAULT_SYNC_WORKER_DELAY_MINS
        ).enqueue()

        OneTimeWorkerRequester(
            context = this,
            clazz = StorageExportationWorker::class.java,
            builder = OneTimeWorkRequestBuilder<StorageExportationWorker>(),
            workerDelay = WorkerDelay.FITNESS_PRO_DEFAULT_SYNC_WORKER_DELAY_MINS
        ).enqueue()

    }
}