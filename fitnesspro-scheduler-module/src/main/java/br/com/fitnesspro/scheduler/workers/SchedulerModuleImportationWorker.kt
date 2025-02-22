package br.com.fitnesspro.scheduler.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.repository.importation.SchedulerConfigImportationRepository
import br.com.fitnesspro.core.worker.FitnessProCoroutineWorker
import br.com.fitnesspro.firebase.api.crashlytics.sendToFirebaseCrashlytics
import br.com.fitnesspro.scheduler.repository.importation.SchedulerImportationRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SchedulerModuleImportationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val schedulerConfigImportationRepository: SchedulerConfigImportationRepository,
    private val schedulerImportationRepository: SchedulerImportationRepository
) : FitnessProCoroutineWorker(context, workerParams) {

    override fun onError(e: Exception) {
        e.sendToFirebaseCrashlytics()
    }

    override suspend fun onWork() {
        schedulerConfigImportationRepository.import()
        schedulerImportationRepository.import()
    }
}