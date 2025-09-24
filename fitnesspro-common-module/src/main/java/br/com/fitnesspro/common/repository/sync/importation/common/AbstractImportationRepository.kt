package br.com.fitnesspro.common.repository.sync.importation.common

import android.content.Context
import android.util.Log
import br.com.fitnesspro.common.repository.sync.common.AbstractSyncRepository
import br.com.fitnesspro.core.exceptions.ServiceException
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.core.extensions.defaultGSon
import br.com.fitnesspro.core.worker.LogConstants
import br.com.fitnesspro.local.data.access.dao.common.MaintenanceDAO
import br.com.fitnesspro.model.base.BaseModel
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.sync.ImportationHistory
import br.com.fitnesspro.shared.communication.dtos.common.AuditableDTO
import br.com.fitnesspro.shared.communication.dtos.common.BaseDTO
import br.com.fitnesspro.shared.communication.dtos.logs.UpdatableExecutionLogInfosDTO
import br.com.fitnesspro.shared.communication.dtos.logs.UpdatableExecutionLogPackageInfosDTO
import br.com.fitnesspro.shared.communication.dtos.logs.UpdatableExecutionLogSubPackageEntityCountsDTO
import br.com.fitnesspro.shared.communication.dtos.logs.UpdatableExecutionLogSubPackageInfosDTO
import br.com.fitnesspro.shared.communication.dtos.sync.interfaces.ISyncDTO
import br.com.fitnesspro.shared.communication.enums.execution.EnumExecutionState
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.CommonImportFilter
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.reflect.KClass

