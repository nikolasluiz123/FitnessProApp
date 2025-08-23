package br.com.fitnesspro.common.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.injection.ICommonWorkersEntryPoint
import br.com.fitnesspro.common.workers.common.AbstractExportationWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors

@HiltWorker
class GeneralModuleExportationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : AbstractExportationWorker(context, workerParams) {

    private val entryPoint = EntryPointAccessors.fromApplication(context, ICommonWorkersEntryPoint::class.java)

    override suspend fun onExport(serviceToken: String) {
        entryPoint.getPersonExportationRepository().export(serviceToken)
        entryPoint.getPersonAcademyTimeExportationRepository().export(serviceToken)
    }

    override fun getClazz() = javaClass

    override fun getOneTimeWorkRequestBuilder(): OneTimeWorkRequest.Builder {
        return OneTimeWorkRequestBuilder<GeneralModuleExportationWorker>()
    }

}