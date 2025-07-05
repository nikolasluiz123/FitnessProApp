package br.com.fitnesspro.common.repository.sync.exportation.common

import android.content.Context
import br.com.fitnesspro.common.repository.sync.common.AbstractSyncRepository
import br.com.fitnesspro.core.exceptions.ServiceException
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.shared.communication.dtos.logs.UpdatableExecutionLogInfosDTO
import br.com.fitnesspro.shared.communication.dtos.logs.UpdatableExecutionLogPackageInfosDTO
import br.com.fitnesspro.shared.communication.enums.execution.EnumExecutionState
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset

abstract class AbstractExportationRepository<MODEL: IntegratedModel, DAO: IntegratedMaintenanceDAO<MODEL>>(context: Context)
    : AbstractSyncRepository<DAO>(context) {

    abstract suspend fun getExportationData(pageInfos: ExportPageInfos): List<MODEL>

    abstract suspend fun callExportationService(modelList: List<MODEL>, token: String): ExportationServiceResponse

    override fun getPageSize(): Int = 100

    suspend fun export(serviceToken: String) {
        var response: ExportationServiceResponse? = null
        var clientDateTimeStart: LocalDateTime? = null
        var models: List<MODEL>

        try {
            val pageInfos = ExportPageInfos(pageSize = getPageSize())

            do {
                clientDateTimeStart = dateTimeNow(ZoneOffset.UTC)

                models = getExportationData(pageInfos)

                if (models.isNotEmpty()) {
                    updateTransmissionState(models, EnumTransmissionState.RUNNING)

                    val serviceCallExportationStart = dateTimeNow(ZoneOffset.UTC)
                    response = callExportationService(models, serviceToken)
                    val serviceCallExportationEnd = dateTimeNow(ZoneOffset.UTC)

                    val callExportationTime = Duration.between(serviceCallExportationStart, serviceCallExportationEnd)

                    updateLogWithStartRunningInfos(
                        serviceToken = serviceToken,
                        logPackageId = response.executionLogPackageId,
                        logId = response.executionLogId,
                        pageInfos = pageInfos,
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

                        pageInfos.pageNumber++
                    } else {
                        throw ServiceException(response.error!!)
                    }
                }
            } while (models.size == pageInfos.pageSize)

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
        models.forEach { it.transmissionState = transmissionState }
        getOperationDAO().updateBatch(models, writeTransmissionState = false)
    }

    private suspend fun updateLogWithStartRunningInfos(
        serviceToken: String,
        logId: String,
        logPackageId: String,
        pageInfos: ExportPageInfos,
        clientStartDateTime: LocalDateTime
    ) {
        executionLogWebClient.updateLogInformation(
            token = serviceToken,
            logId = logId,
            dto = UpdatableExecutionLogInfosDTO(
                pageSize = pageInfos.pageSize
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

    private suspend fun updateExecutionLogPackageWithSuccessIterationInfos(
        serviceToken: String,
        logPackageId: String,
        models: List<MODEL>,
        clientExecutionEnd: LocalDateTime
    ) {
        executionLogWebClient.updateLogPackageInformation(
            token = serviceToken,
            logPackageId = logPackageId,
            dto = UpdatableExecutionLogPackageInfosDTO(
                allItemsCount = models.size,
                clientExecutionEnd = clientExecutionEnd
            )
        )
    }

    private suspend fun updateLogWithFinalizationInfos(serviceToken: String, logId: String) {
        executionLogWebClient.updateLogInformation(
            token = serviceToken,
            logId = logId,
            dto = UpdatableExecutionLogInfosDTO(
                state = EnumExecutionState.FINISHED
            )
        )
    }

    private suspend fun updateLogPackageWithErrorInfos(
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