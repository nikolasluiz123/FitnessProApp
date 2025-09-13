package br.com.fitnesspro.charts.styles.text.extensions

import android.graphics.Paint
import android.text.TextPaint
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import br.com.fitnesspro.charts.styles.text.ChartTextStyle
import br.com.fitnesspro.charts.styles.text.enums.LongLabelStrategy

fun ChartTextStyle.getTextPaint(density: Density): TextPaint {
    val paint = TextPaint()

    with(density) {
        paint.color = color.toArgb()
        paint.textSize = fontSize.toPx()
        paint.typeface = fontWeight.getTypeFace()

        when (longLabelStrategy) {
            LongLabelStrategy.MultiLine -> {
                paint.isAntiAlias = true
                paint.textAlign = Paint.Align.LEFT
            }

            LongLabelStrategy.Diagonal, LongLabelStrategy.Abbreviate -> {
                paint.textAlign = textAlign
            }
        }
    }

    return paint
}