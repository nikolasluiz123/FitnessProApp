package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.authentication.Device
import br.com.fitnesspro.shared.communication.dtos.serviceauth.ApplicationDTO
import br.com.fitnesspro.shared.communication.dtos.serviceauth.DeviceDTO

class DeviceModelMapper: AbstractModelMapper() {

    init {
        mapper.typeMap(DeviceDTO::class.java, Device::class.java).addMappings { mapper ->
            mapper.map(
                { to: DeviceDTO -> to.application?.id },
                { device: Device, value: String? -> device.applicationId = value }
            )
        }

        mapper.typeMap(Device::class.java, DeviceDTO::class.java).addMappings { mapper ->
            mapper.map(
                { to: Device -> to.applicationId },
                { device: DeviceDTO, value: String? -> device.application = ApplicationDTO(id = value) }
            )
        }
    }

    fun getDeviceDTO(device: Device): DeviceDTO {
        return mapper.map(device, DeviceDTO::class.java)
    }

    fun getDevice(deviceDTO: DeviceDTO): Device {
        return mapper.map(deviceDTO, Device::class.java)
    }
}