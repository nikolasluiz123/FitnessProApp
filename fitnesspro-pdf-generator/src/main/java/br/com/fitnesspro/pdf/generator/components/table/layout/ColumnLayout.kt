package br.com.fitnesspro.pdf.generator.components.table.layout

import android.graphics.Paint
import br.com.fitnesspro.pdf.generator.components.table.enums.VerticalAlign

data class ColumnLayout(
    val label: String,
    val widthPercent: Float,
    val horizontalAlignment: Paint.Align = Paint.Align.LEFT,
    val verticalAlignment: VerticalAlign = VerticalAlign.CENTER
)