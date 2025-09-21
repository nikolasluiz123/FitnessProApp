package br.com.fitnesspro.common.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.injection.IGeneralWorkerEntryPoint
import br.com.fitnesspro.common.workers.common.AbstractExportationWorker
import br.com.fitnesspro.model.enums.EnumSyncModule
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors

@HiltWorker
class GeneralModuleExportationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : AbstractExportationWorker(context, workerParams) {

    private val entryPoint = EntryPointAccessors.fromApplication(context, IGeneralWorkerEntryPoint::class.java)

    override suspend fun onExport(serviceToken: String) {
        entryPoint.getGeneralModuleExportationRepository().export(serviceToken)
    }

    override fun getClazz() = javaClass

    override fun getModule() = EnumSyncModule.GENERAL

    override fun getOneTimeWorkRequestBuilder(): OneTimeWorkRequest.Builder {
        return OneTimeWorkRequestBuilder<GeneralModuleExportationWorker>()
    }

    override fun getWorkerDelay(): Long = DEFAULT_WORKER_DELAY

}