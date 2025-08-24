package br.com.fitnesspor.service.data.access.adapters

import br.com.fitnesspor.service.data.access.extensions.defaultServiceGSon
import br.com.fitnesspro.shared.communication.dtos.general.UserDTO
import br.com.fitnesspro.shared.communication.dtos.general.interfaces.IUserDTO
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class UserDTOTypeAdapter : TypeAdapter<IUserDTO>() {
    override fun write(out: JsonWriter, value: IUserDTO?) {
        val gson = GsonBuilder().defaultServiceGSon()
        gson.toJson(value as UserDTO?, UserDTO::class.java, out)
    }

    override fun read(reader: JsonReader): IUserDTO {
        val gson = GsonBuilder().defaultServiceGSon()
        return gson.fromJson(reader, UserDTO::class.java)
    }
}