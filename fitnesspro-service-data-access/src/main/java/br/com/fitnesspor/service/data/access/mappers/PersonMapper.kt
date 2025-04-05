package br.com.fitnesspor.service.data.access.mappers

import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.PersonAcademyTime
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.shared.communication.dtos.general.PersonAcademyTimeDTO
import br.com.fitnesspro.shared.communication.dtos.general.PersonDTO

fun PersonAcademyTime.toPersonAcademyTimeDTO(): PersonAcademyTimeDTO {
    return PersonAcademyTimeDTO(
        id = id,
        personId = personId,
        academyId = academyId,
        timeStart = timeStart,
        timeEnd = timeEnd,
        dayOfWeek = dayOfWeek,
        active = active
    )
}


fun Person.toPersonDTO(user: User): PersonDTO {
    return PersonDTO(
        id = id,
        name = name,
        active = active,
        birthDate = birthDate,
        phone = phone,
        user = user.toUserDTO()
    )
}