package br.com.fitnesspro.charts.styles.bar

import br.com.fitnesspro.charts.styles.ChartContainerStyle
import br.com.fitnesspro.charts.styles.legend.ChartLegendStyle

data class GroupedBarChartStyle(
    val defaultBarStyles: List<BarStyle>,
    val backgroundStyle: ChartContainerStyle = ChartContainerStyle(),
    val legendStyle: ChartLegendStyle = ChartLegendStyle()
)