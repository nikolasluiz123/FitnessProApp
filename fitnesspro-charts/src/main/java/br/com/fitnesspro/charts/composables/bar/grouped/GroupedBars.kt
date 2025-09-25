package br.com.fitnesspro.charts.composables.bar.grouped

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.charts.composables.bar.simple.SimpleBar
import br.com.fitnesspro.charts.entries.bar.GroupedBarEntry
import br.com.fitnesspro.charts.entries.bar.SimpleBarEntry
import br.com.fitnesspro.charts.styles.bar.BarStyle

@Composable
fun GroupedBars(
    entry: GroupedBarEntry,
    styles: List<BarStyle>,
    maxValue: Float,
    groupIndex: Int,
    barWidthFraction: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxHeight(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        entry.values.forEachIndexed { innerIndex, value ->
            val style = styles[innerIndex % styles.size]
            val singleEntry = SimpleBarEntry(label = "", value = value)

            SimpleBar(
                entry = singleEntry,
                style = style,
                maxValue = maxValue,
                index = groupIndex,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(barWidthFraction)
            )
        }
    }
}