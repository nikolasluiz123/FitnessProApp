package br.com.fitnesspro.charts.states.bar

import br.com.fitnesspro.charts.entries.bar.BarChartEntry
import br.com.fitnesspro.charts.styles.ChartBackgroundStyle
import br.com.fitnesspro.charts.styles.legend.ChartLegend

interface IBarChartState {
    val entries: List<BarChartEntry>
    val backgroundStyle: ChartBackgroundStyle
    val legend: ChartLegend?
}