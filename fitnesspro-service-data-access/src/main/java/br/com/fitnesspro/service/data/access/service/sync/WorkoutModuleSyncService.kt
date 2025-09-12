package br.com.fitnesspro.service.data.access.service.sync

import br.com.fitnesspro.shared.communication.constants.EndPointsV1.SYNC
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.SYNC_EXPORT_WORKOUT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.SYNC_IMPORT_WORKOUT
import br.com.fitnesspro.shared.communication.dtos.sync.WorkoutModuleSyncDTO
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface WorkoutModuleSyncService {

    @GET("$SYNC$SYNC_IMPORT_WORKOUT")
    suspend fun import(
        @Header("Authorization") token: String,
        @Query("filter") filter: String,
        @Query("pageInfos") pageInfos: String
    ): Response<ImportationServiceResponse<WorkoutModuleSyncDTO>>

    @POST("$SYNC$SYNC_EXPORT_WORKOUT")
    suspend fun export(
        @Header("Authorization") token: String,
        @Body dto: WorkoutModuleSyncDTO
    ): Response<ExportationServiceResponse>
}