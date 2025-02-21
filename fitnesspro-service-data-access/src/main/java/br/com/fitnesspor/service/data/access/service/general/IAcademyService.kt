package br.com.fitnesspor.service.data.access.service.general

import br.com.fitnesspro.shared.communication.constants.EndPointsV1.ACADEMY
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.ACADEMY_IMPORT
import br.com.fitnesspro.shared.communication.dtos.general.AcademyDTO
import br.com.fitnesspro.shared.communication.filter.CommonImportFilter
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.responses.ReadServiceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface IAcademyService {

    @POST("$ACADEMY$ACADEMY_IMPORT")
    suspend fun importAcademies(
        @Header("Authorization") token: String,
        @Body filter: CommonImportFilter,
        @Query("pageInfos") pageInfos: ImportPageInfos
    ): Response<ReadServiceResponse<AcademyDTO>>
}