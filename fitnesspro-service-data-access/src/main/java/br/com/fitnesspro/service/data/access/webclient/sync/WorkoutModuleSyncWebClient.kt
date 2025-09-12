package br.com.fitnesspro.service.data.access.webclient.sync

import android.content.Context
import br.com.fitnesspro.service.data.access.extensions.defaultServiceGSon
import br.com.fitnesspro.service.data.access.extensions.getResponseBody
import br.com.fitnesspro.service.data.access.service.sync.WorkoutModuleSyncService
import br.com.fitnesspro.service.data.access.webclient.common.FitnessProWebClient
import br.com.fitnesspro.shared.communication.dtos.sync.WorkoutModuleSyncDTO
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.CommonImportFilter
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import com.google.gson.GsonBuilder

class WorkoutModuleSyncWebClient(
    context: Context,
    private val service: WorkoutModuleSyncService,
): FitnessProWebClient(context) {

    suspend fun export(token: String, dto: WorkoutModuleSyncDTO): ExportationServiceResponse {
        return exportationServiceErrorHandlingBlock(
            codeBlock = {
                service.export(token = formatToken(token), dto = dto).getResponseBody()
            }
        )
    }

    suspend fun import(token: String, filter: CommonImportFilter, pageInfos: ImportPageInfos): ImportationServiceResponse<WorkoutModuleSyncDTO> {
        return importationServiceErrorHandlingBlock(
            codeBlock = {
                val gson = GsonBuilder().defaultServiceGSon()

                service.import(
                    token = formatToken(token),
                    filter = gson.toJson(filter),
                    pageInfos = gson.toJson(pageInfos)
                ).getResponseBody(WorkoutModuleSyncDTO::class.java)
            }
        )
    }
}