package br.com.fitnesspro.charts.styles.legend

import androidx.compose.ui.graphics.Color
import br.com.fitnesspro.charts.styles.text.ChartTextStyle

data class ChartLegendStyle(
    val textStyle: ChartTextStyle = ChartTextStyle(),
    val colors: List<Color> = emptyList(),
)