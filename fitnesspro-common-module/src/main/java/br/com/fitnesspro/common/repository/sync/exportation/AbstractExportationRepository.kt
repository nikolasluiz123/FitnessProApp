package br.com.fitnesspro.common.repository.sync.exportation

import android.content.Context
import android.util.Log
import br.com.fitnesspro.common.repository.sync.common.AbstractSyncRepository
import br.com.fitnesspro.local.data.access.dao.common.AuditableMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.CommonExportFilter
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.sync.EnumSyncType
import br.com.fitnesspro.model.sync.SyncLog
import br.com.fitnesspro.shared.communication.dtos.common.BaseDTO
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import java.time.LocalDateTime

abstract class AbstractExportationRepository<DTO: BaseDTO, MODEL: IntegratedModel, DAO: AuditableMaintenanceDAO<MODEL>>(context: Context)
    : AbstractSyncRepository<MODEL, DAO>(context) {

    abstract suspend fun getExportationData(filter: CommonExportFilter, pageInfos: ExportPageInfos): List<MODEL>

    abstract suspend fun callExportationService(modelList: List<MODEL>, token: String): PersistenceServiceResponse

    suspend fun export() {
        getAuthenticatedUser()?.let { user ->
            try {
                val exportFilter = CommonExportFilter(authenticatedUserId = user.id, lastUpdateDate = getLastSyncDate())
                val pageInfos = ExportPageInfos(pageSize = getPageSize())

                val log = saveRunningLog(exportFilter, pageInfos)

                do {
                    val models = getExportationData(exportFilter, pageInfos)

                    if (models.isNotEmpty()) {
                        val response = callExportationService(models, user.serviceToken!!)

                        if (response.success) {
                            updateLogWithSuccessIteration(log, models, pageInfos)
                            updateTransmissionDate(models, response.transmissionDate)

                            pageInfos.pageNumber++
                        } else {
                            updateLogWithError(log, response, pageInfos.pageNumber)
                        }
                    }
                } while (models.size == pageInfos.pageSize)

                updateLogWithSuccess(log)
                Log.d(TAG, log.processDetails!!)
            } catch (exception: Exception) {
                saveUnknownError(exception, EnumSyncType.EXPORTATION)
                throw exception
            }
        }
    }

    private suspend fun updateLogWithSuccessIteration(log: SyncLog, models: List<MODEL>, pageInfos: ExportPageInfos) {
        val section = buildString {
            appendLine("=========================================")
            appendLine(" EXECUTION ${pageInfos.pageNumber} - SUCCESS ")
            appendLine("=========================================")
            appendLine("ITEMS           : ${models.size}")
            appendLine("EXECUTION DATE  : ${LocalDateTime.now()}")
            appendLine("=========================================")
        }

        val newProcessDetails = log.processDetails.orEmpty() + "\n" + section
        val updatedLog = log.copy(processDetails = newProcessDetails)

        syncLogDAO.update(updatedLog)
    }

    private suspend fun saveRunningLog(filter: CommonExportFilter, pageInfos: ExportPageInfos): SyncLog {
        val header = buildString {
            appendLine("=========================================")
            appendLine("           EXPORTATION START          ")
            appendLine("=========================================")
            appendLine("Filter:")
            appendLine("  lastUpdateDate: ${filter.lastUpdateDate ?: "N/A"}")
            appendLine("-----------------------------------------")
            appendLine("PageInfos:")
            appendLine("  pageSize  : ${pageInfos.pageSize}")
            appendLine("  pageNumber: ${pageInfos.pageNumber}")
            appendLine("=========================================")
        }

        return insertRunningLog(header, EnumSyncType.EXPORTATION)
    }

    private suspend fun updateTransmissionDate(models: List<MODEL>, transmissionDate: LocalDateTime?) {
        models.forEach { it.transmissionDate = transmissionDate }
        getOperationDAO().updateBatch(models, writeAuditableData = false)
    }

    companion object {
        private const val TAG = "EXPORTATION"
    }
}