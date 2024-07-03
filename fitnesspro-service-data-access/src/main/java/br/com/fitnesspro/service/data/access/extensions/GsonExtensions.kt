package br.com.fitnesspro.service.data.access.extensions

import br.com.fitnesspro.service.data.access.adapters.LocalDateTimeTypeAdapter
import br.com.fitnesspro.service.data.access.adapters.LocalDateTypeAdapter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.LocalDate
import java.time.LocalDateTime

fun GsonBuilder.defaultGSon(): Gson {
    return this.registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
        .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter())
        .create()
}