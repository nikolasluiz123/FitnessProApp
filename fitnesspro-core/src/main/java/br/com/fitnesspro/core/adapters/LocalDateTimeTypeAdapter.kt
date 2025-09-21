package br.com.fitnesspro.core.adapters

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeTypeAdapter : JsonSerializer<LocalDateTime?>, JsonDeserializer<LocalDateTime?> {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override fun serialize(
        src: LocalDateTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return if (src == null) {
            JsonNull.INSTANCE
        } else {
            JsonPrimitive(src.format(formatter))
        }
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDateTime? {
        return if (json == null || json.isJsonNull) {
            null
        } else {
            LocalDateTime.parse(json.asString, formatter)
        }
    }
}