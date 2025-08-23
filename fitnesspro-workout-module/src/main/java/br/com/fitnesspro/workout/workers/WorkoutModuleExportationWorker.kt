package br.com.fitnesspro.workout.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.workers.common.AbstractExportationWorker
import br.com.fitnesspro.workout.injection.IWorkoutWorkersEntryPoint
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors

@HiltWorker
class WorkoutModuleExportationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
) : AbstractExportationWorker(context, workerParams) {

    private val entryPoint = EntryPointAccessors.fromApplication(context, IWorkoutWorkersEntryPoint::class.java)

    override suspend fun onExport(serviceToken: String) {
        entryPoint.getWorkoutExportationRepository().export(serviceToken)
        entryPoint.getWorkoutGroupExportationRepository().export(serviceToken)
        entryPoint.getExerciseExportationRepository().export(serviceToken)
        entryPoint.getVideoExportationRepository().export(serviceToken)
        entryPoint.getVideoExerciseExportationRepository().export(serviceToken)

        entryPoint.getExerciseExecutionExportationRepository().export(serviceToken)
        entryPoint.getVideoExerciseExecutionExportationRepository().export(serviceToken)

        entryPoint.getWorkoutGroupPreDefinitionExportationRepository().export(serviceToken)
        entryPoint.getExercisePreDefinitionExportationRepository().export(serviceToken)
        entryPoint.getVideoExercisePreDefinitionExportationRepository().export(serviceToken)
    }

    override fun getClazz() = javaClass

    override fun getOneTimeWorkRequestBuilder(): OneTimeWorkRequest.Builder {
        return OneTimeWorkRequestBuilder<WorkoutModuleExportationWorker>()
    }
}