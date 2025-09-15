package br.com.fitnesspro.charts.styles.line

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.charts.styles.IAnimatedStyle
import br.com.fitnesspro.charts.styles.line.enums.LineType

data class LineStyle(
    val color: Color,
    val width: Dp = 3.dp,
    val lineType: LineType = LineType.STRAIGHT,
    override val animationDuration: Int = 1000,
    override val animationDelay: Long = 200L
): IAnimatedStyle