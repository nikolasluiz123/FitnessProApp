package br.com.fitnesspro.charts.composables.bar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.charts.entries.bar.BarChartEntry
import br.com.fitnesspro.charts.styles.bar.BarStyle

@Composable
fun ChartBar(
    entry: BarChartEntry,
    style: BarStyle,
    maxValue: Float,
    chartHeight: Dp,
    modifier: Modifier = Modifier
) {
    val fraction = (entry.value / maxValue).coerceIn(0f, 1f)

    val animatedFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(durationMillis = 800),
        label = "Bar Growth"
    )

    Box(
        modifier = modifier
            .height(chartHeight * animatedFraction)
            .background(
                color = style.fillColor,
                shape = style.shape
            )
            .then(
                if (style.borderWidth > 0.dp && style.borderColor != Color.Transparent) {
                    Modifier.border(
                        width = style.borderWidth,
                        color = style.borderColor,
                        shape = style.shape
                    )
                } else Modifier
            )
    )
}