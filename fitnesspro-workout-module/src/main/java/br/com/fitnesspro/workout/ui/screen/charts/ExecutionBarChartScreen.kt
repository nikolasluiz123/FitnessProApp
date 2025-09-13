package br.com.fitnesspro.workout.ui.screen.charts

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.charts.composables.bar.BarChart
import br.com.fitnesspro.charts.entries.bar.SimpleBarEntry
import br.com.fitnesspro.charts.states.bar.BarChartState
import br.com.fitnesspro.charts.styles.ChartBackgroundStyle
import br.com.fitnesspro.charts.styles.bar.BarStyle
import br.com.fitnesspro.charts.styles.text.ChartTextStyle
import br.com.fitnesspro.charts.styles.text.enums.LongLabelStrategy

@Composable
fun ExecutionBarChartScreen() {
    val state = BarChartState(
        entries = listOf(
            SimpleBarEntry("Janeiro Muito Grande Mesmo", 30f),
            SimpleBarEntry("Fevereiro", 55f),
            SimpleBarEntry("Março", 80f),
            SimpleBarEntry("Abril", 20f)
        ),
        backgroundStyle = ChartBackgroundStyle(
            xAxisLabelStyle = ChartTextStyle(longLabelStrategy = LongLabelStrategy.Abbreviate),
        ),
        defaultBarStyle = BarStyle(
            fillColor = MaterialTheme.colorScheme.primary,
        )
    )

    BarChart(
        state = state,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(16.dp)
    )
}