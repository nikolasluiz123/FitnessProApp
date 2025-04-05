package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.to.TOUser

fun User.toTOUser(): TOUser {
    return TOUser(
        id = id,
        email = email,
        password = password,
        type = type,
        active = active,
        serviceToken = serviceToken,
    )
}

fun TOUser.toUser(): User {
    val model = User(
        email = email,
        password = password,
        type = type,
        active = active,
    )

    id?.let { model.id = it }

    return model
}