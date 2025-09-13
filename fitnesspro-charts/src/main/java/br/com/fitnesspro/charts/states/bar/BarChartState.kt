package br.com.fitnesspro.charts.states.bar

import br.com.fitnesspro.charts.entries.bar.BarChartEntry
import br.com.fitnesspro.charts.styles.ChartBackgroundStyle
import br.com.fitnesspro.charts.styles.bar.BarStyle

data class BarChartState(
    val entries: List<BarChartEntry>,
    val backgroundStyle: ChartBackgroundStyle = ChartBackgroundStyle(),
    val barStyles: List<BarStyle> = emptyList(), // se vazio → aplica estilo default
    val defaultBarStyle: BarStyle? = null
)