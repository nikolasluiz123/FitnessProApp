package br.com.fitnesspro.core.extensions

import br.com.fitnesspro.core.adapters.LocalDateTimeTypeAdapter
import br.com.fitnesspro.core.adapters.LocalDateTypeAdapter
import br.com.fitnesspro.core.adapters.LocalTimeTypeAdapter
import br.com.fitnesspro.core.adapters.LocalTimeTypeServiceAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

fun GsonBuilder.defaultGSonComposeNavigation(): Gson {
    return this.registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
        .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter())
        .registerTypeHierarchyAdapter(LocalTime::class.java, LocalTimeTypeAdapter())
        .create()
}

fun GsonBuilder.defaultGSonService(): Gson {
    return this.registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
        .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter())
        .registerTypeHierarchyAdapter(LocalTime::class.java, LocalTimeTypeServiceAdapter())
        .create()
}