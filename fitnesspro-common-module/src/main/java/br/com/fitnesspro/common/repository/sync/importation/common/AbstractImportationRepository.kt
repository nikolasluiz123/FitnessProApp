package br.com.fitnesspro.common.repository.sync.importation.common

import android.content.Context
import android.util.Log
import br.com.fitnesspro.common.repository.sync.common.AbstractSyncRepository
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.exceptions.ServiceException
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.worker.LogConstants
import br.com.fitnesspro.local.data.access.dao.common.MaintenanceDAO
import br.com.fitnesspro.model.base.BaseModel
import br.com.fitnesspro.shared.communication.dtos.common.BaseDTO
import br.com.fitnesspro.shared.communication.dtos.logs.UpdatableExecutionLogInfosDTO
import br.com.fitnesspro.shared.communication.dtos.logs.UpdatableExecutionLogPackageInfosDTO
import br.com.fitnesspro.shared.communication.dtos.sync.interfaces.ISyncDTO
import br.com.fitnesspro.shared.communication.enums.execution.EnumExecutionState
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.CommonImportFilter
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.reflect.KClass

abstract class AbstractImportationRepository<DTO: ISyncDTO, FILTER: CommonImportFilter>(context: Context)
    : AbstractSyncRepository(context) {

    abstract suspend fun getImportationData(
        token: String,
        filter: FILTER,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<DTO>

    abstract suspend fun executeSegregation(dto: DTO): List<ImportSegregationResult<BaseModel>>

    abstract fun convertDTOToEntity(dto: BaseDTO): BaseModel

    abstract fun getMaintenanceDAO(modelClass: KClass<out BaseModel>): MaintenanceDAO<out BaseModel>

    @Suppress("UNCHECKED_CAST")
    open suspend fun getImportFilter(lastUpdateDate: LocalDateTime?): FILTER {
        return CommonImportFilter(lastUpdateDate = lastUpdateDate?.minusMinutes(5)) as FILTER
    }

    suspend fun import(serviceToken: String, lastUpdateDate: LocalDateTime?) {
        Log.i(LogConstants.WORKER_IMPORT, "Importando ${javaClass.simpleName} lastUpdateDate = ${lastUpdateDate?.format(EnumDateTimePatterns.DATE_TIME_SHORT)}")

        var response: ImportationServiceResponse<DTO>? = null
        var clientStartDateTime: LocalDateTime? = null

        try {
            val importFilter = getImportFilter(lastUpdateDate)
            val pageInfos = ImportPageInfos(pageSize = getPageSize())

            do {
                clientStartDateTime = dateTimeNow(ZoneOffset.UTC)

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
                    response.success && response.value!!.isEmpty() -> {
                        Log.i(LogConstants.WORKER_IMPORT, "Sucesso Sem Dados")

                        updateExecutionLogPackageWithSuccessIterationInfos(
                            logPackageId = response.executionLogPackageId,
                            insertionListCount = 0,
                            updateListCount = 0,
                            serviceToken = serviceToken
                        )
                    }

                    response.success -> {
                        Log.i(LogConstants.WORKER_IMPORT, "Sucesso Com Dados Novos. pageNumber = ${pageInfos.pageNumber} pageSize = ${pageInfos.pageSize} maxListSize = ${response.value?.getMaxListSize()}")

                        val segregationResult = executeSegregation(response.value!!)

                        saveDataLocally(segregationResult)

                        updateExecutionLogPackageWithSuccessIterationInfos(
                            logPackageId = response.executionLogPackageId,
                            insertionListCount = segregationResult.sumOf { it.insertionList.size },
                            updateListCount = segregationResult.sumOf { it.updateList.size },
                            serviceToken = serviceToken
                        )

                        pageInfos.pageNumber++
                    }

                    else -> {
                        throw ServiceException(response.error!!)
                    }
                }
            } while (response.value?.getMaxListSize() == pageInfos.pageSize)

            updateLogWithFinalizationInfos(serviceToken, response.executionLogId)
        } catch (ex: Exception) {
            response?.let { serviceResponse ->
                updateLogPackageWithErrorInfos(
                    serviceToken = serviceToken,
                    logId = serviceResponse.executionLogId,
                    logPackageId = serviceResponse.executionLogPackageId,
                    exception = ex,
                    clientStartDateTime = clientStartDateTime
                )
            }

            throw ex
        }
    }

    protected suspend fun segregate(dtoList: List<BaseDTO>, hasEntityWithId: suspend (String) -> Boolean): ImportSegregationResult<BaseModel>? {
        return if (dtoList.isNotEmpty()) {
            val insertionList = dtoList
                .filter { !hasEntityWithId(it.id!!) }
                .map { convertDTOToEntity(it) }

            val updateList = dtoList
                .filter { hasEntityWithId(it.id!!) }
                .map { convertDTOToEntity(it) }

            ImportSegregationResult(insertionList, updateList)
        } else {
            null
        }
    }

    protected suspend fun saveDataLocally(segregationResult: List<ImportSegregationResult<BaseModel>>) {
        segregationResult.forEach { result ->
            if (result.insertionList.isNotEmpty() || result.updateList.isNotEmpty()) {
                val clazz = (result.insertionList.firstOrNull() ?: result.updateList.firstOrNull())!!::class
                val maintenanceDAO = getMaintenanceDAO(clazz) as MaintenanceDAO<BaseModel>

                if (result.insertionList.isNotEmpty()) {
                    maintenanceDAO.insertBatch(result.insertionList)
                }

                if (result.updateList.isNotEmpty()) {
                    maintenanceDAO.updateBatch(result.updateList)
                }
            }
        }
    }

    private suspend fun updateLogPackageWithErrorInfos(
        serviceToken: String,
        logId: String,
        logPackageId: String,
        exception: Exception,
        clientStartDateTime: LocalDateTime?
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
        insertionListCount: Int,
        updateListCount: Int,
    ) {
        executionLogWebClient.updateLogPackageInformation(
            token = serviceToken,
            logPackageId = logPackageId,
            dto = UpdatableExecutionLogPackageInfosDTO(
                insertedItemsCount = insertionListCount,
                updatedItemsCount = updateListCount,
                clientExecutionEnd = dateTimeNow(ZoneOffset.UTC)
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
}