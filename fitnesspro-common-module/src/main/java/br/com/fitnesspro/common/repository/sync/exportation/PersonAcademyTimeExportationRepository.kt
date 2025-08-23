package br.com.fitnesspro.common.repository.sync.exportation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.general.PersonWebClient
import br.com.fitnesspro.common.repository.sync.exportation.common.AbstractExportationRepository
import br.com.fitnesspro.local.data.access.dao.PersonAcademyTimeDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.general.PersonAcademyTime
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse

class PersonAcademyTimeExportationRepository(
    context: Context,
    private val personAcademyTimeDAO: PersonAcademyTimeDAO,
    private val personWebClient: PersonWebClient,
): AbstractExportationRepository<PersonAcademyTime, PersonAcademyTimeDAO>(context) {

    override suspend fun getExportationData(
        pageInfos: ExportPageInfos
    ): List<PersonAcademyTime> {
        return personAcademyTimeDAO.getExportationData(pageInfos)
    }

    override fun getOperationDAO(): PersonAcademyTimeDAO {
        return personAcademyTimeDAO
    }

    override suspend fun callExportationService(
        modelList: List<PersonAcademyTime>,
        token: String
    ): ExportationServiceResponse {
        return personWebClient.savePersonAcademyTimeBatch(
            token = token,
            personAcademyTimeList = modelList
        )
    }

}