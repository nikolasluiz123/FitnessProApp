package br.com.fitnesspro.pdf.generator.utils

import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.text.TextPaint


object Paints {
    val titlePaint by lazy {
        TextPaint().apply {
            color = Color.rgb(33, 150, 243)
            textSize = 16f
        }
    }

    val subtitlePaint by lazy {
        TextPaint().apply {
            color = Color.rgb(33, 150, 243)
            textSize = 14f
        }
    }

    val titleLinePaint by lazy {
        Paint().apply {
            color = Color.GRAY
            strokeWidth = 1f
        }
    }

    val dashedLinePaint by lazy {
        Paint().apply {
            color = Color.LTGRAY
            strokeWidth = 1f
            pathEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
        }
    }

    val defaultValuePaint by lazy {
        TextPaint().apply {
            color = Color.BLACK
            textSize = 12f
        }
    }

    val defaultLabelPaint by lazy {
        TextPaint().apply {
            color = Color.GRAY
            textSize = 12f
        }
    }
}