package br.com.fitnesspro.core.extensions

import android.content.Context
import br.com.fitnesspro.core.R
import java.time.temporal.ChronoUnit

fun ChronoUnit.getLabelFromChronoUnit(context: Context): String {
    return when (this) {
        ChronoUnit.SECONDS -> context.getString(R.string.chrono_unit_seconds)
        ChronoUnit.MINUTES -> context.getString(R.string.chrono_unit_minutes)
        ChronoUnit.HOURS -> context.getString(R.string.chrono_unit_hours)
        else -> throw IllegalArgumentException(context.getString(R.string.invalid_label_chrono_unit_message))
    }
}

fun ChronoUnit?.getStringFromConvertedChronoUnitValue(value: Long?): String {
    return this?.let { value?.millisTo(it) }.toStringOrEmpty()
}
