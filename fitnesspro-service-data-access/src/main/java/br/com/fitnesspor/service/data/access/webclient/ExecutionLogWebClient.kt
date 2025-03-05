package br.com.fitnesspor.service.data.access.webclient

import android.content.Context
import br.com.fitnesspor.service.data.access.extensions.getResponseBody
import br.com.fitnesspor.service.data.access.service.log.IExecutionLogService
import br.com.fitnesspor.service.data.access.webclient.common.FitnessProWebClient
import br.com.fitnesspro.shared.communication.dtos.logs.UpdatableExecutionLogInfosDTO
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import java.time.LocalDateTime

class ExecutionLogWebClient(
    context: Context,
    private val executionLogService: IExecutionLogService
): FitnessProWebClient(context) {

    suspend fun updateLogInformation(
        token: String,
        logId: String,
        clientDateTimeStart: LocalDateTime?,
        clientDateTimeEnd: LocalDateTime?
    ): PersistenceServiceResponse {
        return persistenceServiceErrorHandlingBlock(
            codeBlock = {
                executionLogService.updateExecutionLog(
                    token = formatToken(token),
                    id = logId,
                    log = UpdatableExecutionLogInfosDTO(clientDateTimeStart, clientDateTimeEnd)
                ).getResponseBody()
            }
        )
    }
}