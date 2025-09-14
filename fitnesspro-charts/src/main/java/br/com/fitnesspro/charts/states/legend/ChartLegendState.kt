package br.com.fitnesspro.charts.states.legend

import br.com.fitnesspro.charts.entries.legend.LegendEntry

data class ChartLegendState(
    val entries: List<LegendEntry>,
    val isEnabled: Boolean = true,
)