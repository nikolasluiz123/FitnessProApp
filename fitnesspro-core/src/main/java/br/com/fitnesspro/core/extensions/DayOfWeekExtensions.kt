package br.com.fitnesspro.core.extensions

import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

fun DayOfWeek.getFirstPartFullDisplayName(): String {
    val displayName = getDisplayName(TextStyle.FULL, Locale.getDefault())
    val firstPart = displayName.split("-")[0]

    return firstPart.replaceFirstChar(Char::uppercase)
}

fun DayOfWeek.getShortDisplayNameAllCaps(): String {
    return getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase().replace(".", "")
}