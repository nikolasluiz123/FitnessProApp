package br.com.fitnesspro.core.extensions

import br.com.fitnesspro.core.adapters.InstantTypeAdapter
import br.com.fitnesspro.core.adapters.LocalDateTimeTypeAdapter
import br.com.fitnesspro.core.adapters.LocalDateTypeAdapter
import br.com.fitnesspro.core.adapters.LocalTimeTypeAdapter
import br.com.fitnesspro.core.adapters.OffsetDateTimeTypeAdapter
import br.com.fitnesspro.core.adapters.ZoneOffsetTypeAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

fun GsonBuilder.defaultGSon(serializeNulls: Boolean = false): Gson {
    val builder = this.registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
        .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter())
        .registerTypeAdapter(LocalTime::class.java, LocalTimeTypeAdapter())
        .registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeTypeAdapter())
        .registerTypeAdapter(Instant::class.java, InstantTypeAdapter())
        .registerTypeAdapter(ZoneOffset::class.java, ZoneOffsetTypeAdapter())

    if (serializeNulls) {
        builder.serializeNulls()
    }

    return builder.create()
}