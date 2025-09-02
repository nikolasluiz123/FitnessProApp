package br.com.fitnesspro.scheduler.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.workers.common.AbstractAuthenticatedImportationWorker
import br.com.fitnesspro.model.enums.EnumImportationModule
import br.com.fitnesspro.scheduler.injection.IScheduleWorkersEntryPoint
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import java.time.LocalDateTime

@HiltWorker
class SchedulerModuleImportationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
) : AbstractAuthenticatedImportationWorker(context, workerParams) {

    private val scheduleEntryPoint = EntryPointAccessors.fromApplication(context, IScheduleWorkersEntryPoint::class.java)

    override suspend fun onImport(serviceToken: String, lastUpdateDate: LocalDateTime?) {
        scheduleEntryPoint.getSchedulerModuleImportationRepository().import(serviceToken, lastUpdateDate)
    }

    override fun getModule() = EnumImportationModule.SCHEDULER

    override fun getClazz() = javaClass

    override fun getOneTimeWorkRequestBuilder(): OneTimeWorkRequest.Builder {
        return OneTimeWorkRequestBuilder<SchedulerModuleImportationWorker>()
    }
}