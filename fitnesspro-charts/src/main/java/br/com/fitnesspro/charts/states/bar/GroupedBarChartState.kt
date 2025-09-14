package br.com.fitnesspro.charts.states.bar

import br.com.fitnesspro.charts.entries.bar.GroupedBarEntry
import br.com.fitnesspro.charts.states.legend.ChartLegendState

data class GroupedBarChartState(
    override val entries: List<GroupedBarEntry> = emptyList(),
    override val legendState: ChartLegendState? = null
) : IBarChartState