package br.com.fitnesspor.service.data.access.webclient.sync

import android.content.Context
import br.com.fitnesspor.service.data.access.extensions.defaultServiceGSon
import br.com.fitnesspor.service.data.access.extensions.getResponseBody
import br.com.fitnesspor.service.data.access.service.sync.SchedulerModuleSyncService
import br.com.fitnesspor.service.data.access.webclient.common.FitnessProWebClient
import br.com.fitnesspro.shared.communication.dtos.sync.SchedulerModuleSyncDTO
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.CommonImportFilter
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import com.google.gson.GsonBuilder

class SchedulerModuleSyncWebClient(
    context: Context,
    private val service: SchedulerModuleSyncService,
): FitnessProWebClient(context) {

    suspend fun export(token: String, dto: SchedulerModuleSyncDTO): ExportationServiceResponse {
        return exportationServiceErrorHandlingBlock(
            codeBlock = {
                service.export(token = formatToken(token), dto = dto).getResponseBody()
            }
        )
    }

    suspend fun import(token: String, filter: CommonImportFilter, pageInfos: ImportPageInfos): ImportationServiceResponse<SchedulerModuleSyncDTO> {
        return importationServiceErrorHandlingBlock(
            codeBlock = {
                val gson = GsonBuilder().defaultServiceGSon()

                service.import(
                    token = formatToken(token),
                    filter = gson.toJson(filter),
                    pageInfos = gson.toJson(pageInfos)
                ).getResponseBody(SchedulerModuleSyncDTO::class.java)
            }
        )
    }
}