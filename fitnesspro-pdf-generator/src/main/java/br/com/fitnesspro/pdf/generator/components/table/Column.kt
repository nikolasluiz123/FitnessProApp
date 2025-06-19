package br.com.fitnesspro.pdf.generator.components.table

import android.graphics.Paint

data class Column(
    val label: String,
    val widthPercent: Float,
    val alignment: Paint.Align = Paint.Align.LEFT
)