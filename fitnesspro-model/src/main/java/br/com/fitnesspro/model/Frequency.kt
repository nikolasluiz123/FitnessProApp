package br.com.fitnesspro.model

import java.time.LocalTime

data class Frequency(
    val id: Long? = null,
    var dayWeek: String? = null,
    var start: LocalTime? = null,
    var end: LocalTime? = null,
    var academy: Long? = null,
    var username: String? = null,
    var academyName: String? = null,
    var dayWeekDisplay: String? = null
)
