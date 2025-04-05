package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOUser

fun Person.toTOPerson(toUser: TOUser): TOPerson {
    return TOPerson(
        id = id,
        name = name,
        birthDate = birthDate,
        phone = phone,
        toUser = toUser,
        active = active,
    )
}

fun TOPerson.toPerson(userId: String): Person {
    val model = Person(
        name = name,
        birthDate = birthDate,
        phone = phone,
        userId = userId,
        active = active,
    )

    id?.let { model.id = it }

    return model
}
