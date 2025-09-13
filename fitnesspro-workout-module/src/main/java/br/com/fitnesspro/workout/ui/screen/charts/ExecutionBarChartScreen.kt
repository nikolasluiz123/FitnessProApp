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
import br.com.fitnesspro.charts.styles.bar.BarStyle

@Composable
fun ExecutionBarChartScreen() {
    val state = BarChartState(
        entries = listOf(
            SimpleBarEntry("Jan", 30f),
            SimpleBarEntry("Feb", 55f),
            SimpleBarEntry("Mar", 80f),
            SimpleBarEntry("Apr", 20f)
        ),
        defaultBarStyle = BarStyle(
            fillColor = MaterialTheme.colorScheme.primaryContainer,
            borderColor = MaterialTheme.colorScheme.primary,
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