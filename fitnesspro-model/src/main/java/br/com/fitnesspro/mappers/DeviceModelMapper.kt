package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.authentication.Device
import br.com.fitnesspro.shared.communication.dtos.general.PersonDTO
import br.com.fitnesspro.shared.communication.dtos.serviceauth.DeviceDTO
import br.com.fitnesspro.shared.communication.dtos.serviceauth.interfaces.IDeviceDTO

fun Device.getDeviceDTO(personDTO: PersonDTO): DeviceDTO {
    return DeviceDTO(
        id = id,
        model = model,
        brand = brand,
        androidVersion = androidVersion,
        active = active,
        firebaseMessagingToken = firebaseMessagingToken,
        zoneId = zoneId,
        person = personDTO,
    )
}

fun IDeviceDTO.getDevice(): Device {
    return Device(
        id = id!!,
        model = model,
        brand = brand,
        androidVersion = androidVersion,
        active = active,
        applicationId = application?.id,
        firebaseMessagingToken = firebaseMessagingToken,
        zoneId = zoneId,
        personId = person?.id,
    )
}