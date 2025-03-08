package br.com.fitnesspro.common.repository.sync.importation.common

import android.content.Context
import android.util.Log
import br.com.fitnesspro.common.repository.sync.common.AbstractSyncRepository
import br.com.fitnesspro.core.exceptions.ServiceException
import br.com.fitnesspro.local.data.access.dao.common.MaintenanceDAO
import br.com.fitnesspro.model.base.BaseModel
import br.com.fitnesspro.model.enums.EnumSyncType
import br.com.fitnesspro.model.sync.SyncLog
import br.com.fitnesspro.shared.communication.dtos.common.BaseDTO
import br.com.fitnesspro.shared.communication.filter.CommonImportFilter
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.ReadServiceResponse
import java.time.LocalDateTime

abstract class AbstractImportationRepository<DTO: BaseDTO, MODEL: BaseModel, DAO: MaintenanceDAO<MODEL>>(context: Context)
    : AbstractSyncRepository<MODEL, DAO>(context) {

    abstract suspend fun getImportationData(
        token: String,
        filter: CommonImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<DTO>

    abstract suspend fun hasEntityWithId(id: String): Boolean

    abstract suspend fun convertDTOToEntity(dto: DTO): MODEL

    suspend fun import(serviceToken: String, lastUpdateDate: LocalDateTime?) {
        try {
            val importFilter = CommonImportFilter(lastUpdateDate = lastUpdateDate)
            val pageInfos = ImportPageInfos(pageSize = getPageSize())

            val logId = saveLocalRunningLog(importFilter, pageInfos).id

            do {
                val response = getImportationData(serviceToken, importFilter, pageInfos)

                when {
                    response.success && response.values.isEmpty() -> {
                        updateLogWithSuccessIteration(logId, emptyList(), emptyList(), pageInfos)
                    }

                    response.success -> {
                        updateRemoteLogWithStartDateTime(response.executionLogId, serviceToken)

                        val (insertionList, updateList) = executeSegregation(response)

                        saveDataLocally(insertionList, updateList)

                        updateLogWithSuccessIteration(logId, insertionList, updateList, pageInfos)
                        pageInfos.pageNumber++

                        updateRemoteLogWithEndDateTime(response.executionLogId, serviceToken)
                    }

                    else -> {
                        updateLogWithError(logId, response, pageInfos.pageNumber)
                        throw ServiceException(response.error!!)
                    }
                }
            } while (response.values.size == pageInfos.pageSize)

            updateLogWithSuccess(logId)
            showFinalLocalLog(logId)
        } catch (exception: Exception) {
            saveUnknownError(exception, EnumSyncType.IMPORTATION)
            throw exception
        }
    }

    private suspend fun showFinalLocalLog(logId: String) {
        val log = syncLogDAO.findById(logId)
        Log.d(TAG, log.processDetails!!)
    }

    private suspend fun saveLocalRunningLog(importFilter: CommonImportFilter, pageInfos: ImportPageInfos): SyncLog {
        val header = buildString {
            appendLine("=========================================")
            appendLine(" IMPORTATION START - MODULE ${getModule().name}         ")
            appendLine("=========================================")
            appendLine("Filter:")
            appendLine("  lastUpdateDate: ${importFilter.lastUpdateDate ?: "N/A"}")
            appendLine("-----------------------------------------")
            appendLine("PageInfos:")
            appendLine("  pageSize  : ${pageInfos.pageSize}")
            appendLine("  pageNumber: ${pageInfos.pageNumber}")
            appendLine("=========================================")
        }

        return insertRunningLog(header, EnumSyncType.IMPORTATION)
    }

    private suspend fun updateLogWithSuccessIteration(
        logId: String,
        insertionList: List<MODEL>,
        updateList: List<MODEL>,
        pageInfos: ImportPageInfos,
    ) {
        val log = syncLogDAO.findById(logId)

        val section = buildString {
            appendLine("=========================================")
            appendLine(" EXECUTION ${pageInfos.pageNumber} - SUCCESS ")
            appendLine("=========================================")
            appendLine("INSERTED ITEMS  : ${insertionList.size}")
            appendLine("UPDATED ITEMS   : ${updateList.size}")
            appendLine("EXECUTION DATE  : ${LocalDateTime.now()}")
            appendLine("=========================================")
        }

        val newProcessDetails = log.processDetails.orEmpty() + "\n" + section
        val updatedLog = log.copy(processDetails = newProcessDetails)

        syncLogDAO.update(updatedLog)
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

    companion object {
        private const val TAG = "IMPORTATION"
    }
}