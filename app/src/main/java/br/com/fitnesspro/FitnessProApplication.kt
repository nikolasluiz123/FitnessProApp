package br.com.fitnesspro

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import br.com.fitnesspro.common.workers.GeneralModuleExportationWorker
import br.com.fitnesspro.common.workers.GeneralModuleImportationWorker
import br.com.fitnesspro.core.worker.OneTimeWorkerRequester
import br.com.fitnesspro.scheduler.workers.SchedulerModuleExportationWorker
import br.com.fitnesspro.scheduler.workers.SchedulerModuleImportationWorker
import dagger.hilt.android.HiltAndroidApp
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

        OneTimeWorkerRequester(
            context = applicationContext,
            clazz = GeneralModuleImportationWorker::class.java,
            builder = OneTimeWorkRequestBuilder<GeneralModuleImportationWorker>()
        ).enqueue()

        OneTimeWorkerRequester(
            context = applicationContext,
            clazz = GeneralModuleExportationWorker::class.java,
            builder = OneTimeWorkRequestBuilder<GeneralModuleExportationWorker>()
        ).enqueue()

        OneTimeWorkerRequester(
            context = applicationContext,
            clazz = SchedulerModuleImportationWorker::class.java,
            builder = OneTimeWorkRequestBuilder<SchedulerModuleImportationWorker>()
        ).enqueue()

        OneTimeWorkerRequester(
            context = applicationContext,
            clazz = SchedulerModuleExportationWorker::class.java,
            builder = OneTimeWorkRequestBuilder<SchedulerModuleExportationWorker>()
        ).enqueue()
    }
}