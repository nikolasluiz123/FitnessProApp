package br.com.fitnesspro.scheduler.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.workers.common.AbstractImportationWorker
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.scheduler.injection.IScheduleWorkersEntryPoint
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import java.time.LocalDateTime

@HiltWorker
class SchedulerModuleImportationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
) : AbstractImportationWorker(context, workerParams) {

    private val scheduleEntryPoint = EntryPointAccessors.fromApplication(context, IScheduleWorkersEntryPoint::class.java)

    override suspend fun onImport(serviceToken: String, lastUpdateDate: LocalDateTime?) {
        scheduleEntryPoint.getSchedulerImportationRepository().import(serviceToken, lastUpdateDate)
        scheduleEntryPoint.getReportsFromSchedulerImportationRepository().import(serviceToken, lastUpdateDate)
        scheduleEntryPoint.getSchedulerReportImportationRepository().import(serviceToken, lastUpdateDate)
    }

    override fun getModule() = EnumSyncModule.SCHEDULER

    override fun getClazz() = javaClass

    override fun getOneTimeWorkRequestBuilder(): OneTimeWorkRequest.Builder {
        return OneTimeWorkRequestBuilder<SchedulerModuleImportationWorker>()
    }
}