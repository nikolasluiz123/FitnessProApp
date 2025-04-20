package br.com.fitnesspor.service.data.access.mappers

import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.shared.communication.dtos.general.UserDTO
import br.com.fitnesspro.shared.communication.enums.general.EnumUserType as EnumUserTypeService

fun User.toUserDTO(): UserDTO {
    return UserDTO(
        id = id,
        email = email,
        password = password,
        active = active,
        type = getUserType(type!!),
    )
}

fun getUserType(type: EnumUserType): EnumUserTypeService {
    return when (type) {
        EnumUserType.PERSONAL_TRAINER -> EnumUserTypeService.PERSONAL_TRAINER
        EnumUserType.NUTRITIONIST -> EnumUserTypeService.NUTRITIONIST
        EnumUserType.ACADEMY_MEMBER -> EnumUserTypeService.ACADEMY_MEMBER
    }
}

fun getUserType(type: EnumUserTypeService): EnumUserType {
    return when (type) {
        EnumUserTypeService.PERSONAL_TRAINER -> EnumUserType.PERSONAL_TRAINER
        EnumUserTypeService.NUTRITIONIST -> EnumUserType.NUTRITIONIST
        EnumUserTypeService.ACADEMY_MEMBER -> EnumUserType.ACADEMY_MEMBER
        EnumUserTypeService.ADMINISTRATOR -> throw IllegalArgumentException("O valor $type é invalido")
    }
}