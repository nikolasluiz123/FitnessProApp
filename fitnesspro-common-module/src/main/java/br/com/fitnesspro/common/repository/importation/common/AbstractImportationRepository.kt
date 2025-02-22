package br.com.fitnesspro.common.repository.importation.common

import android.content.Context
import android.util.Log
import br.com.fitnesspro.local.data.access.dao.SyncHistoryDAO
import br.com.fitnesspro.local.data.access.dao.SyncLogDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.local.data.access.dao.common.MaintenanceDAO
import br.com.fitnesspro.model.base.BaseModel
import br.com.fitnesspro.model.sync.EnumSyncModule
import br.com.fitnesspro.model.sync.EnumSyncStatus
import br.com.fitnesspro.model.sync.EnumSyncType
import br.com.fitnesspro.model.sync.SyncLog
import br.com.fitnesspro.shared.communication.dtos.common.BaseDTO
import br.com.fitnesspro.shared.communication.filter.CommonImportFilter
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.responses.ReadServiceResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

abstract class AbstractImportationRepository<DTO: BaseDTO, MODEL: BaseModel, DAO: MaintenanceDAO<MODEL>> {

    @ApplicationContext
    protected lateinit var context: Context

    @Inject
    lateinit var userDAO: UserDAO

    @Inject
    lateinit var syncHistoryDAO: SyncHistoryDAO

    @Inject
    lateinit var syncLogDAO: SyncLogDAO

    @Inject
    lateinit var importationDAO: DAO

    abstract fun getDescription(): String

    abstract fun getModule(): EnumSyncModule

    abstract suspend fun getImportationData(
        token: String,
        filter: CommonImportFilter,
        pageInfos: ImportPageInfos
    ): ReadServiceResponse<DTO>

    abstract suspend fun hasEntityWithId(id: String): Boolean

    abstract suspend fun convertDTOToEntity(dto: DTO): MODEL

    open fun getPageSize(): Int = 500

    suspend fun import() = withContext(IO) {
        userDAO.getAuthenticatedUser()?.serviceToken?.let { token ->
            try {
                val importFilter = CommonImportFilter(onlyActives = true, lastUpdateDate = getLasSyncDate())
                val pageInfos = ImportPageInfos(pageSize = getPageSize())

                val log = saveRunningLog(importFilter, pageInfos)

                do {
                    val response = getImportationData(token, importFilter, pageInfos)

                    if (response.success) {
                        val (insertionList, updateList) = getServiceData(response)

                        saveDataLocally(insertionList, updateList)

                        updateLogWithSuccessIteration(log, insertionList, updateList, pageInfos)

                        pageInfos.pageNumber++
                    } else {
                        updateLogWithError(log, response, pageInfos)
                    }
                } while (response.values.size == pageInfos.pageSize)

                updateLogWithSuccess(log)
                Log.d(TAG, log.processDetails!!)
            } catch (exception: Exception) {
                saveUnknownError(exception)
                throw exception
            }
        }
    }

    private suspend fun saveRunningLog(importFilter: CommonImportFilter, pageInfos: ImportPageInfos): SyncLog {
        val header = buildString {
            appendLine("=========================================")
            appendLine("           IMPORTATION START          ")
            appendLine("=========================================")
            appendLine("CommonImportFilter:")
            appendLine("  onlyActives   : ${importFilter.onlyActives}")
            appendLine("  lastUpdateDate: ${importFilter.lastUpdateDate ?: "N/A"}")
            appendLine("-----------------------------------------")
            appendLine("ImportPageInfos:")
            appendLine("  pageSize  : ${pageInfos.pageSize}")
            appendLine("  pageNumber: ${pageInfos.pageNumber}")
            appendLine("=========================================")
        }

        val log = SyncLog(
            module = getModule(),
            status = EnumSyncStatus.RUNNING,
            type = EnumSyncType.IMPORTATION,
            startDate = LocalDateTime.now(),
            processDetails = header
        )

        syncLogDAO.insert(log)

        return log
    }

    private suspend fun updateLogWithSuccessIteration(
        log: SyncLog,
        insertionList: List<MODEL>,
        updateList: List<MODEL>,
        pageInfos: ImportPageInfos,
    ) {
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

    private suspend fun updateLogWithError(
        log: SyncLog,
        response: ReadServiceResponse<DTO>,
        pageInfos: ImportPageInfos
    ) {
        val section = buildString {
            appendLine("=========================================")
            appendLine("       EXECUTION ERROR ${pageInfos.pageNumber}       ")
            appendLine("=========================================")
            appendLine("Error   : ${response.error}")
            appendLine("=========================================")
        }

        val newProcessDetails = log.processDetails.orEmpty() + "\n" + section
        val updatedLog = log.copy(
            processDetails = newProcessDetails,
            status = EnumSyncStatus.ERROR,
            endDate = LocalDateTime.now()
        )

        syncLogDAO.update(updatedLog)
    }

    private suspend fun saveUnknownError(exception: Exception) {
        val error = buildString {
            appendLine("=========================================")
            appendLine("              EXECUTION ERROR           ")
            appendLine("=========================================")
            appendLine(exception.stackTraceToString())
            appendLine("=========================================")
        }

        val now = LocalDateTime.now()

        val log = SyncLog(
            module = getModule(),
            status = EnumSyncStatus.ERROR,
            type = EnumSyncType.IMPORTATION,
            startDate = now,
            endDate = now,
            processDetails = error
        )

        syncLogDAO.insert(log)
    }

    private suspend fun updateLogWithSuccess(log: SyncLog) {
        syncLogDAO.update(log.copy(status = EnumSyncStatus.SUCCESS, endDate = LocalDateTime.now()))
    }

    private suspend fun getLasSyncDate(): LocalDateTime? {
        return syncHistoryDAO.getSyncHistory(getModule())?.lastSyncDate
    }

    private suspend fun getServiceData(response: ReadServiceResponse<DTO>): Pair<List<MODEL>, List<MODEL>> {
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
            importationDAO.insertBatch(insertionList)
        }

        if (updateList.isNotEmpty()) {
            importationDAO.updateBatch(updateList)
        }
    }

    companion object {
        private const val TAG = "IMPORTATION"
    }
}