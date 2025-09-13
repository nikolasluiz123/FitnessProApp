package br.com.fitnesspro.charts.styles.legend

import br.com.fitnesspro.charts.entries.legend.LegendEntry
import br.com.fitnesspro.charts.styles.text.ChartTextStyle

data class ChartLegend(
    val entries: List<LegendEntry>,
    val isEnabled: Boolean = true,
    val textStyle: ChartTextStyle = ChartTextStyle()
)