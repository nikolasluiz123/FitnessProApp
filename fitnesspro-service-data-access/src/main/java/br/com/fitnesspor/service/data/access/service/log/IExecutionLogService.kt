package br.com.fitnesspor.service.data.access.service.log

import br.com.fitnesspro.shared.communication.constants.EndPointsV1
import br.com.fitnesspro.shared.communication.dtos.logs.UpdatableExecutionLogInfosDTO
import br.com.fitnesspro.shared.communication.dtos.logs.UpdatableExecutionLogPackageInfosDTO
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface IExecutionLogService {

    @PUT("${EndPointsV1.LOGS}/{executionLogId}")
    suspend fun updateExecutionLog(
        @Header("Authorization") token: String,
        @Path("executionLogId") executionLogId: String,
        @Body dto: UpdatableExecutionLogInfosDTO
    ): Response<PersistenceServiceResponse>

    @PUT("${EndPointsV1.LOGS}${EndPointsV1.LOGS_PACKAGE}/{executionLogPackageId}")
    suspend fun updateExecutionLogPackage(
        @Header("Authorization") token: String,
        @Path("executionLogPackageId") executionLogPackageId: String,
        @Body dto: UpdatableExecutionLogPackageInfosDTO
    ): Response<PersistenceServiceResponse>

}