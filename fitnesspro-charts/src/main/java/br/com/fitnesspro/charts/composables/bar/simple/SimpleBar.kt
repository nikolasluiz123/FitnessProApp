package br.com.fitnesspro.charts.composables.bar.simple

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.charts.composables.tooltip.ChartTooltip
import br.com.fitnesspro.charts.entries.bar.SimpleBarEntry
import br.com.fitnesspro.charts.styles.bar.BarStyle
import kotlinx.coroutines.delay

@Composable
fun SimpleBar(
    entry: SimpleBarEntry,
    style: BarStyle,
    maxValue: Float,
    modifier: Modifier = Modifier,
    index: Int = 0
) {
    val targetFraction = (entry.value / maxValue).coerceIn(0f, 1f)
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(index * style.animationDelay)
        startAnimation = true
    }

    val animatedFraction by animateFloatAsState(
        targetValue = if (startAnimation) targetFraction else 0f,
        animationSpec = tween(
            durationMillis = style.animationDuration,
            easing = FastOutSlowInEasing
        ),
        label = "Bar Growth"
    )

    val showTooltip = (animatedFraction == targetFraction) &&
            (style.tooltipStyle != null) &&
            targetFraction > 0f

    BoxWithConstraints(
        modifier = modifier.fillMaxHeight(),
        contentAlignment = Alignment.BottomCenter
    ) {
        val barHeight = this.maxHeight * animatedFraction

        Box(
            modifier = Modifier
                .height(barHeight)
                .fillMaxWidth()
                .background(
                    color = style.fillColor,
                    shape = style.shape
                )
                .then(
                    if (isDrawBorder(style)) {
                        Modifier.border(
                            width = style.borderWidth,
                            color = style.borderColor,
                            shape = style.shape
                        )
                    } else Modifier
                )
        )

        if (showTooltip) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = -barHeight)
            ) {
                ChartTooltip(
                    value = entry.value,
                    style = style.tooltipStyle
                )
            }
        }
    }
}

private fun isDrawBorder(style: BarStyle): Boolean {
    return style.borderWidth > 0.dp && style.borderColor != Color.Transparent
}