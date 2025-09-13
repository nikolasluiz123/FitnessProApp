package br.com.fitnesspro.charts.styles

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.charts.styles.text.ChartTextStyle

data class ChartBackgroundStyle(
    val showXAxisLabels: Boolean = true,
    val xAxisLabelStyle: ChartTextStyle = ChartTextStyle(),

    val showYAxisLabels: Boolean = true,
    val showYAxisLines: Boolean = true,
    val yAxisSteps: Int = 5,
    val yAxisLabelStyle: ChartTextStyle = ChartTextStyle(),

    val gridLineColor: Color = Color.LightGray,
    val gridLineWidth: Dp = 1.dp,

    val enableHorizontalScroll: Boolean = false,
    val scrollableBarWidth: Dp = 96.dp
)