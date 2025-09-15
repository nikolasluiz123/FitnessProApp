package br.com.fitnesspro.charts.styles.line

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.charts.styles.IAnimatedStyle
import br.com.fitnesspro.charts.styles.line.enums.LineType
import br.com.fitnesspro.charts.styles.tooltip.ChartTooltipStyle

data class LineStyle(
    val color: Color,
    val width: Dp = 3.dp,
    val lineType: LineType = LineType.CURVED,
    val showDataPoints: Boolean = true,
    val dataPointRadius: Dp = 4.dp,
    val tooltipStyle: ChartTooltipStyle? = null,
    override val animationDuration: Int = 1000,
    override val animationDelay: Long = 200L
): IAnimatedStyle