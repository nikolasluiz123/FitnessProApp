package br.com.fitnesspor.service.data.access.service.general

import br.com.fitnesspro.shared.communication.constants.EndPointsV1.PERSON
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.PERSON_ACADEMY_TIME
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.PERSON_ACADEMY_TIME_EXPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.PERSON_ACADEMY_TIME_IMPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.PERSON_EXPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.PERSON_IMPORT
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.PERSON_USER_IMPORT
import br.com.fitnesspro.shared.communication.dtos.general.PersonAcademyTimeDTO
import br.com.fitnesspro.shared.communication.dtos.general.PersonDTO
import br.com.fitnesspro.shared.communication.dtos.general.UserDTO
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import br.com.fitnesspro.shared.communication.responses.ReadServiceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface IPersonService {

    @POST(PERSON)
    suspend fun savePerson(@Body personDTO: PersonDTO): Response<PersistenceServiceResponse>

    @POST("$PERSON$PERSON_EXPORT")
    suspend fun savePersonBatch(
        @Header("Authorization") token: String,
        @Body personDTOList: List<PersonDTO>
    ): Response<PersistenceServiceResponse>

    @POST("$PERSON$PERSON_ACADEMY_TIME")
    suspend fun savePersonAcademyTime(
        @Header("Authorization") token: String,
        @Body personAcademyTimeDTO: PersonAcademyTimeDTO
    ): Response<PersistenceServiceResponse>

    @POST("$PERSON$PERSON_ACADEMY_TIME_EXPORT")
    suspend fun savePersonAcademyTimeBatch(
        @Header("Authorization") token: String,
        @Body personAcademyTimeDTOList: List<PersonAcademyTimeDTO>
    ): Response<PersistenceServiceResponse>

    @GET("$PERSON$PERSON_IMPORT")
    suspend fun importPersons(
        @Header("Authorization") token: String,
        @Query("filter") filter: String,
        @Query("pageInfos") pageInfos: String
    ): Response<ReadServiceResponse<PersonDTO>>

    @GET("$PERSON$PERSON_USER_IMPORT")
    suspend fun importUsers(
        @Header("Authorization") token: String,
        @Query("filter") filter: String,
        @Query("pageInfos") pageInfos: String
    ): Response<ReadServiceResponse<UserDTO>>

    @GET("$PERSON$PERSON_ACADEMY_TIME_IMPORT")
    suspend fun importPersonAcademyTimes(
        @Header("Authorization") token: String,
        @Query("filter") filter: String,
        @Query("pageInfos") pageInfos: String
    ): Response<ReadServiceResponse<PersonAcademyTimeDTO>>
}