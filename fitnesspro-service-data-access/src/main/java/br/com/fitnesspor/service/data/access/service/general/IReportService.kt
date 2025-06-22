package br.com.fitnesspor.service.data.access.service.general

import br.com.fitnesspro.shared.communication.constants.EndPointsV1.DELETE_ALL_SCHEDULER_REPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.DELETE_SCHEDULER_REPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.REPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.REPORT_IMPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.SCHEDULER_REPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.SCHEDULER_REPORT_IMPORT
import br.com.fitnesspro.shared.communication.dtos.general.ReportDTO
import br.com.fitnesspro.shared.communication.dtos.general.SchedulerReportDTO
import br.com.fitnesspro.shared.communication.responses.FitnessProServiceResponse
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface IReportService {

    @POST("$REPORT$SCHEDULER_REPORT")
    suspend fun saveSchedulerReport(
        @Header("Authorization") token: String,
        @Body schedulerReportDTO: SchedulerReportDTO
    ): Response<PersistenceServiceResponse<SchedulerReportDTO>>

    @GET("$REPORT$SCHEDULER_REPORT_IMPORT")
    suspend fun importReportsFromScheduler(
        @Header("Authorization") token: String,
        @Query("filter") filter: String,
        @Query("pageInfos") pageInfos: String
    ): Response<ImportationServiceResponse<ReportDTO>>

    @GET("$REPORT$REPORT_IMPORT")
    suspend fun importSchedulerReports(
        @Header("Authorization") token: String,
        @Query("filter") filter: String,
        @Query("pageInfos") pageInfos: String
    ): Response<ImportationServiceResponse<SchedulerReportDTO>>

    @DELETE("$REPORT/$DELETE_SCHEDULER_REPORT")
    suspend fun deleteSchedulerReport(
        @Header("Authorization") token: String,
        @Path("reportId") reportId: String
    ): Response<FitnessProServiceResponse>

    @DELETE("$REPORT/$DELETE_ALL_SCHEDULER_REPORT")
    suspend fun deleteAllSchedulerReport(
        @Header("Authorization") token: String,
    ): Response<FitnessProServiceResponse>
}