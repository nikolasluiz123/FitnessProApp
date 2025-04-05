package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.general.Academy
import br.com.fitnesspro.model.general.PersonAcademyTime
import br.com.fitnesspro.to.TOAcademy
import br.com.fitnesspro.to.TOPersonAcademyTime

fun Academy.toTOAcademy(): TOAcademy {
    return TOAcademy(
        id = id,
        name = name,
        address = address,
        phone = phone,
        active = active,
    )
}

fun PersonAcademyTime.toTOPersonAcademyTime(toAcademy: TOAcademy): TOPersonAcademyTime {
    return TOPersonAcademyTime(
        id = id,
        personId = personId,
        toAcademy = toAcademy,
        timeStart = timeStart,
        timeEnd = timeEnd,
        dayOfWeek = dayOfWeek,
        active = active,
    )
}

fun TOPersonAcademyTime.toPersonAcademyTime(): PersonAcademyTime {
    val model = PersonAcademyTime(
        personId = personId,
        academyId = toAcademy?.id,
        timeStart = timeStart,
        timeEnd = timeEnd,
        dayOfWeek = dayOfWeek,
        active = active,
    )

    id?.let { model.id = it }

    return model
}