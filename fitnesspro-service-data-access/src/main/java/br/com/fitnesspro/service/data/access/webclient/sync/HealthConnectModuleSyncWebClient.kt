package br.com.fitnesspro.service.data.access.webclient.sync

import android.content.Context
import br.com.fitnesspro.service.data.access.extensions.defaultServiceGSon
import br.com.fitnesspro.service.data.access.extensions.getResponseBody
import br.com.fitnesspro.service.data.access.service.sync.HealthConnectModuleSyncService
import br.com.fitnesspro.service.data.access.webclient.common.FitnessProWebClient
import br.com.fitnesspro.shared.communication.dtos.sync.HealthConnectModuleSyncDTO
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.CommonImportFilter
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import com.google.gson.GsonBuilder

class HealthConnectModuleSyncWebClient(
    context: Context,
    private val service: HealthConnectModuleSyncService,
) : FitnessProWebClient(context) {

    suspend fun export(token: String, dto: HealthConnectModuleSyncDTO): ExportationServiceResponse {
        return exportationServiceErrorHandlingBlock(
            codeBlock = {
                service.export(token = formatToken(token), dto = dto).getResponseBody()
            }
        )
    }

    suspend fun import(token: String, filter: CommonImportFilter, pageInfos: ImportPageInfos): ImportationServiceResponse<HealthConnectModuleSyncDTO> {
        return importationServiceErrorHandlingBlock(
            codeBlock = {
                val gson = GsonBuilder().defaultServiceGSon()

                service.import(
                    token = formatToken(token),
                    filter = gson.toJson(filter),
                    pageInfos = gson.toJson(pageInfos)
                ).getResponseBody(HealthConnectModuleSyncDTO::class.java)
            }
        )
    }
}