package br.com.fitnesspro.common.repository.sync.importation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.general.PersonWebClient
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.sync.importation.common.AbstractImportationRepository
import br.com.fitnesspro.local.data.access.dao.PersonDAO
import br.com.fitnesspro.mappers.getPerson
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.shared.communication.dtos.general.PersonDTO
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.CommonImportFilter
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse

class PersonImportationRepository(
    context: Context,
    private val webClient: PersonWebClient,
    private val personDAO: PersonDAO,
): AbstractImportationRepository<PersonDTO, Person, PersonDAO>(context) {

    override fun getDescription(): String {
        return context.getString(R.string.person_importation_descrition)
    }

    override fun getModule() = EnumSyncModule.GENERAL

    override suspend fun getImportationData(
        token: String,
        filter: CommonImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<PersonDTO> {
        return webClient.importPersons(token, filter, pageInfos)
    }

    override suspend fun hasEntityWithId(id: String): Boolean {
        return personDAO.hasPersonWithId(id)
    }

    override fun getOperationDAO(): PersonDAO {
        return personDAO
    }

    override suspend fun convertDTOToEntity(dto: PersonDTO): Person {
        return dto.getPerson()
    }
}