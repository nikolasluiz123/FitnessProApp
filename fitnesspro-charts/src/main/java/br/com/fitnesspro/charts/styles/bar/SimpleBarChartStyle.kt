package br.com.fitnesspro.charts.styles.bar

import br.com.fitnesspro.charts.styles.ChartContainerStyle
import br.com.fitnesspro.charts.styles.legend.ChartLegendStyle

data class SimpleBarChartStyle(
    val defaultBarStyle: BarStyle?,
    val backgroundStyle: ChartContainerStyle = ChartContainerStyle(),
    val barStyles: List<BarStyle> = emptyList(),
    val legendStyle: ChartLegendStyle = ChartLegendStyle()
)