package br.com.fitnesspro.common.repository.sync.common

import android.content.Context
import br.com.fitnesspro.local.data.access.dao.SyncHistoryDAO
import br.com.fitnesspro.local.data.access.dao.SyncLogDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.local.data.access.dao.common.AuditableMaintenanceDAO
import br.com.fitnesspro.model.base.AuditableModel
import br.com.fitnesspro.model.sync.EnumSyncModule
import br.com.fitnesspro.model.sync.EnumSyncStatus
import br.com.fitnesspro.model.sync.EnumSyncType
import br.com.fitnesspro.model.sync.SyncLog
import br.com.fitnesspro.shared.communication.responses.IFitnessProServiceResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import java.time.LocalDateTime

abstract class AbstractSyncRepository<MODEL: AuditableModel, DAO: AuditableMaintenanceDAO<MODEL>> {

    @ApplicationContext
    protected lateinit var context: Context

    @Inject
    lateinit var userDAO: UserDAO

    @Inject
    lateinit var syncHistoryDAO: SyncHistoryDAO

    @Inject
    lateinit var syncLogDAO: SyncLogDAO

    @Inject
    lateinit var operationDAO: DAO

    abstract fun getDescription(): String

    abstract fun getModule(): EnumSyncModule

    open fun getPageSize(): Int = 200

    protected suspend fun getLastSyncDate(): LocalDateTime? {
        return syncHistoryDAO.getSyncHistory(getModule())?.lastSyncDate
    }

    protected suspend fun insertRunningLog(header: String, syncType: EnumSyncType): SyncLog {
        val log = SyncLog(
            module = getModule(),
            status = EnumSyncStatus.RUNNING,
            type = syncType,
            startDate = LocalDateTime.now(),
            processDetails = header
        )

        syncLogDAO.insert(log)

        return log
    }

    protected suspend fun updateLogWithError(log: SyncLog, response: IFitnessProServiceResponse, pageNumber: Int) {
        val section = buildString {
            appendLine("=========================================")
            appendLine("       EXECUTION ERROR $pageNumber       ")
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

    protected suspend fun updateLogWithSuccess(log: SyncLog) {
        syncLogDAO.update(log.copy(status = EnumSyncStatus.SUCCESS, endDate = LocalDateTime.now()))
    }

    protected suspend fun saveUnknownError(exception: Exception, syncType: EnumSyncType) {
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
            type = syncType,
            startDate = now,
            endDate = now,
            processDetails = error
        )

        syncLogDAO.insert(log)
    }
}