package br.com.fitnesspro.core.extensions

import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
}

fun Long.toLocalDateFormattedOnlyNumbers(enumDateTimePatterns: EnumDateTimePatterns): String {
    return toLocalDate().format(enumDateTimePatterns).replace("/", "")
}