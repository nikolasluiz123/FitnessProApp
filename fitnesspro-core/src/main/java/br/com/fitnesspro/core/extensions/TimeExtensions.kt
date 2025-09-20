package br.com.fitnesspro.core.extensions

import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import java.time.DateTimeException
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Converte uma string em um objeto LocalDate utilizando o padrão especificado.
 *
 * @param enumDateTimePatterns O padrão de formato para a conversão.
 * @return O objeto LocalDate resultante da conversão.
 * @receiver A string contendo a data a ser convertida.
 * @author Nikolas Luiz Schmitt
 */
fun String.parseToLocalDate(enumDateTimePatterns: EnumDateTimePatterns): LocalDate? {
    if (this.isEmpty()) return null

    return try {
        LocalDate.parse(this, DateTimeFormatter.ofPattern(enumDateTimePatterns.pattern))
    } catch (_: DateTimeException) {
        null
    }
}

/**
 * Converte uma string em um objeto LocalTime utilizando o padrão especificado.
 *
 * @param enumDateTimePatterns O padrão de formato para a conversão.
 * @return O objeto LocalTime resultante da conversão.
 * @receiver A string contendo a hora a ser convertida.
 * @author Nikolas Luiz Schmitt
 */
fun String.parseToLocalTime(enumDateTimePatterns: EnumDateTimePatterns): LocalTime? {
    if (this.isEmpty()) return null

    return try {
        LocalTime.parse(this, DateTimeFormatter.ofPattern(enumDateTimePatterns.pattern))
    } catch (_: DateTimeException) {
        null
    }
}

fun String.parseTimeToOffsetDateTime(date: LocalDate, enumDateTimePatterns: EnumDateTimePatterns): OffsetDateTime? {
    if (this.isEmpty()) return null

    val localTime = parseToLocalTime(enumDateTimePatterns) ?: return null

    return date.getOffsetDateTime(localTime)
}

/**
 * Converte uma string em um objeto LocalDateTime utilizando o padrão especificado.
 *
 * @param enumDateTimePatterns O padrão de formato para a conversão.
 * @return O objeto LocalDateTime resultante da conversão.
 * @receiver A string contendo a data e hora a ser convertida.
 * @throws IndexOutOfBoundsException em caso de falta de informações necessárias.
 * @throws Exception em outros erros.
 * @author Nikolas Luiz Schmitt
 */
fun String.parseToLocalDateTime(enumDateTimePatterns: EnumDateTimePatterns): LocalDateTime? {
    if (this.isEmpty()) return null

    return try {
        LocalDateTime.parse(this, DateTimeFormatter.ofPattern(enumDateTimePatterns.pattern))
    } catch (_: DateTimeException) {
        null
    }
}

fun String.formatFileDateToDateTime(): String? {
    return parseToLocalDateTime(EnumDateTimePatterns.DATE_TIME_FILE_NAME)?.format(EnumDateTimePatterns.DATE_TIME)
}

/**
 * Formata um objeto LocalDate em uma string utilizando o padrão especificado.
 *
 * @param enumDateTimePatterns O padrão de formato para a formatação.
 * @return A string formatada resultante.
 * @receiver O objeto LocalDate a ser formatado.
 */
fun LocalDate.format(enumDateTimePatterns: EnumDateTimePatterns): String {
    return this.format(DateTimeFormatter.ofPattern(enumDateTimePatterns.pattern))
}

/**
 * Formata um objeto LocalTime em uma string utilizando o padrão especificado.
 *
 * @param enumDateTimePatterns O padrão de formato para a formatação.
 * @return A string formatada resultante.
 * @receiver O objeto LocalTime a ser formatado.
 */
fun LocalTime.format(enumDateTimePatterns: EnumDateTimePatterns): String {
    return this.format(DateTimeFormatter.ofPattern(enumDateTimePatterns.pattern))
}

/**
 * Formata um objeto LocalDateTime em uma string utilizando o padrão especificado.
 *
 * @param enumDateTimePatterns O padrão de formato para a formatação.
 * @return A string formatada resultante.
 * @receiver O objeto LocalDateTime a ser formatado.
 * @author Nikolas Luiz Schmitt
 */
fun LocalDateTime.format(enumDateTimePatterns: EnumDateTimePatterns): String {
    return this.format(DateTimeFormatter.ofPattern(enumDateTimePatterns.pattern))
}

fun Instant.format(enumDateTimePatterns: EnumDateTimePatterns): String {
    return this.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(enumDateTimePatterns.pattern))
}

fun Duration.formatSimpleTime(): String {
    val totalSeconds = this.seconds
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}

fun YearMonth.format(enumDateTimePatterns: EnumDateTimePatterns): String {
    return this.format(DateTimeFormatter.ofPattern(enumDateTimePatterns.pattern))
}

fun OffsetDateTime.format(pattern: EnumDateTimePatterns, zoneId: ZoneId? = null): String {
    val formatter = DateTimeFormatter.ofPattern(pattern.pattern)

    return if (zoneId != null) {
        this.atZoneSameInstant(zoneId).format(formatter)
    } else {
        this.format(formatter)
    }
}

fun timeNow(zoneId: ZoneId): LocalTime {
    return ZonedDateTime.now(zoneId).toLocalTime()
}

fun dateNow(zoneId: ZoneId): LocalDate {
    return ZonedDateTime.now(zoneId).toLocalDate()
}

fun dateTimeNow(zoneId: ZoneId): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.now(), zoneId)
}

fun yearMonthNow(): YearMonth {
    return YearMonth.now()
}

fun offsetDateTimeNow(zoneId: ZoneId): OffsetDateTime {
    return OffsetDateTime.now(zoneId)
}

fun LocalDateTime.toEpochMillis(): Long {
    val zoneId = ZoneId.systemDefault()
    val zoneOffset = zoneId.rules.getOffset(this)
    return this.toInstant(zoneOffset).toEpochMilli()
}

fun LocalDate.getOffsetDateTime(time: LocalTime): OffsetDateTime? {
    val zoneId = ZoneId.systemDefault()
    val offset = zoneId.rules.getOffset(this.atTime(time))

    return OffsetDateTime.of(this, time, offset)
}