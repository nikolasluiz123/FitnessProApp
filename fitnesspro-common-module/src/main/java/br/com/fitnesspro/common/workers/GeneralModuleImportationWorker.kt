package br.com.fitnesspro.common.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.injection.ICommonWorkersEntryPoint
import br.com.fitnesspro.common.workers.common.AbstractAuthenticatedImportationWorker
import br.com.fitnesspro.model.enums.EnumSyncModule
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import java.time.LocalDateTime

@HiltWorker
class GeneralModuleImportationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
) : AbstractAuthenticatedImportationWorker(context, workerParams) {

    private val entryPoint = EntryPointAccessors.fromApplication(context, ICommonWorkersEntryPoint::class.java)

    override suspend fun onImport(serviceToken: String, lastUpdateDate: LocalDateTime?) {
        entryPoint.getAcademyImportationRepository().import(serviceToken, lastUpdateDate)
        entryPoint.getUserImportationRepository().import(serviceToken, lastUpdateDate)
        entryPoint.getPersonImportationRepository().import(serviceToken, lastUpdateDate)
        entryPoint.getPersonAcademyTimeImportationRepository().import(serviceToken, lastUpdateDate)
        entryPoint.getSchedulerConfigImportationRepository().import(serviceToken, lastUpdateDate)
    }

    override fun getModule() = EnumSyncModule.GENERAL

    override fun getClazz() = javaClass

    override fun getOneTimeWorkRequestBuilder(): OneTimeWorkRequest.Builder {
        return OneTimeWorkRequestBuilder<GeneralModuleImportationWorker>()
    }
}