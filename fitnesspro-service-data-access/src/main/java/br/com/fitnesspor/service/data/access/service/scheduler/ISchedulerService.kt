package br.com.fitnesspor.service.data.access.service.scheduler

import br.com.fitnesspro.shared.communication.constants.EndPointsV1.SCHEDULER
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.SCHEDULER_CONFIG
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.SCHEDULER_CONFIG_EXPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.SCHEDULER_CONFIG_IMPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.SCHEDULER_EXPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.SCHEDULER_IMPORT
import br.com.fitnesspro.shared.communication.dtos.scheduler.SchedulerConfigDTO
import br.com.fitnesspro.shared.communication.dtos.scheduler.SchedulerDTO
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ISchedulerService {

    @POST(SCHEDULER)
    suspend fun saveScheduler(
        @Header("Authorization") token: String,
        @Body schedulerDTO: SchedulerDTO
    ): Response<PersistenceServiceResponse>

    @POST("$SCHEDULER/$SCHEDULER_EXPORT")
    suspend fun saveSchedulerBatch(
        @Header("Authorization") token: String,
        @Body schedulerDTOList: List<SchedulerDTO>
    ): Response<ExportationServiceResponse>

    @POST("$SCHEDULER$SCHEDULER_CONFIG")
    suspend fun saveSchedulerConfig(@Body schedulerConfigDTO: SchedulerConfigDTO): Response<PersistenceServiceResponse>

    @POST("$SCHEDULER$SCHEDULER_CONFIG_EXPORT")
    suspend fun saveSchedulerConfigBatch(
        @Header("Authorization") token: String,
        @Body schedulerConfigDTOList: List<SchedulerConfigDTO>
    ): Response<ExportationServiceResponse>

    @GET("$SCHEDULER$SCHEDULER_IMPORT")
    suspend fun importSchedules(
        @Header("Authorization") token: String,
        @Query("filter") filter: String,
        @Query("pageInfos") pageInfos: String
    ): Response<ImportationServiceResponse<SchedulerDTO>>

    @GET("$SCHEDULER$SCHEDULER_CONFIG_IMPORT")
    suspend fun importScheduleConfigs(
        @Header("Authorization") token: String,
        @Query("filter") filter: String,
        @Query("pageInfos") pageInfos: String
    ): Response<ImportationServiceResponse<SchedulerConfigDTO>>
}