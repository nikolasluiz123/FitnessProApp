package br.com.fitnesspro.mappers

import br.com.android.room.toolkit.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.shared.communication.dtos.general.PersonDTO
import br.com.fitnesspro.shared.communication.dtos.general.UserDTO
import br.com.fitnesspro.shared.communication.dtos.general.interfaces.IPersonDTO
import br.com.fitnesspro.shared.communication.dtos.general.interfaces.IUserDTO
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOUser
import br.com.fitnesspro.shared.communication.enums.general.EnumUserType as EnumUserTypeService

fun Person.getTOPerson(user: User): TOPerson {
    return TOPerson(
        id = id,
        name = name,
        birthDate = birthDate,
        phone = phone,
        user = user.getTOUser(),
        active = active,
    )
}

fun IPersonDTO.getTOPerson(): TOPerson {
    return TOPerson(
        id = id,
        name = name,
        birthDate = birthDate,
        phone = phone,
        active = active,
        user = user?.getTOUser(),
    )
}

fun TOPerson.getPerson(): Person {
    val model = Person(
        name = name,
        birthDate = birthDate,
        phone = phone,
        active = active,
        userId = user?.id,
    )

    id?.let { model.id = it }

    return model
}

fun TOPerson.getPersonDTO(): PersonDTO {
    return PersonDTO(
        id = id,
        name = name,
        birthDate = birthDate,
        phone = phone,
        active = active,
        user = user?.getUserDTO(),
    )
}

fun IPersonDTO.getPerson(): Person {
    return Person(
        id = id!!,
        name = name,
        birthDate = birthDate,
        phone = phone,
        active = active,
        userId = user?.id,
        transmissionState = EnumTransmissionState.TRANSMITTED
    )
}

fun Person.getPersonDTO(user: User): PersonDTO {
    return PersonDTO(
        id = id,
        name = name,
        birthDate = birthDate,
        phone = phone,
        active = active,
        user = user.getUserDTO(),
    )
}

fun User.getTOUser(): TOUser {
    return TOUser(
        id = id,
        email = email,
        password = password,
        active = active,
        type = type,
    )
}

fun User.getUserDTO(): UserDTO {
    return UserDTO(
        id = id,
        email = email,
        password = password,
        active = active,
        type = getServiceUserType(type!!)
    )
}

fun TOUser.getUser(): User {
    val model = User(
        email = email,
        password = password,
        active = active,
        type = type,
    )

    if (id != null) {
        model.id = id!!
    } else {
        id = model.id
    }

    return model
}

fun TOUser.getUserDTO(): UserDTO {
    return UserDTO(
        id = id,
        email = email,
        password = password,
        active = active,
        type = getServiceUserType(type!!)
    )
}

fun IUserDTO.getUser(): User {
    return User(
        id = id!!,
        email = email,
        password = password,
        active = active,
        type = getUserType(type!!),
        transmissionState = EnumTransmissionState.TRANSMITTED
    )
}

fun IUserDTO.getTOUser(): TOUser {
    return TOUser(
        id = id,
        email = email,
        password = password,
        active = active,
        type = getUserType(type!!),
    )
}

fun getServiceUserType(type: EnumUserType): EnumUserTypeService {
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
        EnumUserTypeService.ADMINISTRATOR -> throw IllegalArgumentException("O valor ${type.name} é inválido para obter o EnumUserType")
    }
}