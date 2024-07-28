package br.com.fitnesspro.service.data.access.extensions

import br.com.fitnesspro.service.data.access.adapters.LocalDateTimeTypeAdapter
import br.com.fitnesspro.service.data.access.adapters.LocalDateTypeAdapter
import br.com.fitnesspro.service.data.access.adapters.LocalTimeTypeAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

fun GsonBuilder.defaultGSon(): Gson {
    return this.registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
        .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter())
        .registerTypeHierarchyAdapter(LocalTime::class.java, LocalTimeTypeAdapter())
        .create()
}