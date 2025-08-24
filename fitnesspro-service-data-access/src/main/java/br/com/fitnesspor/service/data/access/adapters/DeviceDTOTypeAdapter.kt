package br.com.fitnesspor.service.data.access.adapters

import br.com.fitnesspor.service.data.access.extensions.defaultServiceGSon
import br.com.fitnesspro.shared.communication.dtos.serviceauth.DeviceDTO
import br.com.fitnesspro.shared.communication.dtos.serviceauth.interfaces.IDeviceDTO
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class DeviceDTOTypeAdapter : TypeAdapter<IDeviceDTO>() {
    override fun write(out: JsonWriter, value: IDeviceDTO?) {
        val gson = GsonBuilder().defaultServiceGSon()
        gson.toJson(value as DeviceDTO?, DeviceDTO::class.java, out)
    }

    override fun read(reader: JsonReader): IDeviceDTO {
        val gson = GsonBuilder().defaultServiceGSon()
        return gson.fromJson(reader, DeviceDTO::class.java)
    }
}