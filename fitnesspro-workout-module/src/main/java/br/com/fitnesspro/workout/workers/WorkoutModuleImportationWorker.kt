package br.com.fitnesspro.workout.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.workers.common.AbstractImportationWorker
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.workout.injection.IWorkoutWorkersEntryPoint
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import java.time.LocalDateTime

@HiltWorker
class WorkoutModuleImportationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
) : AbstractImportationWorker(context, workerParams) {

    private val entryPoint = EntryPointAccessors.fromApplication(context, IWorkoutWorkersEntryPoint::class.java)

    override suspend fun onImport(serviceToken: String, lastUpdateDate: LocalDateTime?) {
        entryPoint.getWorkoutImportationRepository().import(serviceToken, lastUpdateDate)
        entryPoint.getWorkoutGroupImportationRepository().import(serviceToken, lastUpdateDate)
        entryPoint.getExerciseImportationRepository().import(serviceToken, lastUpdateDate)
        entryPoint.getVideoImportationRepository().import(serviceToken, lastUpdateDate)
        entryPoint.getVideoExerciseImportationRepository().import(serviceToken, lastUpdateDate)

        entryPoint.getExerciseExecutionImportationRepository().import(serviceToken, lastUpdateDate)
        entryPoint.getVideoExerciseExecutionImportationRepository().import(serviceToken, lastUpdateDate)

        entryPoint.getWorkoutGroupPreDefinitionImportationRepository().import(serviceToken, lastUpdateDate)
        entryPoint.getExercisePreDefinitionImportationRepository().import(serviceToken, lastUpdateDate)
        entryPoint.getVideoExercisePreDefinitionImportationRepository().import(serviceToken, lastUpdateDate)
    }

    override fun getModule() = EnumSyncModule.WORKOUT

    override fun getClazz() = javaClass

    override fun getOneTimeWorkRequestBuilder(): OneTimeWorkRequest.Builder {
        return OneTimeWorkRequestBuilder<WorkoutModuleImportationWorker>()
    }
}