package br.com.fitnesspro.common.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.repository.importation.AcademyImportationRepository
import br.com.fitnesspro.common.repository.importation.PersonImportationRepository
import br.com.fitnesspro.common.repository.importation.UserImportationRepository
import br.com.fitnesspro.core.worker.FitnessProCoroutineWorker
import br.com.fitnesspro.firebase.api.crashlytics.sendToFirebaseCrashlytics
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class GeneralModuleImportationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val academyImportationRepository: AcademyImportationRepository,
    private val userImportationRepository: UserImportationRepository,
    private val personImportationRepository: PersonImportationRepository,
) : FitnessProCoroutineWorker(context, workerParams) {

    override fun onError(e: Exception) {
        e.sendToFirebaseCrashlytics()
    }

    override suspend fun onWork() {
        academyImportationRepository.import()
        userImportationRepository.import()
        personImportationRepository.import()
    }
}