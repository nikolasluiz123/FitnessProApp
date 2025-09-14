package br.com.fitnesspro.charts.styles.bar

import br.com.fitnesspro.charts.styles.ChartBackgroundStyle
import br.com.fitnesspro.charts.styles.legend.ChartLegendStyle

data class SimpleBarChartStyle(
    val defaultBarStyle: BarStyle?,
    val backgroundStyle: ChartBackgroundStyle = ChartBackgroundStyle(),
    val barStyles: List<BarStyle> = emptyList(),
    val legendStyle: ChartLegendStyle = ChartLegendStyle()
)