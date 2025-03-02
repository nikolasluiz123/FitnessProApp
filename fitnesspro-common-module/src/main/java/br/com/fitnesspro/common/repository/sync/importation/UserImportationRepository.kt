package br.com.fitnesspro.common.repository.sync.importation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.general.PersonWebClient
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.sync.importation.common.AbstractImportationRepository
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.shared.communication.dtos.general.UserDTO
import br.com.fitnesspro.shared.communication.filter.CommonImportFilter
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.responses.ReadServiceResponse
import br.com.fitnesspro.models.general.enums.EnumUserType as EnumUserTypeService

class UserImportationRepository(
    context: Context,
    private val webClient: PersonWebClient
): AbstractImportationRepository<UserDTO, User, UserDAO>(context) {

    override fun getDescription(): String {
        return context.getString(R.string.user_importation_descrition)
    }

    override fun getModule() = EnumSyncModule.GENERAL

    override suspend fun getImportationData(
        token: String,
        filter: CommonImportFilter,
        pageInfos: ImportPageInfos
    ): ReadServiceResponse<UserDTO> {
        return webClient.importUsers(token, filter, pageInfos)
    }

    override suspend fun hasEntityWithId(id: String): Boolean {
        return userDAO.hasUserWithId(id)
    }

    override fun getOperationDAO(): UserDAO {
        return userDAO
    }

    override suspend fun convertDTOToEntity(dto: UserDTO): User {
        return User(
            id = dto.id!!,
            active = dto.active,
            email = dto.email!!,
            password = dto.password!!,
            type = getEnumUserType(dto),
        )
    }

    private fun getEnumUserType(dto: UserDTO): EnumUserType {
        return when (dto.type!!) {
            EnumUserTypeService.PERSONAL_TRAINER -> EnumUserType.PERSONAL_TRAINER
            EnumUserTypeService.NUTRITIONIST -> EnumUserType.NUTRITIONIST
            EnumUserTypeService.ACADEMY_MEMBER -> EnumUserType.ACADEMY_MEMBER
        }
    }
}