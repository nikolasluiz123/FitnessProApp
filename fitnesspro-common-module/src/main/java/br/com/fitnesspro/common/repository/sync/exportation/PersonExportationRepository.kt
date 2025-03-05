package br.com.fitnesspro.common.repository.sync.exportation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.general.PersonWebClient
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.sync.exportation.common.AbstractExportationRepository
import br.com.fitnesspro.local.data.access.dao.PersonDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.shared.communication.dtos.general.PersonDTO
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse

class PersonExportationRepository(
    context: Context,
    private val personDAO: PersonDAO,
    private val personWebClient: PersonWebClient,
): AbstractExportationRepository<PersonDTO, Person, PersonDAO>(context) {

    override suspend fun getExportationData(
        pageInfos: ExportPageInfos
    ): List<Person> {
        return personDAO.getExportationData(pageInfos)
    }

    override fun getOperationDAO(): PersonDAO {
        return personDAO
    }

    override suspend fun callExportationService(
        modelList: List<Person>,
        token: String
    ): ExportationServiceResponse {
        val userList = modelList.map { userDAO.findById(it.userId!!)!! }

        return personWebClient.savePersonBatch(
            token = token,
            persons = modelList,
            users = userList
        )
    }

    override fun getDescription(): String {
        return context.getString(R.string.sync_module_person)
    }

    override fun getModule() = EnumSyncModule.GENERAL
}