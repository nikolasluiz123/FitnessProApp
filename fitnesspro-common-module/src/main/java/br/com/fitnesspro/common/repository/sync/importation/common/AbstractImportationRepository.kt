package br.com.fitnesspro.common.repository.sync.importation.common

import android.content.Context
import br.com.fitnesspro.common.repository.sync.common.AbstractSyncRepository
import br.com.fitnesspro.core.exceptions.ServiceException
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.local.data.access.dao.common.MaintenanceDAO
import br.com.fitnesspro.model.base.BaseModel
import br.com.fitnesspro.shared.communication.dtos.common.BaseDTO
import br.com.fitnesspro.shared.communication.dtos.logs.UpdatableExecutionLogInfosDTO
import br.com.fitnesspro.shared.communication.dtos.logs.UpdatableExecutionLogPackageInfosDTO
import br.com.fitnesspro.shared.communication.enums.execution.EnumExecutionState
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.CommonImportFilter
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.ReadServiceResponse
import java.time.LocalDateTime

abstract class AbstractImportationRepository<DTO: BaseDTO, MODEL: BaseModel, DAO: MaintenanceDAO<MODEL>>(context: Context)
    : AbstractSyncRepository<DAO>(context) {

    abstract suspend fun getImportationData(
        token: String,
        filter: CommonImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<DTO>

    abstract suspend fun hasEntityWithId(id: String): Boolean

    abstract suspend fun convertDTOToEntity(dto: DTO): MODEL

    suspend fun import(serviceToken: String, lastUpdateDate: LocalDateTime?) {
        val importFilter = CommonImportFilter(lastUpdateDate = lastUpdateDate?.minusMinutes(5))
        val pageInfos = ImportPageInfos(pageSize = getPageSize())

        lateinit var response: ImportationServiceResponse<DTO>
        lateinit var clientStartDateTime: LocalDateTime

        try {
            do {
                clientStartDateTime = dateTimeNow()

                response = getImportationData(serviceToken, importFilter, pageInfos)

                updateLogWithStartRunningInfos(
                    serviceToken = serviceToken,
                    logPackageId = response.executionLogPackageId,
                    logId = response.executionLogId,
                    importFilter = importFilter,
                    pageInfos = pageInfos,
                    clientStartDateTime = clientStartDateTime
                )

                when {
                    response.success && response.values.isEmpty() -> {
                        updateExecutionLogPackageWithSuccessIterationInfos(
                            logPackageId = response.executionLogPackageId,
                            insertionList = emptyList(),
                            updateList = emptyList(),
                            serviceToken = serviceToken
                        )
                    }

                    response.success -> {
                        val (insertionList, updateList) = executeSegregation(response)

                        saveDataLocally(insertionList, updateList)

                        updateExecutionLogPackageWithSuccessIterationInfos(
                            logPackageId = response.executionLogPackageId,
                            insertionList = insertionList,
                            updateList = updateList,
                            serviceToken = serviceToken
                        )

                        pageInfos.pageNumber++
                    }

                    else -> {
                        throw ServiceException(response.error!!)
                    }
                }
            } while (response.values.size == pageInfos.pageSize)

            updateLogWithFinalizationInfos(serviceToken, response.executionLogId)
        } catch (ex: Exception) {
            updateLogPackageWithErrorInfos(
                serviceToken = serviceToken,
                logId = response.executionLogId,
                logPackageId = response.executionLogPackageId,
                exception = ex,
                clientStartDateTime = clientStartDateTime
            )

            throw ex
        }
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
                clientExecutionEnd = dateTimeNow(),
                error = exception.stackTraceToString(),
            )
        )
    }

    private suspend fun updateLogWithStartRunningInfos(
        serviceToken: String,
        logId: String,
        logPackageId: String,
        importFilter: CommonImportFilter,
        pageInfos: ImportPageInfos,
        clientStartDateTime: LocalDateTime
    ) {
        executionLogWebClient.updateLogInformation(
            token = serviceToken,
            logId = logId,
            dto = UpdatableExecutionLogInfosDTO(
                lastUpdateDate = importFilter.lastUpdateDate,
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
        insertionList: List<MODEL>,
        updateList: List<MODEL>,
    ) {
        executionLogWebClient.updateLogPackageInformation(
            token = serviceToken,
            logPackageId = logPackageId,
            dto = UpdatableExecutionLogPackageInfosDTO(
                insertedItemsCount = insertionList.size,
                updatedItemsCount = updateList.size,
                clientExecutionEnd = dateTimeNow()
            )
        )
    }

    private suspend fun updateLogWithFinalizationInfos(
        serviceToken: String,
        logId: String,
    ) {
        executionLogWebClient.updateLogInformation(
            token = serviceToken,
            logId = logId,
            dto = UpdatableExecutionLogInfosDTO(
                state = EnumExecutionState.FINISHED
            )
        )
    }

    private suspend fun executeSegregation(response: ReadServiceResponse<DTO>): Pair<List<MODEL>, List<MODEL>> {
        val insertionList = response.values
            .filter { !hasEntityWithId(it.id!!) }
            .map { convertDTOToEntity(it) }

        val updateList = response.values
            .filter { hasEntityWithId(it.id!!) }
            .map { convertDTOToEntity(it) }

        return Pair(insertionList, updateList)
    }

    private suspend fun saveDataLocally(insertionList: List<MODEL>, updateList: List<MODEL>) {
        if (insertionList.isNotEmpty()) {
            getOperationDAO().insertBatch(insertionList)
        }

        if (updateList.isNotEmpty()) {
            getOperationDAO().updateBatch(updateList)
        }
    }
}