package br.com.fitnesspro.charts.styles.text.extensions

import android.graphics.Typeface
import androidx.compose.ui.text.font.FontWeight

fun FontWeight.getTypeFace(): Typeface {
    val style = when (this) {
        FontWeight.Bold -> Typeface.BOLD
        else -> Typeface.NORMAL
    }

    return Typeface.defaultFromStyle(style)
}