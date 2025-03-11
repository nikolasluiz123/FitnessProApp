package br.com.fitnesspro.common.repository.sync.exportation.common

import android.content.Context
import br.com.fitnesspro.common.repository.sync.common.AbstractSyncRepository
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.shared.communication.dtos.common.BaseDTO
import br.com.fitnesspro.shared.communication.dtos.logs.UpdatableExecutionLogInfosDTO
import br.com.fitnesspro.shared.communication.dtos.logs.UpdatableExecutionLogPackageInfosDTO
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import java.time.Duration
import java.time.LocalDateTime

abstract class AbstractExportationRepository<DTO: BaseDTO, MODEL: IntegratedModel, DAO: IntegratedMaintenanceDAO<MODEL>>(context: Context)
    : AbstractSyncRepository<MODEL, DAO>(context) {

    abstract suspend fun getExportationData(pageInfos: ExportPageInfos): List<MODEL>

    abstract suspend fun callExportationService(modelList: List<MODEL>, token: String): ExportationServiceResponse

    suspend fun export(serviceToken: String) {
        val pageInfos = ExportPageInfos(pageSize = getPageSize())

        do {
            val clientDateTimeStart = dateTimeNow()

            val models = getExportationData(pageInfos)

            if (models.isNotEmpty()) {
                updateTransmissionState(models, EnumTransmissionState.RUNNING)

                val serviceCallExportationStart = dateTimeNow()
                val response = callExportationService(models, serviceToken)
                val serviceCallExportationEnd = dateTimeNow()

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
                        clientExecutionEnd = dateTimeNow().minus(callExportationTime)
                    )

                    pageInfos.pageNumber++
                }
            }
        } while (models.size == pageInfos.pageSize)
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
}