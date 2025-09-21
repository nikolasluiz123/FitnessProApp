package br.com.fitnesspro.scheduler.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.workers.common.AbstractExportationWorker
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.scheduler.injection.IScheduleWorkersEntryPoint
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors

@HiltWorker
class SchedulerModuleExportationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
) : AbstractExportationWorker(context, workerParams) {

    private val scheduleEntryPoint = EntryPointAccessors.fromApplication(context, IScheduleWorkersEntryPoint::class.java)

    override suspend fun onExport(serviceToken: String) {
        scheduleEntryPoint.getSchedulerModuleExportationRepository().export(serviceToken)
    }

    override fun getClazz() = javaClass

    override fun getModule() = EnumSyncModule.SCHEDULER

    override fun getOneTimeWorkRequestBuilder(): OneTimeWorkRequest.Builder {
        return OneTimeWorkRequestBuilder<SchedulerModuleExportationWorker>()
    }

    override fun getWorkerDelay(): Long = DEFAULT_WORKER_DELAY
}