package br.com.fitnesspro.charts.composables.bar.grouped

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import br.com.fitnesspro.charts.composables.tooltip.ChartTooltip
import br.com.fitnesspro.charts.entries.bar.GroupedBarEntry
import br.com.fitnesspro.charts.styles.bar.BarStyle
import kotlinx.coroutines.delay

private const val BAR_SPACING_FRACTION = 0.1f

@Composable
fun GroupedBars(
    entry: GroupedBarEntry,
    styles: List<BarStyle>,
    maxValue: Float,
    groupIndex: Int,
    modifier: Modifier = Modifier,
    barWidthFraction: Float
) {
    if (entry.values.isEmpty()) return

    var startAnimation by remember { mutableStateOf(false) }

    val targetFractions = remember(entry.values, maxValue) {
        entry.values.map { (it / maxValue).coerceIn(0f, 1f) }
    }

    val animatedFractions = entry.values.mapIndexed { index, _ ->
        val style = styles.getOrElse(index) { styles.first() }
        val animation by animateFloatAsState(
            targetValue = if (startAnimation) targetFractions[index] else 0f,
            animationSpec = tween(
                durationMillis = style.animationDuration,
                delayMillis = (groupIndex * style.animationDelay).toInt(),
                easing = FastOutSlowInEasing
            ),
            label = "BarAnimation_${groupIndex}_$index"
        )
        animation
    }

    LaunchedEffect(Unit) {
        delay(groupIndex * styles.first().animationDelay)
        startAnimation = true
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val canvasWidth = this.maxWidth
        val canvasHeight = this.maxHeight

        Canvas(modifier = Modifier.fillMaxSize()) {
            val groupWidth = size.width * barWidthFraction
            val totalSpacing = if (entry.values.size > 1) groupWidth * BAR_SPACING_FRACTION else 0f
            val individualBarWidth = (groupWidth - totalSpacing) / entry.values.size.toFloat()
            val spacing = if (entry.values.size > 1) totalSpacing / (entry.values.size - 1).toFloat() else 0f
            val startX = (size.width - groupWidth) / 2f

            entry.values.forEachIndexed { index, _ ->
                val barHeight = size.height * animatedFractions[index]
                val left = startX + index * (individualBarWidth + spacing)
                val top = size.height - barHeight
                drawBar(
                    style = styles.getOrElse(index) { styles.first() },
                    topLeft = Offset(left, top),
                    size = Size(individualBarWidth, barHeight)
                )
            }
        }

        val groupWidthDp = canvasWidth * barWidthFraction
        val totalSpacingDp = if (entry.values.size > 1) groupWidthDp * BAR_SPACING_FRACTION else 0.dp
        val individualBarWidthDp = (groupWidthDp - totalSpacingDp) / entry.values.size
        val spacingDp = if (entry.values.size > 1) totalSpacingDp / (entry.values.size - 1) else 0.dp
        val startXDp = (canvasWidth - groupWidthDp) / 2f

        entry.values.forEachIndexed { index, value ->
            val style = styles.getOrElse(index) { styles.first() }
            val animatedFraction = animatedFractions[index]
            val targetFraction = targetFractions[index]

            val showTooltip = (animatedFraction == targetFraction) &&
                    (style.tooltipStyle != null) &&
                    targetFraction > 0f

            if (showTooltip) {
                val barHeightDp = canvasHeight * animatedFraction
                val barStart = startXDp + index * (individualBarWidthDp + spacingDp)

                Box(
                    modifier = Modifier
                        .padding(start = barStart)
                        .width(individualBarWidthDp)
                        .fillMaxHeight()
                ) {
                    ChartTooltip(
                        value = value,
                        style = style.tooltipStyle,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = -barHeightDp)
                    )
                }
            }
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

    if (borderWidthPx > 0f) {
        drawPath(
            path = path,
            color = style.borderColor,
            style = Stroke(width = borderWidthPx)
        )
    }
}