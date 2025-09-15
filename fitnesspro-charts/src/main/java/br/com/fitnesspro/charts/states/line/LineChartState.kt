package br.com.fitnesspro.charts.states.line

import br.com.fitnesspro.charts.entries.line.LineChartPointEntry
import br.com.fitnesspro.charts.states.legend.ChartLegendState

data class LineChartState(
    val entries: List<LineChartPointEntry> = emptyList(),
    val legendState: ChartLegendState? = null
)