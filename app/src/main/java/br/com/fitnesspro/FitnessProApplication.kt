package br.com.fitnesspro

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.OneTimeWorkRequestBuilder
import br.com.fitnesspro.common.workers.GeneralModuleExportationWorker
import br.com.fitnesspro.common.workers.GeneralModuleImportationWorker
import br.com.fitnesspro.core.worker.OneTimeWorkerRequester
import br.com.fitnesspro.scheduler.workers.SchedulerModuleImportationWorker
import dagger.hilt.android.HiltAndroidApp
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

@HiltAndroidApp
class FitnessProApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .setMinimumLoggingLevel(android.util.Log.VERBOSE)
            .setExecutor(Dispatchers.IO.asExecutor())
            .build()

    override fun onCreate() {
        super.onCreate()

        OneTimeWorkerRequester(
            context = this,
            clazz = GeneralModuleImportationWorker::class.java,
            builder = OneTimeWorkRequestBuilder<GeneralModuleImportationWorker>()
        ).enqueue()

        OneTimeWorkerRequester(
            context = this,
            clazz = SchedulerModuleImportationWorker::class.java,
            builder = OneTimeWorkRequestBuilder<SchedulerModuleImportationWorker>()
        ).enqueue()

        OneTimeWorkerRequester(
            context = this,
            clazz = GeneralModuleExportationWorker::class.java,
            builder = OneTimeWorkRequestBuilder<GeneralModuleExportationWorker>()
        ).enqueue()
//
//        OneTimeWorkerRequester(
//            context = this,
//            clazz = SchedulerModuleExportationWorker::class.java,
//            builder = OneTimeWorkRequestBuilder<SchedulerModuleExportationWorker>()
//        ).enqueue()
    }
}