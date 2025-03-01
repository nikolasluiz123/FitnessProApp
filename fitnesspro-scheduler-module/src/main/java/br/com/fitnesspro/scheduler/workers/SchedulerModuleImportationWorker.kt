package br.com.fitnesspro.scheduler.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.repository.sync.importation.SchedulerConfigImportationRepository
import br.com.fitnesspro.core.worker.FitnessProOneTimeCoroutineWorker
import br.com.fitnesspro.firebase.api.crashlytics.sendToFirebaseCrashlytics
import br.com.fitnesspro.scheduler.repository.sync.importation.SchedulerImportationRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SchedulerModuleImportationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val schedulerConfigImportationRepository: SchedulerConfigImportationRepository,
    private val schedulerImportationRepository: SchedulerImportationRepository
) : FitnessProOneTimeCoroutineWorker(context, workerParams) {

    override fun onError(e: Exception) {
        e.sendToFirebaseCrashlytics()
    }

    override suspend fun onWorkOneTime() {
        schedulerConfigImportationRepository.import()
        schedulerImportationRepository.import()
    }

    override fun getClazz() = javaClass

    override fun getOneTimeWorkRequestBuilder(): OneTimeWorkRequest.Builder {
        return OneTimeWorkRequestBuilder<SchedulerModuleImportationWorker>()
    }
}