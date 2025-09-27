package br.com.fitnesspro.common.repository.sync.exportation.common

import android.content.Context
import android.util.Log
import br.com.fitnesspro.common.repository.sync.common.AbstractSyncRepository
import br.com.fitnesspro.core.exceptions.ServiceException
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.core.utils.FileUtils
import br.com.fitnesspro.core.worker.LogConstants
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.model.base.FileModel
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.base.StorageModel
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.shared.communication.dtos.logs.UpdatableExecutionLogInfosDTO
import br.com.fitnesspro.shared.communication.dtos.logs.UpdatableExecutionLogPackageInfosDTO
import br.com.fitnesspro.shared.communication.enums.execution.EnumExecutionState
import br.com.fitnesspro.shared.communication.responses.StorageServiceResponse
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset

abstract class AbstractStorageExportationRepository<MODEL, DAO: IntegratedMaintenanceDAO<MODEL>>(context: Context) : AbstractSyncRepository(context)
    where MODEL: IntegratedModel, MODEL: StorageModel, MODEL: FileModel {

    abstract suspend fun getExportationModels(pageSize: Int): List<MODEL>

    abstract suspend fun callExportationService(modelIds: List<String>, files: List<File>, token: String): StorageServiceResponse

    abstract fun getOperationDAO(): DAO

    open fun getExportationFiles(paths: List<String>): List<File> {
        return FileUtils.getFileListFromPaths(paths)
    }

    suspend fun export(serviceToken: String) {
        Log.i(LogConstants.WORKER_EXPORT, "Exportando ${javaClass.simpleName}")

        var response: StorageServiceResponse? = null
        var clientDateTimeStart: LocalDateTime? = null
        var models: List<MODEL>
        var files: List<File>
        val pageSize = getPageSize()

        try {

            do {
                clientDateTimeStart = dateTimeNow(ZoneOffset.UTC)
                models = getExportationModels(pageSize).filter {
                    it.filePath?.let(FileUtils::getFileExists) ?: false
                }

                if (models.isNotEmpty()) {
                    Log.i(LogConstants.WORKER_EXPORT, "${models.size} arquivos")

                    val paths: MutableList<String> = mutableListOf()
                    val modelIds: MutableList<String> = mutableListOf()

                    models.forEach { model ->
                        paths += model.filePath!!
                        modelIds += model.id
                    }

                    files = getExportationFiles(paths)

                    updateTransmissionState(models, EnumTransmissionState.RUNNING)

                    val serviceCallExportationStart = dateTimeNow(ZoneOffset.UTC)
                    response = callExportationService(modelIds, files, serviceToken)
                    val serviceCallExportationEnd = dateTimeNow(ZoneOffset.UTC)

                    val callExportationTime = Duration.between(serviceCallExportationStart, serviceCallExportationEnd)

                    updateLogWithStartRunningInfos(
                        serviceToken = serviceToken,
                        logPackageId = response.executionLogPackageId,
                        logId = response.executionLogId,
                        pageSize = pageSize,
                        clientStartDateTime = clientDateTimeStart
                    )

                    if (response.success) {
                        updateTransmissionState(models, EnumTransmissionState.TRANSMITTED)

                        updateExecutionLogPackageWithSuccessIterationInfos(
                            logPackageId = response.executionLogPackageId,
                            models = models,
                            serviceToken = serviceToken,
                            clientExecutionEnd = dateTimeNow(ZoneOffset.UTC).minus(callExportationTime)
                        )

                    } else {
                        throw ServiceException(response.error!!)
                    }
                }
            } while (models.size == pageSize)

            if (models.isNotEmpty()) {
                updateLogWithFinalizationInfos(serviceToken, response?.executionLogId!!)
            }

        } catch (ex: Exception) {
            response?.let { serviceResponse ->
                updateLogPackageWithErrorInfos(
                    serviceToken = serviceToken,
                    logId = serviceResponse.executionLogId,
                    logPackageId = serviceResponse.executionLogPackageId,
                    exception = ex,
                    clientStartDateTime = clientDateTimeStart!!
                )
            }

            throw ex
        }
    }

    private suspend fun updateTransmissionState(models: List<MODEL>, transmissionState: EnumTransmissionState) {
        models.forEach {
            it.storageTransmissionState = transmissionState
        }

        getOperationDAO().updateBatch(models, writeTransmissionState = false)
    }

    protected suspend fun updateLogWithStartRunningInfos(
        serviceToken: String,
        logId: String,
        logPackageId: String,
        pageSize: Int,
        clientStartDateTime: LocalDateTime
    ) {
        executionLogWebClient.updateLogInformation(
            token = serviceToken,
            logId = logId,
            dto = UpdatableExecutionLogInfosDTO(
                pageSize = pageSize
            )
        )

        executionLogWebClient.updateLogPackageInformation(
            token = serviceToken,
            logPackageId = logPackageId,
            dto = UpdatableExecutionLogPackageInfosDTO(
                clientExecutionStart = clientStartDateTime
            )
        )
    }

    protected suspend fun updateExecutionLogPackageWithSuccessIterationInfos(
        serviceToken: String,
        logPackageId: String,
        models: List<MODEL>,
        clientExecutionEnd: LocalDateTime
    ) {
        executionLogWebClient.updateLogPackageInformation(
            token = serviceToken,
            logPackageId = logPackageId,
            dto = UpdatableExecutionLogPackageInfosDTO(
                clientExecutionEnd = clientExecutionEnd
            )
        )
    }

    protected suspend fun updateLogWithFinalizationInfos(serviceToken: String, logId: String) {
        executionLogWebClient.updateLogInformation(
            token = serviceToken,
            logId = logId,
            dto = UpdatableExecutionLogInfosDTO(
                state = EnumExecutionState.FINISHED
            )
        )
    }

    protected suspend fun updateLogPackageWithErrorInfos(
        serviceToken: String,
        logId: String,
        logPackageId: String,
        exception: Exception,
        clientStartDateTime: LocalDateTime
    ) {
        executionLogWebClient.updateLogInformation(
            token = serviceToken,
            logId = logId,
            dto = UpdatableExecutionLogInfosDTO(
                state = EnumExecutionState.ERROR
            )
        )

        executionLogWebClient.updateLogPackageInformation(
            token = serviceToken,
            logPackageId = logPackageId,
            dto = UpdatableExecutionLogPackageInfosDTO(
                clientExecutionStart = clientStartDateTime,
                clientExecutionEnd = dateTimeNow(ZoneOffset.UTC),
                error = exception.stackTraceToString(),
            )
        )
    }
}