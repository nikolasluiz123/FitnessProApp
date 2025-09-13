package br.com.fitnesspro.charts.states.bar

import br.com.fitnesspro.charts.entries.bar.GroupedBarEntry
import br.com.fitnesspro.charts.styles.ChartBackgroundStyle
import br.com.fitnesspro.charts.styles.bar.BarStyle

data class GroupedBarChartState(
    override val entries: List<GroupedBarEntry>,
    override val backgroundStyle: ChartBackgroundStyle = ChartBackgroundStyle(),
    val defaultBarStyles: List<BarStyle>
) : IBarChartState