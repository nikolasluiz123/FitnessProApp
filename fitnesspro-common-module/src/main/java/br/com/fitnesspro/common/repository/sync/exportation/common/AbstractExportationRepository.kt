package br.com.fitnesspro.common.repository.sync.exportation.common

import android.content.Context
import android.util.Log
import br.com.fitnesspro.common.repository.sync.common.AbstractSyncRepository
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumSyncType
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.sync.SyncLog
import br.com.fitnesspro.shared.communication.dtos.common.BaseDTO
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import java.time.LocalDateTime

abstract class AbstractExportationRepository<DTO: BaseDTO, MODEL: IntegratedModel, DAO: IntegratedMaintenanceDAO<MODEL>>(context: Context)
    : AbstractSyncRepository<MODEL, DAO>(context) {

    abstract suspend fun getExportationData(pageInfos: ExportPageInfos): List<MODEL>

    abstract suspend fun callExportationService(modelList: List<MODEL>, token: String): PersistenceServiceResponse

    suspend fun export() {
        val user = getAuthenticatedUser()

        if (user?.serviceToken != null) {
            try {
                val pageInfos = ExportPageInfos(pageSize = getPageSize())

                val log = saveRunningLog(pageInfos)

                do {
                    val models = getExportationData(pageInfos)

                    if (models.isNotEmpty()) {
                        updateTransmissionState(models, EnumTransmissionState.RUNNING)

                        val response = callExportationService(models, user.serviceToken!!)

                        if (response.success) {
                            updateLogWithSuccessIteration(log, models, pageInfos)
                            updateTransmissionState(models, EnumTransmissionState.TRANSMITTED)

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

    private suspend fun saveRunningLog(pageInfos: ExportPageInfos): SyncLog {
        val header = buildString {
            appendLine("=========================================")
            appendLine("           EXPORTATION START          ")
            appendLine("=========================================")
            appendLine("PageInfos:")
            appendLine("  pageSize  : ${pageInfos.pageSize}")
            appendLine("  pageNumber: ${pageInfos.pageNumber}")
            appendLine("=========================================")
        }

        return insertRunningLog(header, EnumSyncType.EXPORTATION)
    }

    private suspend fun updateTransmissionState(models: List<MODEL>, transmissionState: EnumTransmissionState) {
        models.forEach { it.transmissionState = transmissionState }
        getOperationDAO().updateBatch(models, writeTransmissionState = false)
    }

    companion object {
        private const val TAG = "EXPORTATION"
    }
}