package br.com.fitnesspro.charts.entries.bar


data class SimpleBarEntry(
    override val label: String,
    override val value: Float,
) : BarChartEntry