package br.com.fitnesspro.common.repository.sync.importation

import br.com.fitnesspor.service.data.access.webclient.general.AcademyWebClient
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.sync.importation.common.AbstractImportationRepository
import br.com.fitnesspro.local.data.access.dao.AcademyDAO
import br.com.fitnesspro.model.general.Academy
import br.com.fitnesspro.model.sync.EnumSyncModule
import br.com.fitnesspro.shared.communication.dtos.general.AcademyDTO
import br.com.fitnesspro.shared.communication.filter.CommonImportFilter
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.responses.ReadServiceResponse

class AcademyImportationRepository(
    private val academyDAO: AcademyDAO,
    private val academyWebClient: AcademyWebClient
): AbstractImportationRepository<AcademyDTO, Academy, AcademyDAO>() {

    override fun getDescription(): String {
        return context.getString(R.string.academy_importation_descrition)
    }

    override fun getModule() = EnumSyncModule.GENERAL

    override suspend fun getImportationData(
        token: String,
        filter: CommonImportFilter,
        pageInfos: ImportPageInfos
    ): ReadServiceResponse<AcademyDTO> {
        return academyWebClient.importAcademies(token, filter, pageInfos)
    }

    override suspend fun hasEntityWithId(id: String): Boolean {
        return academyDAO.hasAcademyWithId(id)
    }

    override suspend fun convertDTOToEntity(dto: AcademyDTO): Academy {
        return Academy(
            id = dto.id!!,
            name = dto.name!!,
            address = dto.address!!,
            phone = dto.phone!!,
            active = dto.active,
        )
    }
}