package br.com.fitnesspro.charts.entries.bar

data class GroupedBarEntry(
    override val label: String,
    val values: List<Float>
) : BarChartEntry