package br.com.fitnesspro.pdf.generator.components.table

import android.graphics.Paint

data class Column(
    val label: String,
    val widthPercent: Float,
    val horizontalAlignment: Paint.Align = Paint.Align.LEFT,
    val verticalAlignment: VerticalAlign = VerticalAlign.CENTER
)