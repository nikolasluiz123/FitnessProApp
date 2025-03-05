package br.com.fitnesspor.service.data.access.service.log

import br.com.fitnesspro.shared.communication.constants.EndPointsV1
import br.com.fitnesspro.shared.communication.dtos.logs.UpdatableExecutionLogInfosDTO
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path

interface IExecutionLogService {

    @PUT("${EndPointsV1.LOGS}/{id}")
    suspend fun updateExecutionLog(@Header("Authorization") token: String,
                                   @Path("id") id: String,
                                   @Body log: UpdatableExecutionLogInfosDTO): Response<PersistenceServiceResponse>

}