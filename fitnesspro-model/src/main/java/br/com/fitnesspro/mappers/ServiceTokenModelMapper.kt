package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.authentication.ServiceToken
import br.com.fitnesspro.model.enums.EnumTokenType
import br.com.fitnesspro.shared.communication.dtos.serviceauth.ServiceTokenDTO
import br.com.fitnesspro.shared.communication.enums.serviceauth.EnumTokenType as EnumServiceTokenType

fun ServiceTokenDTO.getServiceToken(): ServiceToken {
    return ServiceToken(
        id = id!!,
        userId = user?.id,
        applicationId = application?.id,
        deviceId = device?.id,
        jwtToken = jwtToken,
        type = getTokenType(type!!),
        expirationDate = expirationDate,
        creationDate = creationDate
    )
}

private fun getTokenType(type: EnumServiceTokenType): EnumTokenType {
    return when (type) {
        EnumServiceTokenType.USER_AUTHENTICATION_TOKEN -> EnumTokenType.USER_AUTHENTICATION_TOKEN
        EnumServiceTokenType.DEVICE_TOKEN -> EnumTokenType.DEVICE_TOKEN
        EnumServiceTokenType.APPLICATION_TOKEN -> EnumTokenType.APPLICATION_TOKEN
    }
}