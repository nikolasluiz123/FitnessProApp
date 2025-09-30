package br.com.fitnesspro.workout.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.workers.common.importation.onetime.AbstractFitnessProImportationAuthOneTimeWorker
import br.com.fitnesspro.workout.injection.IWorkoutWorkersEntryPoint
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors

@HiltWorker
class WorkoutModuleImportationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
) : AbstractFitnessProImportationAuthOneTimeWorker(context, workerParams) {

    private val entryPoint = EntryPointAccessors.fromApplication(context, IWorkoutWorkersEntryPoint::class.java)

    override suspend fun onImport(serviceToken: String) {
        entryPoint.getWorkoutModuleImportationRepository().import(serviceToken)
    }

    override fun getClazz() = javaClass

    override fun getOneTimeWorkRequestBuilder(): OneTimeWorkRequest.Builder {
        return OneTimeWorkRequestBuilder<WorkoutModuleImportationWorker>()
    }
}