abstract class AbstractImportationRepository<DTO: ISyncDTO, FILTER: CommonImportFilter>(context: Context)
    : AbstractSyncRepository(context) {

    protected val importationHistoryDAO = syncEntryPoint.getImportationHistoryDAO()

    private val gson: Gson = GsonBuilder().defaultGSon(serializeNulls = true)
    protected val timestampMapType: Type? = object : TypeToken<Map<String, LocalDateTime?>>() {}.type

    abstract suspend fun getImportationData(
        token: String,
        filter: FILTER,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<DTO>

    protected abstract suspend fun executeSegregation(dto: DTO): List<ImportSegregationResult<BaseModel>>

    protected abstract fun convertDTOToEntity(dto: BaseDTO): BaseModel

    protected abstract fun getMaintenanceDAO(modelClass: KClass<out BaseModel>): MaintenanceDAO<out BaseModel>

    protected abstract fun getListModelClassesNames(): List<String>

    protected abstract fun getCursorDataFrom(syncDTO: DTO): MutableMap<String, LocalDateTime?>

    protected abstract fun getModule(): EnumSyncModule

    @Suppress("UNCHECKED_CAST")
    open suspend fun getImportFilter(lastUpdateDateMap: MutableMap<String, LocalDateTime?>): FILTER {
        return CommonImportFilter(lastUpdateDateMap = lastUpdateDateMap) as FILTER
    }

    open fun shouldIgnoreEntityLog(result: ImportSegregationResult<BaseModel>): Boolean = false

    suspend fun import(serviceToken: String): Boolean {
        Log.i(LogConstants.WORKER_IMPORT, "Importando ${javaClass.simpleName}")

        var response: ImportationServiceResponse<DTO>? = null
        var clientStartDateTime: LocalDateTime? = null
        var hasData: Boolean

        try {
            val history = getHistoryFromDB()
            val cursorTimestampMap: MutableMap<String, LocalDateTime?> = getCursorTimestampMap(history)
            val importFilter = getImportFilter(cursorTimestampMap)
            val pageInfos = ImportPageInfos(pageSize = getPageSize())

            clientStartDateTime = dateTimeNow(ZoneOffset.UTC)

            response = getImportationData(serviceToken, importFilter, pageInfos)
            hasData = (response.value?.getMaxListSize() ?: 0) == pageInfos.pageSize

            updateLogWithStartRunningInfos(
                serviceToken = serviceToken,
                logPackageId = response.executionLogPackageId,
                logId = response.executionLogId,
                pageInfos = pageInfos,
                clientStartDateTime = clientStartDateTime
            )

            when {
                response.success && response.value!!.isEmpty() -> {
                    Log.i(LogConstants.WORKER_IMPORT, "Sucesso Sem Dados")

                    updateExecutionLogPackageWithSuccessIterationInfos(
                        logPackageId = response.executionLogPackageId,
                        serviceToken = serviceToken
                    )
                }

                response.success -> {
                    Log.i(LogConstants.WORKER_IMPORT, "Sucesso Com Dados Novos. pageSize = ${pageInfos.pageSize} maxListSize = ${response.value?.getMaxListSize()}")

                    val segregationResult = executeSegregation(response.value!!)
                    val entityCounts = saveDataLocally(segregationResult)

                    updateExecutionLogPackageWithSuccessIterationInfos(
                        logPackageId = response.executionLogPackageId,
                        serviceToken = serviceToken
                    )

                    updateExecutionLogSubPackageEntityCounts(
                        logPackageId = response.executionLogPackageId,
                        serviceToken = serviceToken,
                        entityCounts = entityCounts,
                        cursorTimestampMap = cursorTimestampMap
                    )

                    updateCursorsImportationHistory(response.value!!, history)
                }

                else -> {
                    throw ServiceException(response.error!!)
                }
            }

            if (!hasData) {
                updateImportationHistoryFinishedExecution(history)
                updateLogWithFinalizationInfos(serviceToken, response.executionLogId)
            }
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

        return hasData
    }

    private suspend fun updateCursorsImportationHistory(syncDTO: DTO, importationHistory: ImportationHistory) {
        val cursorData = getCursorDataFrom(syncDTO)
        importationHistory.cursorTimestampMapJson = gson.toJson(cursorData, timestampMapType)

        importationHistoryDAO.update(importationHistory)
    }

    private suspend fun updateImportationHistoryFinishedExecution(importationHistory: ImportationHistory) {
        importationHistory.date = dateTimeNow(ZoneOffset.UTC)
        importationHistory.cursorTimestampMapJson = null

        importationHistoryDAO.update(importationHistory)
    }

    private suspend fun getHistoryFromDB(): ImportationHistory {
        val databaseHistory = importationHistoryDAO.getImportationHistory(getModule())

        return if (databaseHistory == null) {
            val model = ImportationHistory(module = getModule())
            importationHistoryDAO.insert(model)
            model
        } else {
            databaseHistory
        }
    }

    private fun getCursorTimestampMap(importationHistory: ImportationHistory?): MutableMap<String, LocalDateTime?> {
        val cursorTimestampMap: MutableMap<String, LocalDateTime?> = importationHistory?.cursorTimestampMapJson?.let {
            gson.fromJson(it, timestampMapType)
        } ?: mutableMapOf()

        if (cursorTimestampMap.isEmpty()) {
            getListModelClassesNames().map {
                cursorTimestampMap[it] = importationHistory?.date
            }
        }

        return cursorTimestampMap
    }

    protected suspend fun segregate(dtoList: List<BaseDTO>, hasEntityWithId: suspend (String) -> Boolean): ImportSegregationResult<BaseModel>? {
        return if (dtoList.isNotEmpty()) {
            val insertionList = dtoList
                .filter { !hasEntityWithId(it.id!!) }
                .map { convertDTOToEntity(it) }

            val updateList = dtoList
                .filter { hasEntityWithId(it.id!!) }
                .map { convertDTOToEntity(it) }

            val clazz = (insertionList.firstOrNull() ?: updateList.firstOrNull())!!::class

            ImportSegregationResult(insertionList, updateList, clazz)
        } else {
            null
        }
    }

    protected fun List<AuditableDTO>.populateCursorInfos(
        cursorTimestampMap: MutableMap<String, LocalDateTime?>,
        entityClass: KClass<*>
    ) {
        if (this.size < getPageSize()) {
            cursorTimestampMap[entityClass.simpleName!!] = dateTimeNow(ZoneOffset.UTC)
        } else {
            cursorTimestampMap[entityClass.simpleName!!] = this.maxBy { it.updateDate!! }.updateDate
        }
    }

    protected suspend fun saveDataLocally(segregationResult: List<ImportSegregationResult<BaseModel>>): Map<String, UpdatableExecutionLogSubPackageEntityCountsDTO> {
        val entityCounts = mutableMapOf<String, UpdatableExecutionLogSubPackageEntityCountsDTO>()

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

            if (!shouldIgnoreEntityLog(result)) {
                entityCounts.put(
                    result.modelClass.simpleName!!,
                    UpdatableExecutionLogSubPackageEntityCountsDTO(
                        insertedItemsCount = result.insertionList.size,
                        updatedItemsCount = result.updateList.size
                    )
                )
            }
        }

        return entityCounts
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
        pageInfos: ImportPageInfos,
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
    ) {
        executionLogWebClient.updateLogPackageInformation(
            token = serviceToken,
            logPackageId = logPackageId,
            dto = UpdatableExecutionLogPackageInfosDTO(
                clientExecutionEnd = dateTimeNow(ZoneOffset.UTC)
            )
        )
    }

    private suspend fun updateExecutionLogSubPackageEntityCounts(
        logPackageId: String,
        serviceToken: String,
        entityCounts: Map<String, UpdatableExecutionLogSubPackageEntityCountsDTO>,
        cursorTimestampMap: MutableMap<String, LocalDateTime?>
    ) {
        executionLogWebClient.updateLogSubPackageInformation(
            token = serviceToken,
            logPackageId = logPackageId,
            dto = UpdatableExecutionLogSubPackageInfosDTO(
                entityCounts = entityCounts,
                lastUpdateDateMap = cursorTimestampMap
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