package br.com.fitnesspro.common.repository.sync.importation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.general.PersonWebClient
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.sync.importation.common.AbstractImportationRepository
import br.com.fitnesspro.local.data.access.dao.PersonAcademyTimeDAO
import br.com.fitnesspro.mappers.getPersonAcademyTime
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.general.PersonAcademyTime
import br.com.fitnesspro.shared.communication.dtos.general.PersonAcademyTimeDTO
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.CommonImportFilter
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse

class PersonAcademyTimeImportationRepository(
    context: Context,
    private val webClient: PersonWebClient,
    private val personAcademyTimeDAO: PersonAcademyTimeDAO,
): AbstractImportationRepository<PersonAcademyTimeDTO, PersonAcademyTime, PersonAcademyTimeDAO, CommonImportFilter>(context) {

    override fun getDescription(): String {
        return context.getString(R.string.person_importation_descrition)
    }

    override fun getModule() = EnumSyncModule.GENERAL

    override suspend fun getImportationData(
        token: String,
        filter: CommonImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<PersonAcademyTimeDTO> {
        return webClient.importPersonAcademyTime(token, filter, pageInfos)
    }

    override suspend fun hasEntityWithId(id: String): Boolean {
        return personAcademyTimeDAO.hasPersonAcademyTimeWithId(id)
    }

    override fun getOperationDAO(): PersonAcademyTimeDAO {
        return personAcademyTimeDAO
    }

    override suspend fun convertDTOToEntity(dto: PersonAcademyTimeDTO): PersonAcademyTime {
        return dto.getPersonAcademyTime()
    }
}