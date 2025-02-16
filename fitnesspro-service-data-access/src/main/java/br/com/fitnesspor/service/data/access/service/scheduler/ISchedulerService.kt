package br.com.fitnesspor.service.data.access.service.scheduler

import br.com.fitnesspro.shared.communication.constants.EndPointsV1
import br.com.fitnesspro.shared.communication.dtos.scheduler.SchedulerConfigDTO
import br.com.fitnesspro.shared.communication.dtos.scheduler.SchedulerDTO
import br.com.fitnesspro.shared.communication.filter.CommonImportFilter
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import br.com.fitnesspro.shared.communication.responses.ReadServiceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ISchedulerService {

    @POST(EndPointsV1.SCHEDULER)
    suspend fun saveScheduler(
        @Header("Authorization") token: String,
        @Body schedulerDTO: SchedulerDTO
    ): Response<PersistenceServiceResponse>

    @POST("${EndPointsV1.SCHEDULER}/${EndPointsV1.SCHEDULER_EXPORT}")
    suspend fun saveSchedulerBatch(
        @Header("Authorization") token: String,
        @Body schedulerDTOList: List<SchedulerDTO>
    ): Response<PersistenceServiceResponse>

    @POST(EndPointsV1.SCHEDULER_CONFIG)
    suspend fun saveSchedulerConfig(
        @Header("Authorization") token: String,
        @Body schedulerConfigDTO: SchedulerConfigDTO
    ): Response<PersistenceServiceResponse>

    @POST("${EndPointsV1.SCHEDULER_CONFIG}/${EndPointsV1.SCHEDULER_CONFIG_EXPORT}")
    suspend fun saveSchedulerConfigBatch(
        @Header("Authorization") token: String,
        @Body schedulerConfigDTOList: List<SchedulerConfigDTO>
    ): Response<PersistenceServiceResponse>

    @POST("${EndPointsV1.SCHEDULER}/${EndPointsV1.SCHEDULER_IMPORT}")
    suspend fun importSchedules(
        @Header("Authorization") token: String,
        @Body filter: CommonImportFilter,
        @Query("pageInfos") pageInfos: ImportPageInfos
    ): Response<ReadServiceResponse<SchedulerDTO>>

    @POST("${EndPointsV1.SCHEDULER_CONFIG}/${EndPointsV1.SCHEDULER_CONFIG_IMPORT}")
    suspend fun importScheduleConfigs(
        @Header("Authorization") token: String,
        @Body filter: CommonImportFilter,
        @Query("pageInfos") pageInfos: ImportPageInfos
    ): Response<ReadServiceResponse<SchedulerConfigDTO>>
}