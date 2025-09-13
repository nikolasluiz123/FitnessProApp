package br.com.fitnesspro.charts.composables.bar

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.charts.entries.bar.BarChartEntry
import br.com.fitnesspro.charts.styles.bar.BarStyle
import kotlinx.coroutines.delay

@Composable
fun ChartBar(
    entry: BarChartEntry,
    style: BarStyle,
    maxValue: Float,
    chartHeight: Dp,
    modifier: Modifier = Modifier,
    index: Int = 0 // <- novo parâmetro para delay
) {
    val targetFraction = (entry.value / maxValue).coerceIn(0f, 1f)
    var startAnimation by remember { mutableStateOf(false) }

    // Dispara animação com delay baseado no índice
    LaunchedEffect(Unit) {
        delay(index * 150L) // 150ms entre cada barra
        startAnimation = true
    }

    val animatedFraction by animateFloatAsState(
        targetValue = if (startAnimation) targetFraction else 0f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
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