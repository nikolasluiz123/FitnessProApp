package br.com.fitnesspro.common.repository.importation

import br.com.fitnesspor.service.data.access.webclient.general.PersonWebClient
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.importation.common.AbstractImportationRepository
import br.com.fitnesspro.local.data.access.dao.PersonDAO
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.sync.EnumSyncModule
import br.com.fitnesspro.shared.communication.dtos.general.PersonDTO
import br.com.fitnesspro.shared.communication.filter.CommonImportFilter
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.responses.ReadServiceResponse

class PersonImportationRepository(
    private val webClient: PersonWebClient,
    private val personDAO: PersonDAO
): AbstractImportationRepository<PersonDTO, Person, PersonDAO>() {

    override fun getDescription(): String {
        return context.getString(R.string.person_importation_descrition)
    }

    override fun getModule() = EnumSyncModule.GENERAL

    override suspend fun getImportationData(
        token: String,
        filter: CommonImportFilter,
        pageInfos: ImportPageInfos
    ): ReadServiceResponse<PersonDTO> {
        return webClient.importPersons(token, filter, pageInfos)
    }

    override suspend fun hasEntityWithId(id: String): Boolean {
        return personDAO.hasPersonWithId(id)
    }

    override suspend fun convertDTOToEntity(dto: PersonDTO): Person {
        return Person(
            id = dto.id!!,
            active = dto.active,
            name = dto.name!!,
            birthDate = dto.birthDate!!,
            phone = dto.phone,
            userId = dto.user?.id
        )
    }
}