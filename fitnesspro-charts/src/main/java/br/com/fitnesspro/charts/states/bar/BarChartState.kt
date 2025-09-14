package br.com.fitnesspro.charts.states.bar

import br.com.fitnesspro.charts.entries.bar.SimpleBarEntry
import br.com.fitnesspro.charts.states.legend.ChartLegendState

data class BarChartState(
    override val entries: List<SimpleBarEntry> = emptyList(),
    override val legendState: ChartLegendState? = null
) : IBarChartState