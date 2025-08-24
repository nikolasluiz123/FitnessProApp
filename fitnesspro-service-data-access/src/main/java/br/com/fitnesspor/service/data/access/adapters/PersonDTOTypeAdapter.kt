package br.com.fitnesspor.service.data.access.adapters

import br.com.fitnesspor.service.data.access.extensions.defaultServiceGSon
import br.com.fitnesspro.shared.communication.dtos.general.PersonDTO
import br.com.fitnesspro.shared.communication.dtos.general.interfaces.IPersonDTO
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class PersonDTOTypeAdapter : TypeAdapter<IPersonDTO>() {
    override fun write(out: JsonWriter, value: IPersonDTO?) {
        val gson = GsonBuilder().defaultServiceGSon()
        gson.toJson(value as PersonDTO?, PersonDTO::class.java, out)
    }

    override fun read(reader: JsonReader): IPersonDTO {
        val gson = GsonBuilder().defaultServiceGSon()
        return gson.fromJson(reader, PersonDTO::class.java)
    }
}