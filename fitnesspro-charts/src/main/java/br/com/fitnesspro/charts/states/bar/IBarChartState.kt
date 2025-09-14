package br.com.fitnesspro.charts.states.bar

import br.com.fitnesspro.charts.entries.bar.BarChartEntry
import br.com.fitnesspro.charts.states.legend.ChartLegendState

interface IBarChartState {
    val entries: List<BarChartEntry>
    val legendState: ChartLegendState?
}