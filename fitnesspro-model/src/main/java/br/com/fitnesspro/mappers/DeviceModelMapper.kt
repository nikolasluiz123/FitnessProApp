package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.authentication.Device
import br.com.fitnesspro.shared.communication.dtos.serviceauth.DeviceDTO

fun Device.getDeviceDTO(): DeviceDTO {
    return DeviceDTO(
        id = id,
        model = model,
        brand = brand,
        androidVersion = androidVersion,
        active = active,
    )
}

fun DeviceDTO.getDevice(): Device {
    return Device(
        id = id!!,
        model = model,
        brand = brand,
        androidVersion = androidVersion,
        active = active,
        applicationId = application?.id
    )
}