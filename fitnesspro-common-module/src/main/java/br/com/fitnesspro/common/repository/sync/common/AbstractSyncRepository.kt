package br.com.fitnesspro.common.repository.sync.common

import android.content.Context
import br.com.fitnesspro.common.injection.ISyncRepositoryEntryPoint
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.core.extensions.dateTimeNow
import br.com.fitnesspro.local.data.access.dao.SyncLogDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.local.data.access.dao.common.BaseDAO
import br.com.fitnesspro.model.base.BaseModel
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.enums.EnumSyncStatus
import br.com.fitnesspro.model.enums.EnumSyncType
import br.com.fitnesspro.model.sync.SyncLog
import br.com.fitnesspro.shared.communication.responses.IFitnessProServiceResponse
import dagger.hilt.android.EntryPointAccessors
import java.time.LocalDateTime

abstract class AbstractSyncRepository<MODEL: BaseModel, DAO: BaseDAO>(context: Context): FitnessProRepository(context) {

    private val entryPoint: ISyncRepositoryEntryPoint = EntryPointAccessors.fromApplication(context, ISyncRepositoryEntryPoint::class.java)

    private val executionLogWebClient = entryPoint.getExecutionLogWebClient()

    protected val userDAO: UserDAO = entryPoint.getUserDAO()

    protected val syncLogDAO: SyncLogDAO = entryPoint.getSyncLogDAO()

    abstract fun getOperationDAO(): DAO

    abstract fun getDescription(): String

    abstract fun getModule(): EnumSyncModule

    open fun getPageSize(): Int = 1000

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

    protected suspend fun updateRemoteLogWithStartDateTime(
        logId: String,
        token: String,
        clientDateTimeStart: LocalDateTime? = null,
    ) {
        executionLogWebClient.updateLogInformation(
            token = token,
            logId = logId,
            clientDateTimeStart = clientDateTimeStart ?: dateTimeNow(),
            clientDateTimeEnd = null,
        )
    }

    protected suspend fun updateRemoteLogWithEndDateTime(
        logId: String,
        token: String,
        clientDateTimeEnd: LocalDateTime? = null
    ) {
        executionLogWebClient.updateLogInformation(
            token = token,
            logId = logId,
            clientDateTimeStart = null,
            clientDateTimeEnd = clientDateTimeEnd ?: dateTimeNow(),
        )
    }
}