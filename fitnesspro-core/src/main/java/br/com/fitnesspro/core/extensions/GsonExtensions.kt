package br.com.fitnesspro.core.extensions

import br.com.fitnesspro.core.adapters.LocalDateTimeTypeAdapter
import br.com.fitnesspro.core.adapters.LocalDateTypeAdapter
import br.com.fitnesspro.core.adapters.LocalTimeTypeAdapter
import br.com.fitnesspro.core.adapters.OffsetDateTimeTypeAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime

fun GsonBuilder.defaultGSon(): Gson {
    return this.registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
        .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter())
        .registerTypeAdapter(LocalTime::class.java, LocalTimeTypeAdapter())
        .registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeTypeAdapter())
        .create()
}