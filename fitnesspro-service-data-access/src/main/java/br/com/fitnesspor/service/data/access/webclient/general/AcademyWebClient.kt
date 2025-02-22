package br.com.fitnesspor.service.data.access.webclient.general

import android.content.Context
import br.com.fitnesspor.service.data.access.extensions.getResponseBody
import br.com.fitnesspor.service.data.access.service.general.IAcademyService
import br.com.fitnesspor.service.data.access.webclient.common.FitnessProWebClient
import br.com.fitnesspro.shared.communication.dtos.general.AcademyDTO
import br.com.fitnesspro.shared.communication.filter.CommonImportFilter
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.responses.ReadServiceResponse

class AcademyWebClient(
    context: Context,
    private val academyService: IAcademyService
): FitnessProWebClient(context) {

    suspend fun importAcademies(
        token: String,
        filter: CommonImportFilter,
        pageInfos: ImportPageInfos
    ): ReadServiceResponse<AcademyDTO> {
        return readServiceErrorHandlingBlock(
            codeBlock = {
                academyService.importAcademies(
                    token = formatToken(token),
                    filter = filter,
                    pageInfos = pageInfos
                ).getResponseBody()
            }
        )
    }

}