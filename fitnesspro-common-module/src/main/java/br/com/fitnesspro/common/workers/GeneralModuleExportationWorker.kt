package br.com.fitnesspro.common.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.repository.sync.exportation.PersonExportationRepository
import br.com.fitnesspro.core.worker.FitnessProOneTimeCoroutineWorker
import br.com.fitnesspro.firebase.api.crashlytics.sendToFirebaseCrashlytics
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class GeneralModuleExportationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val personExportationRepository: PersonExportationRepository,
) : FitnessProOneTimeCoroutineWorker(context, workerParams) {

    override fun onError(e: Exception) {
        e.sendToFirebaseCrashlytics()
    }

    override suspend fun onWorkOneTime() {
        personExportationRepository.export()
    }

    override fun getClazz() = javaClass

    override fun getOneTimeWorkRequestBuilder(): OneTimeWorkRequest.Builder {
        return OneTimeWorkRequestBuilder<GeneralModuleExportationWorker>()
    }

}