package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.general.Academy
import br.com.fitnesspro.model.general.PersonAcademyTime
import br.com.fitnesspro.shared.communication.dtos.general.AcademyDTO
import br.com.fitnesspro.shared.communication.dtos.general.PersonAcademyTimeDTO
import br.com.fitnesspro.to.TOAcademy
import br.com.fitnesspro.to.TOPersonAcademyTime

fun Academy.getTOAcademy(): TOAcademy {
    return TOAcademy(
        id = id,
        name = name,
        active = active,
        address = address,
        phone = phone,
    )
}

fun AcademyDTO.getAcademy(): Academy {
    return Academy(
        id = id!!,
        name = name,
        active = active,
        address = address,
        phone = phone,
    )
}

fun PersonAcademyTime.getTOPersonAcademyTime(academyName: String): TOPersonAcademyTime {
    return TOPersonAcademyTime(
        id = id,
        personId = personId,
        academyId = academyId,
        active = active,
        timeStart = timeStart,
        timeEnd = timeEnd,
        dayOfWeek = dayOfWeek,
        academyName = academyName,
    )
}

fun TOPersonAcademyTime.getPersonAcademyTime(): PersonAcademyTime {
    val model = PersonAcademyTime(
        personId = personId,
        academyId = academyId,
        active = active,
        timeStart = timeStart,
        timeEnd = timeEnd,
        dayOfWeek = dayOfWeek,
    )

    id?.let { model.id = it }

    return model
}

fun PersonAcademyTimeDTO.getPersonAcademyTime(): PersonAcademyTime {
    return PersonAcademyTime(
        id = id!!,
        personId = personId,
        academyId = academyId,
        active = active,
        timeStart = timeStart,
        timeEnd = timeEnd,
        dayOfWeek = dayOfWeek,
        transmissionState = EnumTransmissionState.TRANSMITTED,
    )
}

fun PersonAcademyTime.getPersonAcademyTimeDTO(): PersonAcademyTimeDTO {
    return PersonAcademyTimeDTO(
        id = id,
        personId = personId,
        academyId = academyId,
        active = active,
        timeStart = timeStart,
        timeEnd = timeEnd,
        dayOfWeek = dayOfWeek,
    )
}