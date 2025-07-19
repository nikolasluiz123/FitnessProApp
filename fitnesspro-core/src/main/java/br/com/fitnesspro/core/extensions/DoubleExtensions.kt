package br.com.fitnesspro.core.extensions

import java.text.DecimalFormat

fun Double?.formatToDecimal(): String {
    return try {
        DecimalFormat.getInstance().format(this)
    } catch (_: Exception) {
        return ""
    }
}

fun String.toDoubleValue(): Double? {
    return try {
        DecimalFormat.getInstance().parse(this) as Double
    } catch (_: Exception) {
        return null
    }
}