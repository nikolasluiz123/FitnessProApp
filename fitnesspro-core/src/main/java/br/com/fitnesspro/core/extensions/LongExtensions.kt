package br.com.fitnesspro.core.extensions

import android.content.Context
import android.text.format.Formatter
import br.com.fitnesspro.core.R
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
}

fun Long.toLocalDateTime(): LocalDateTime {
    return Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()
}

fun Long.toLocalDateFormattedOnlyNumbers(enumDateTimePatterns: EnumDateTimePatterns): String {
    return toLocalDate().format(enumDateTimePatterns).replace("/", "")
}

fun Long.toReadableDuration(context: Context): String {
    val duration = Duration.ofMillis(this)

    val hours = duration.toHours()
    val minutes = duration.toMinutes() % 60
    val seconds = duration.seconds % 60

    return buildList {
        if (hours > 0) {
            add(context.resources.getQuantityString(R.plurals.label_hours, hours.toInt(), hours))
        }

        if (minutes > 0) {
            add(context.resources.getQuantityString(R.plurals.label_minutes, minutes.toInt(), minutes))
        }

        if (seconds > 0 || isEmpty()) {
            add(context.resources.getQuantityString(R.plurals.label_seconds, seconds.toInt(), seconds))
        }
    }.joinToString(context.getString(R.string.label_and))
}

fun Long.toMillis(unit: ChronoUnit): Long {
    require(unit.duration.isZero.not()) { "Valor de unidade inválido: $unit" }
    return unit.duration.multipliedBy(this).toMillis()
}

fun Long.millisTo(unit: ChronoUnit): Long {
    require(unit.duration.toMillis() != 0L) { "Valor de unidade inválido: $unit" }
    return this / unit.duration.toMillis()
}

fun Long.bestChronoUnit(): ChronoUnit {
    val secondLimit = 59 * ChronoUnit.SECONDS.duration.toMillis()
    val minuteLimit = 59 * ChronoUnit.MINUTES.duration.toMillis()
    val hourLimit = 23 * ChronoUnit.HOURS.duration.toMillis()

    return when {
        this <= secondLimit -> ChronoUnit.SECONDS
        this <= minuteLimit -> ChronoUnit.MINUTES
        this <= hourLimit -> ChronoUnit.HOURS
        else -> throw IllegalArgumentException("No suitable ChronoUnit for value: $this ms")
    }
}

fun Long.toReadableFileSize(context: Context): String {
    return Formatter.formatFileSize(context, this * 1024)
}