package br.com.fitnesspro.charts.states.bar

import br.com.fitnesspro.charts.entries.bar.GroupedBarEntry
import br.com.fitnesspro.charts.styles.ChartBackgroundStyle
import br.com.fitnesspro.charts.styles.bar.BarStyle
import br.com.fitnesspro.charts.styles.legend.ChartLegend

data class GroupedBarChartState(
    override val entries: List<GroupedBarEntry>,
    val defaultBarStyles: List<BarStyle>,
    override val backgroundStyle: ChartBackgroundStyle = ChartBackgroundStyle(),
    override val legend: ChartLegend? = null
) : IBarChartState