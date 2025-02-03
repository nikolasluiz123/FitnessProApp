package br.com.fitnesspro.core.extensions

import android.content.Context
import br.com.fitnesspro.core.R
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
}

fun Long.toLocalDateTime(): LocalDateTime {
    return Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDateTime()
}

fun Long.toLocalDateFormattedOnlyNumbers(enumDateTimePatterns: EnumDateTimePatterns): String {
    return toLocalDate().format(enumDateTimePatterns).replace("/", "")
}

fun Long.toDurationFormatted(context: Context): String? {
    val duration = Duration.ofSeconds(this)
    val hours = duration.toHours()
    val minutes = duration.toMinutes() % 60
    val remainingSeconds = duration.seconds % 60

    return when {
        hours > 0 && minutes > 0 && remainingSeconds > 0 -> {
            val labelHours = context.resources.getQuantityString(R.plurals.label_hour, hours.toInt(), hours)
            val labelMinutes = context.resources.getQuantityString(R.plurals.label_minutes, minutes.toInt(), minutes)
            val labelSeconds = context.resources.getQuantityString(R.plurals.label_seconds, remainingSeconds.toInt(), remainingSeconds)

            context.getString(R.string.label_three_times, labelHours, labelMinutes, labelSeconds)
        }

        hours > 0 && minutes > 0 -> {
            val labelHours = context.resources.getQuantityString(R.plurals.label_hour, hours.toInt(), hours)
            val labelMinutes = context.resources.getQuantityString(R.plurals.label_minutes, minutes.toInt(), minutes)

            context.getString(R.string.label_two_times, labelHours, labelMinutes)
        }

        hours > 0 && remainingSeconds > 0 -> {
            val labelHours = context.resources.getQuantityString(R.plurals.label_hour, hours.toInt(), hours)
            val labelSeconds = context.resources.getQuantityString(R.plurals.label_seconds, remainingSeconds.toInt(), remainingSeconds)

            context.getString(R.string.label_two_times, labelHours, labelSeconds)
        }

        hours > 0 -> {
            context.resources.getQuantityString(R.plurals.label_hour, hours.toInt(), hours)
        }

        minutes > 0 && remainingSeconds > 0 -> {
            val labelMinutes = context.resources.getQuantityString(R.plurals.label_minutes, minutes.toInt(), minutes)
            val labelSeconds = context.resources.getQuantityString(R.plurals.label_seconds, remainingSeconds.toInt(), remainingSeconds)

            context.getString(R.string.label_two_times, labelMinutes, labelSeconds)
        }

        minutes > 0 -> {
            context.resources.getQuantityString(R.plurals.label_minutes, minutes.toInt(), minutes)
        }

        remainingSeconds > 0 -> {
            context.resources.getQuantityString(R.plurals.label_seconds, remainingSeconds.toInt(), remainingSeconds)
        }

        else -> null
    }
}