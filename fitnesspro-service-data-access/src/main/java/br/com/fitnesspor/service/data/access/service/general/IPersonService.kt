package br.com.fitnesspor.service.data.access.service.general

import br.com.fitnesspro.shared.communication.constants.EndPointsV1
import br.com.fitnesspro.shared.communication.dtos.general.PersonAcademyTimeDTO
import br.com.fitnesspro.shared.communication.dtos.general.PersonDTO
import br.com.fitnesspro.shared.communication.filter.CommonImportFilter
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import br.com.fitnesspro.shared.communication.responses.ReadServiceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface IPersonService {

    @POST(EndPointsV1.PERSON)
    suspend fun savePerson(@Body personDTO: PersonDTO): Response<PersistenceServiceResponse>

    @POST("${EndPointsV1.PERSON}/${EndPointsV1.PERSON_EXPORT}")
    suspend fun savePersonBatch(
        @Header("Authorization") token: String,
        @Body personDTOList: List<PersonDTO>
    ): Response<PersistenceServiceResponse>

    @POST("${EndPointsV1.PERSON}/${EndPointsV1.PERSON_ACADEMY_TIME}")
    suspend fun savePersonAcademyTime(
        @Header("Authorization") token: String,
        @Body personAcademyTimeDTO: PersonAcademyTimeDTO
    ): Response<PersistenceServiceResponse>

    @POST("${EndPointsV1.PERSON}/${EndPointsV1.PERSON_ACADEMY_TIME_EXPORT}")
    suspend fun savePersonAcademyTimeBatch(
        @Header("Authorization") token: String,
        @Body personAcademyTimeDTOList: List<PersonAcademyTimeDTO>
    ): Response<PersistenceServiceResponse>

    @POST("${EndPointsV1.PERSON}/${EndPointsV1.PERSON_IMPORT}")
    suspend fun importPersons(
        @Header("Authorization") token: String,
        @Body filter: CommonImportFilter,
        @Query("pageInfos") pageInfos: ImportPageInfos
    ): Response<ReadServiceResponse<PersonDTO>>

    @POST("${EndPointsV1.PERSON}/${EndPointsV1.PERSON_ACADEMY_TIME_IMPORT}")
    suspend fun importPersonAcademyTimes(
        @Header("Authorization") token: String,
        @Body filter: CommonImportFilter,
        @Query("pageInfos") pageInfos: ImportPageInfos
    ): Response<ReadServiceResponse<PersonAcademyTimeDTO>>
}