package br.com.fitnesspro.service.data.access.service.sync

import br.com.fitnesspro.shared.communication.constants.EndPointsV1
import br.com.fitnesspro.shared.communication.dtos.workout.health.HealthConnectModuleSyncDTO
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface HealthConnectModuleSyncService {

    @GET("${EndPointsV1.SYNC}${EndPointsV1.SYNC_IMPORT_HEALTH_CONNECT}")
    suspend fun import(
        @Header("Authorization") token: String,
        @Query("filter") filter: String,
        @Query("pageInfos") pageInfos: String
    ): Response<ImportationServiceResponse<HealthConnectModuleSyncDTO>>

    @POST("${EndPointsV1.SYNC}${EndPointsV1.SYNC_EXPORT_HEALTH_CONNECT}")
    suspend fun export(
        @Header("Authorization") token: String,
        @Body dto: HealthConnectModuleSyncDTO
    ): Response<ExportationServiceResponse>
}