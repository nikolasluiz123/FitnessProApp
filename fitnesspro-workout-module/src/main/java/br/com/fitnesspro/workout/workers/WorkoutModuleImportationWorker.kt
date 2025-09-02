package br.com.fitnesspro.workout.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.workers.common.AbstractAuthenticatedImportationWorker
import br.com.fitnesspro.model.enums.EnumImportationModule
import br.com.fitnesspro.workout.injection.IWorkoutWorkersEntryPoint
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import java.time.LocalDateTime

@HiltWorker
class WorkoutModuleImportationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
) : AbstractAuthenticatedImportationWorker(context, workerParams) {

    private val entryPoint = EntryPointAccessors.fromApplication(context, IWorkoutWorkersEntryPoint::class.java)

    override suspend fun onImport(serviceToken: String, lastUpdateDate: LocalDateTime?) {
        entryPoint.getWorkoutModuleImportationRepository().import(serviceToken, lastUpdateDate)
    }

    override fun getModule() = EnumImportationModule.WORKOUT

    override fun getClazz() = javaClass

    override fun getOneTimeWorkRequestBuilder(): OneTimeWorkRequest.Builder {
        return OneTimeWorkRequestBuilder<WorkoutModuleImportationWorker>()
    }
}