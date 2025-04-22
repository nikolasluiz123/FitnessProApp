package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.authentication.Application
import br.com.fitnesspro.shared.communication.dtos.serviceauth.ApplicationDTO

fun ApplicationDTO.getApplication(): Application {
    return Application(
        id = id!!,
        name = name,
        active = active
    )
}