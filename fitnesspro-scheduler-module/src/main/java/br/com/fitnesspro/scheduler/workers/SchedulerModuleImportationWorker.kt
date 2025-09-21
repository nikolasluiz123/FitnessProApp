package br.com.fitnesspro.scheduler.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.workers.common.AbstractAuthenticatedImportationWorker
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.scheduler.injection.IScheduleWorkersEntryPoint
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors

@HiltWorker
class SchedulerModuleImportationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
) : AbstractAuthenticatedImportationWorker(context, workerParams) {

    private val scheduleEntryPoint = EntryPointAccessors.fromApplication(context, IScheduleWorkersEntryPoint::class.java)

    override suspend fun onImport(serviceToken: String) {
        return scheduleEntryPoint.getSchedulerModuleImportationRepository().import(serviceToken)
    }

    override fun getModule() = EnumSyncModule.SCHEDULER

    override fun getClazz() = javaClass

    override fun getOneTimeWorkRequestBuilder(): OneTimeWorkRequest.Builder {
        return OneTimeWorkRequestBuilder<SchedulerModuleImportationWorker>()
    }

    override fun getWorkerDelay(): Long = DEFAULT_WORKER_DELAY
}