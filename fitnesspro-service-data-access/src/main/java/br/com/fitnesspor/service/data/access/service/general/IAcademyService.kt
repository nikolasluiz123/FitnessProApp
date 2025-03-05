package br.com.fitnesspor.service.data.access.service.general

import br.com.fitnesspro.shared.communication.constants.EndPointsV1.ACADEMY
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.ACADEMY_IMPORT
import br.com.fitnesspro.shared.communication.dtos.general.AcademyDTO
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface IAcademyService {

    @GET("$ACADEMY$ACADEMY_IMPORT")
    suspend fun importAcademies(
        @Header("Authorization") token: String,
        @Query("filter") filter: String,
        @Query("pageInfos") pageInfos: String
    ): Response<ImportationServiceResponse<AcademyDTO>>
}