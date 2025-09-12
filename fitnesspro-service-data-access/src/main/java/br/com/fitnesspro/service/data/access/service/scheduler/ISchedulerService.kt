package br.com.fitnesspro.service.data.access.service.scheduler

import br.com.fitnesspro.shared.communication.constants.EndPointsV1.SCHEDULER
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.SCHEDULER_RECURRENT
import br.com.fitnesspro.shared.communication.dtos.scheduler.RecurrentSchedulerDTO
import br.com.fitnesspro.shared.communication.dtos.scheduler.SchedulerDTO
import br.com.fitnesspro.shared.communication.responses.FitnessProServiceResponse
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ISchedulerService {

    @POST(SCHEDULER)
    suspend fun saveScheduler(
        @Header("Authorization") token: String,
        @Body schedulerDTO: SchedulerDTO
    ): Response<PersistenceServiceResponse<SchedulerDTO>>

    @POST("$SCHEDULER$SCHEDULER_RECURRENT")
    suspend fun saveRecurrentScheduler(
        @Header("Authorization") token: String,
        @Body recurrentSchedulerDTO: RecurrentSchedulerDTO
    ): Response<FitnessProServiceResponse>

}