package br.com.fitnesspro.charts.styles.line

import br.com.fitnesspro.charts.styles.ChartContainerStyle
import br.com.fitnesspro.charts.styles.legend.ChartLegendStyle

data class LineChartStyle(
    val lineStyles: List<LineStyle>,
    val backgroundStyle: ChartContainerStyle = ChartContainerStyle(),
    val legendStyle: ChartLegendStyle = ChartLegendStyle()
)