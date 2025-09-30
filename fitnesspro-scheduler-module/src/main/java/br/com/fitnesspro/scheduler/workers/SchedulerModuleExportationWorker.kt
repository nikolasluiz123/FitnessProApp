package br.com.fitnesspro.scheduler.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.workers.common.exportation.AbstractFitnessProExportationAuthOneTimeWorker
import br.com.fitnesspro.scheduler.injection.IScheduleWorkersEntryPoint
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors

@HiltWorker
class SchedulerModuleExportationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
) : AbstractFitnessProExportationAuthOneTimeWorker(context, workerParams) {

    private val scheduleEntryPoint = EntryPointAccessors.fromApplication(context, IScheduleWorkersEntryPoint::class.java)

    override suspend fun onExport(serviceToken: String) {
        scheduleEntryPoint.getSchedulerModuleExportationRepository().export(serviceToken)
    }

    override fun getClazz() = javaClass

    override fun getOneTimeWorkRequestBuilder(): OneTimeWorkRequest.Builder {
        return OneTimeWorkRequestBuilder<SchedulerModuleExportationWorker>()
    }
}