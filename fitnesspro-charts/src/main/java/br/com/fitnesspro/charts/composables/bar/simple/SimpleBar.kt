package br.com.fitnesspro.charts.composables.bar.simple

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
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

    BoxWithConstraints(modifier = modifier.fillMaxHeight()) {
        val canvasHeight = this.maxHeight

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val barHeight = size.height * animatedFraction
            val top = size.height - barHeight

            drawBar(
                style = style,
                topLeft = Offset(0f, top),
                size = Size(size.width, barHeight)
            )
        }

        if (showTooltip) {
            val barHeightDp = canvasHeight * animatedFraction
            ChartTooltip(
                value = entry.value,
                style = style.tooltipStyle,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = -barHeightDp)
            )
        }
    }
}

private fun DrawScope.drawBar(
    style: BarStyle,
    topLeft: Offset,
    size: Size
) {
    val outline = style.shape.createOutline(size, layoutDirection, this)

    val path = Path().apply {
        when (outline) {
            is Outline.Generic -> addPath(outline.path)
            is Outline.Rectangle -> addRect(outline.rect)
            is Outline.Rounded -> addRoundRect(outline.roundRect)
        }
        translate(topLeft)
    }

    val borderWidthPx = style.borderWidth.toPx()

    drawPath(
        path = path,
        color = style.fillColor
    )

    if (borderWidthPx > 0f && style.borderColor != Color.Transparent) {
        drawPath(
            path = path,
            color = style.borderColor,
            style = Stroke(width = borderWidthPx)
        )
    }
}