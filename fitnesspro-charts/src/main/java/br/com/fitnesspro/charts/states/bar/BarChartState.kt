package br.com.fitnesspro.charts.states.bar

import br.com.fitnesspro.charts.entries.bar.SimpleBarEntry
import br.com.fitnesspro.charts.styles.ChartBackgroundStyle
import br.com.fitnesspro.charts.styles.bar.BarStyle
import br.com.fitnesspro.charts.styles.legend.ChartLegend

data class BarChartState(
    override val entries: List<SimpleBarEntry>,
    override val backgroundStyle: ChartBackgroundStyle = ChartBackgroundStyle(),
    val defaultBarStyle: BarStyle?,
    val barStyles: List<BarStyle> = emptyList(),
    override val legend: ChartLegend? = null
) : IBarChartState