package br.com.fitnesspro.common.repository.sync.importation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.general.PersonWebClient
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.sync.importation.common.AbstractImportationRepository
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.mappers.getUser
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.shared.communication.dtos.general.UserDTO
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.CommonImportFilter
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse

class UserImportationRepository(
    context: Context,
    private val webClient: PersonWebClient,
): AbstractImportationRepository<UserDTO, User, UserDAO, CommonImportFilter>(context) {

    override fun getDescription(): String {
        return context.getString(R.string.user_importation_descrition)
    }

    override fun getModule() = EnumSyncModule.GENERAL

    override suspend fun getImportationData(
        token: String,
        filter: CommonImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<UserDTO> {
        return webClient.importUsers(token, filter, pageInfos)
    }

    override suspend fun hasEntityWithId(id: String): Boolean {
        return userDAO.hasUserWithId(id)
    }

    override fun getOperationDAO(): UserDAO {
        return userDAO
    }

    override suspend fun convertDTOToEntity(dto: UserDTO): User {
        return dto.getUser()
    }
}