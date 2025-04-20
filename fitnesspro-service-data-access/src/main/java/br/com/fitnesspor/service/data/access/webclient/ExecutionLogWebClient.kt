package br.com.fitnesspor.service.data.access.webclient

import android.content.Context
import br.com.fitnesspor.service.data.access.extensions.getResponseBody
import br.com.fitnesspor.service.data.access.service.log.IExecutionLogService
import br.com.fitnesspor.service.data.access.webclient.common.FitnessProWebClient
import br.com.fitnesspro.shared.communication.dtos.common.BaseDTO
import br.com.fitnesspro.shared.communication.dtos.logs.UpdatableExecutionLogInfosDTO
import br.com.fitnesspro.shared.communication.dtos.logs.UpdatableExecutionLogPackageInfosDTO
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse

class ExecutionLogWebClient(
    context: Context,
    private val executionLogService: IExecutionLogService
): FitnessProWebClient(context) {

    suspend fun updateLogInformation(
        token: String,
        logId: String,
        dto: UpdatableExecutionLogInfosDTO
    ): PersistenceServiceResponse<BaseDTO> {
        return persistenceServiceErrorHandlingBlock(
            codeBlock = {
                executionLogService.updateExecutionLog(
                    token = formatToken(token),
                    executionLogId = logId,
                    dto = dto
                ).getResponseBody(BaseDTO::class.java)
            }
        )
    }

    suspend fun updateLogPackageInformation(
        token: String,
        logPackageId: String,
        dto: UpdatableExecutionLogPackageInfosDTO,
    ): PersistenceServiceResponse<BaseDTO> {
        return persistenceServiceErrorHandlingBlock(
            codeBlock = {
                executionLogService.updateExecutionLogPackage(
                    token = formatToken(token),
                    executionLogPackageId = logPackageId,
                    dto = dto
                ).getResponseBody(BaseDTO::class.java)
            }
        )
    }
}