package br.com.fitnesspro.workout.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.workers.common.importation.periodic.AbstractFitnessProImportationHealthConnectPeriodicWorker
import br.com.fitnesspro.workout.injection.IWorkoutHealthConnectIntegrationWorkersEntryPoint
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors

@HiltWorker
class WorkoutHealthConnectIntegrationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
): AbstractFitnessProImportationHealthConnectPeriodicWorker(context, workerParams) {

    private val entryPoint = EntryPointAccessors.fromApplication(
        context,
        IWorkoutHealthConnectIntegrationWorkersEntryPoint::class.java
    )

    override suspend fun onImport() {
        entryPoint.getHealthConnectIntegrationRepository().runIntegration()
    }
}