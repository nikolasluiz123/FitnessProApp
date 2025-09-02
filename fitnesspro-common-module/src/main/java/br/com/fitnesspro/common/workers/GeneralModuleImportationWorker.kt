package br.com.fitnesspro.common.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import br.com.fitnesspro.common.injection.IGeneralWorkerEntryPoint
import br.com.fitnesspro.common.workers.common.AbstractAuthenticatedImportationWorker
import br.com.fitnesspro.model.enums.EnumImportationModule
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.EntryPointAccessors
import java.time.LocalDateTime

@HiltWorker
class GeneralModuleImportationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
) : AbstractAuthenticatedImportationWorker(context, workerParams) {

    private val entryPoint = EntryPointAccessors.fromApplication(context, IGeneralWorkerEntryPoint::class.java)

    override suspend fun onImport(serviceToken: String, lastUpdateDate: LocalDateTime?) {
        entryPoint.getGeneralModuleImportationRepository().import(serviceToken, lastUpdateDate)
    }

    override fun getModule() = EnumImportationModule.GENERAL

    override fun getClazz() = javaClass

    override fun getOneTimeWorkRequestBuilder(): OneTimeWorkRequest.Builder {
        return OneTimeWorkRequestBuilder<GeneralModuleImportationWorker>()
    }
}