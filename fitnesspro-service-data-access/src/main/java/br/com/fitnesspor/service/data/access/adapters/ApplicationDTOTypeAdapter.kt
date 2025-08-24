package br.com.fitnesspor.service.data.access.adapters

import br.com.fitnesspor.service.data.access.extensions.defaultServiceGSon
import br.com.fitnesspro.shared.communication.dtos.serviceauth.ApplicationDTO
import br.com.fitnesspro.shared.communication.dtos.serviceauth.interfaces.IApplicationDTO
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class ApplicationDTOTypeAdapter : TypeAdapter<IApplicationDTO>() {
    override fun write(out: JsonWriter, value: IApplicationDTO?) {
        val gson = GsonBuilder().defaultServiceGSon()
        gson.toJson(value as ApplicationDTO?, ApplicationDTO::class.java, out)
    }

    override fun read(reader: JsonReader): IApplicationDTO {
        val gson = GsonBuilder().defaultServiceGSon()
        return gson.fromJson(reader, ApplicationDTO::class.java)
    }
}