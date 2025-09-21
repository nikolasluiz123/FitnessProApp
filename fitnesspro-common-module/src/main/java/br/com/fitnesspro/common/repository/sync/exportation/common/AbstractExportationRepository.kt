package br.com.fitnesspro.common.repository.sync.exportation.common

import android.content.Context
import android.util.Log
import br.com.fitnesspro.common.repository.sync.common.AbstractSyncRepository
import br.com.fitnesspro.core.exceptions.ServiceException
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.core.worker.LogConstants
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.shared.communication.dtos.logs.UpdatableExecutionLogInfosDTO
import br.com.fitnesspro.shared.communication.dtos.logs.UpdatableExecutionLogPackageInfosDTO
import br.com.fitnesspro.shared.communication.dtos.sync.interfaces.ISyncDTO
import br.com.fitnesspro.shared.communication.enums.execution.EnumExecutionState
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.reflect.KClass

abstract class AbstractExportationRepository<DTO: ISyncDTO>(context: Context): AbstractSyncRepository(context) {

    abstract suspend fun getExportationData(pageSize: Int): Map<KClass<out IntegratedModel>, List<IntegratedModel>>

    abstract suspend fun getExportationDTO(models: Map<KClass<out IntegratedModel>, List<IntegratedModel>>): DTO

    abstract suspend fun callExportationService(dto: DTO, token: String): ExportationServiceResponse

    abstract fun getIntegratedMaintenanceDAO(modelClass: KClass<out IntegratedModel>): IntegratedMaintenanceDAO<out IntegratedModel>

    suspend fun export(serviceToken: String) {
        Log.i(LogConstants.WORKER_EXPORT, "Exportando ${javaClass.simpleName}")

        var response: ExportationServiceResponse? = null
        var clientDateTimeStart: LocalDateTime? = null
        var models: Map<KClass<out IntegratedModel>, List<IntegratedModel>>
        var syncDTO: DTO?
        var hasAnyListPopulated: Boolean
        val pageSize = getPageSize()

        try {
            clientDateTimeStart = dateTimeNow(ZoneOffset.UTC)
            models = getExportationData(pageSize)
            hasAnyListPopulated = models.any { it.value.isNotEmpty() }

            Log.i(LogConstants.WORKER_EXPORT, "hasAnyListPopulated = $hasAnyListPopulated")

            if (hasAnyListPopulated) {
                syncDTO = getExportationDTO(models)
                updateTransmissionState(models, EnumTransmissionState.RUNNING)

                val serviceCallExportationStart = dateTimeNow(ZoneOffset.UTC)
                response = callExportationService(syncDTO, serviceToken)
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
                    Log.i(LogConstants.WORKER_EXPORT, "Sucesso pageSize = $pageSize maxListSize = ${syncDTO.getMaxListSize()}")
                    updateTransmissionState(models, EnumTransmissionState.TRANSMITTED)

                    updateExecutionLogPackageWithSuccessIterationInfos(
                        logPackageId = response.executionLogPackageId,
                        dto = syncDTO,
                        serviceToken = serviceToken,
                        clientExecutionEnd = dateTimeNow(ZoneOffset.UTC).minus(callExportationTime)
                    )
                } else {
                    throw ServiceException(response.error!!)
                }
            }

            if (hasAnyListPopulated) {
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

    private suspend fun updateTransmissionState(models: Map<KClass<out IntegratedModel>, List<IntegratedModel>>, transmissionState: EnumTransmissionState) {
        models.forEach { modelMap ->
            modelMap.value.forEach {
                it.transmissionState = transmissionState
            }

            val dao = getIntegratedMaintenanceDAO(modelMap.key) as IntegratedMaintenanceDAO<IntegratedModel>
            dao.updateBatch(modelMap.value, writeTransmissionState = false)
        }
    }

    private suspend fun updateLogWithStartRunningInfos(
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

    private suspend fun updateExecutionLogPackageWithSuccessIterationInfos(
        serviceToken: String,
        logPackageId: String,
        dto: DTO,
        clientExecutionEnd: LocalDateTime
    ) {
        executionLogWebClient.updateLogPackageInformation(
            token = serviceToken,
            logPackageId = logPackageId,
            dto = UpdatableExecutionLogPackageInfosDTO(
                allItemsCount = dto.getItemsCount(),
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