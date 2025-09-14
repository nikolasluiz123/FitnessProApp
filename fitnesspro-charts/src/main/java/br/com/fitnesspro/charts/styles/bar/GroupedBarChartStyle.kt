package br.com.fitnesspro.charts.styles.bar

import br.com.fitnesspro.charts.styles.ChartBackgroundStyle
import br.com.fitnesspro.charts.styles.legend.ChartLegendStyle

data class GroupedBarChartStyle(
    val defaultBarStyles: List<BarStyle>,
    val backgroundStyle: ChartBackgroundStyle = ChartBackgroundStyle(),
    val legendStyle: ChartLegendStyle = ChartLegendStyle()
)