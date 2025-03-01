package br.com.fitnesspro.scheduler.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import br.com.fitnesspro.core.worker.FitnessProOneTimeCoroutineWorker
import br.com.fitnesspro.firebase.api.crashlytics.sendToFirebaseCrashlytics
import br.com.fitnesspro.scheduler.repository.sync.exportation.SchedulerExportationRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SchedulerModuleExportationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val schedulerExportationRepository: SchedulerExportationRepository
) : FitnessProOneTimeCoroutineWorker(context, workerParams) {

    override fun onError(e: Exception) {
        e.sendToFirebaseCrashlytics()
    }

    override suspend fun onWorkOneTime() {
        schedulerExportationRepository.export()
    }

    override fun getClazz() = javaClass

    override fun getOneTimeWorkRequestBuilder(): OneTimeWorkRequest.Builder {
        return OneTimeWorkRequestBuilder<SchedulerModuleExportationWorker>()
    }
}