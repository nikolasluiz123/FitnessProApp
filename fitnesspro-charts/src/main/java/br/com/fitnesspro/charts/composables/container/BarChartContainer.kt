package br.com.fitnesspro.charts.composables.container

import android.graphics.Paint
import android.text.TextPaint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import br.com.fitnesspro.charts.states.bar.IBarChartState
import br.com.fitnesspro.charts.states.legend.ChartLegendState
import br.com.fitnesspro.charts.styles.ChartBackgroundStyle
import br.com.fitnesspro.charts.styles.legend.ChartLegendStyle
import br.com.fitnesspro.charts.styles.text.CanvasTextDrawer
import br.com.fitnesspro.charts.styles.text.enums.LongLabelStrategy
import br.com.fitnesspro.charts.styles.text.extensions.getTextPaint
import kotlin.math.sin

@Composable
fun BarChartContainer(
    modifier: Modifier = Modifier,
    state: IBarChartState,
    backgroundStyle: ChartBackgroundStyle,
    legendStyle: ChartLegendStyle,
    maxValue: Float,
    content: @Composable (chartHeight: Dp, totalChartWidth: Dp, actualSlotWidth: Dp) -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        val viewportWidth = this.maxWidth
        val scrollState = rememberScrollState()
        val density = LocalDensity.current

        val yAxisGutterWidthPx = preCalculateYAxisGutterPx(density, backgroundStyle, maxValue)
        val yAxisGutterWidthDp = with(density) { yAxisGutterWidthPx.toDp() }

        val chartViewportWidthDp = (viewportWidth - yAxisGutterWidthDp).coerceAtLeast(0.dp)

        val (totalChartWidthDp: Dp, actualSlotWidthDp: Dp, availableTextWidthPx: Float) = calculateHorizontalMetrics(
            style = backgroundStyle,
            entryCount = state.entries.size,
            viewportWidth = chartViewportWidthDp
        )

        val (footerTotalHeightPx, maxLabelHeightPx) = preCalculateFooterSizes(
            density = density,
            state = state,
            style = backgroundStyle,
            legendState = state.legendState,
            legendStyle = legendStyle,
            availableTextWidthPx = availableTextWidthPx,
            canvasWidth = with(density) { chartViewportWidthDp.toPx() }
        )

        val footerTotalHeightDp = with(density) { footerTotalHeightPx.toDp() }
        val actualBarAreaHeightDp = (this.maxHeight - footerTotalHeightDp).coerceAtLeast(0.dp)
        val isScrollNeeded = backgroundStyle.enableHorizontalScroll && (totalChartWidthDp > chartViewportWidthDp)

        val scrollModifier = if (isScrollNeeded) {
            Modifier.horizontalScroll(scrollState)
        } else {
            Modifier
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = yAxisGutterWidthDp)
                .then(scrollModifier)
        ) {
            Box(
                modifier = Modifier
                    .width(totalChartWidthDp)
                    .fillMaxHeight()
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val canvasWidth = size.width
                    val newZeroY = size.height - footerTotalHeightPx

                    drawBaseLine(backgroundStyle, newZeroY)
                    drawYAxisGridLines(
                        style = backgroundStyle,
                        maxValue = maxValue,
                        zeroY = newZeroY,
                        chartHeight = actualBarAreaHeightDp
                    )

                    if (backgroundStyle.showXAxisLabels) {
                        val xAxisLabelPaint = backgroundStyle.xAxisLabelStyle.getTextPaint(drawContext.density)
                        val barSlotWidthPx = if (backgroundStyle.enableHorizontalScroll) {
                            actualSlotWidthDp.toPx()
                        } else {
                            if (state.entries.isNotEmpty()) canvasWidth / (state.entries.size * 2) else 0f
                        }

                        state.entries.forEachIndexed { index, entry ->
                            val textDrawer = CanvasTextDrawer(
                                strategy = backgroundStyle.xAxisLabelStyle.longLabelStrategy,
                                textPaint = xAxisLabelPaint,
                                drawContext = drawContext
                            )

                            val xCenter = if (backgroundStyle.enableHorizontalScroll) {
                                (index + 0.5f) * barSlotWidthPx
                            } else {
                                (index * 2 + 1) * barSlotWidthPx
                            }

                            textDrawer.draw(
                                text = entry.label,
                                xCenter = xCenter,
                                topY = newZeroY + backgroundStyle.xAxisLabelStyle.padding.toPx(),
                                availableWidth = availableTextWidthPx
                            )
                        }
                    }

                    val chartViewportWidthPx = with(density) { chartViewportWidthDp.toPx() }
                    val scrollOffsetPx = if (isScrollNeeded) scrollState.value.toFloat() else 0f
                    val effectiveWidth = if (isScrollNeeded) chartViewportWidthPx else canvasWidth

                    state.legendState?.let { legendState ->
                        drawLegend(
                            legendState = legendState,
                            legendStyle = legendStyle,
                            canvasWidth = effectiveWidth,
                            baseLineY = newZeroY,
                            topPadding = backgroundStyle.xAxisLabelStyle.padding,
                            xAxisMaxHeight = maxLabelHeightPx,
                            scrollOffset = scrollOffsetPx
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(actualBarAreaHeightDp)
                        .align(Alignment.TopStart)
                ) {
                    content(actualBarAreaHeightDp, totalChartWidthDp, actualSlotWidthDp)
                }
            }
        }

        Canvas(modifier = Modifier
            .width(yAxisGutterWidthDp)
            .fillMaxHeight()
            .align(Alignment.TopStart)
        ) {
            val newZeroY = size.height - footerTotalHeightPx

            drawYAxisLabels(
                style = backgroundStyle,
                maxValue = maxValue,
                zeroY = newZeroY,
                chartHeight = actualBarAreaHeightDp,
                scrollOffset = 0f,
                gutterWidthPx = yAxisGutterWidthPx
            )
        }
    }
}

private fun preCalculateYAxisGutterPx(
    density: Density,
    style: ChartBackgroundStyle,
    maxValue: Float
): Float {
    if (!style.showYAxisLabels) return 0f

    val yAxisLabelPaint = style.yAxisLabelStyle.getTextPaint(density).apply {
        textAlign = Paint.Align.RIGHT
    }

    val widestLabel = maxValue.toInt().toString()
    val labelWidthPx = yAxisLabelPaint.measureText(widestLabel)
    val labelPaddingPx = with(density) { style.yAxisLabelStyle.padding.toPx() }

    return labelWidthPx + (labelPaddingPx * 2)
}

@Composable
private fun calculateHorizontalMetrics(style: ChartBackgroundStyle, entryCount: Int, viewportWidth: Dp): Triple<Dp, Dp, Float> {
    val (totalChartWidthDp: Dp, actualSlotWidthDp: Dp) = if (style.enableHorizontalScroll && entryCount > 0) {
        val baseSlotWidth = style.scrollableBarWidth
        val minTotalCalculatedWidth = entryCount * baseSlotWidth
        val isScrollActuallyNeeded = minTotalCalculatedWidth > viewportWidth

        if (isScrollActuallyNeeded) {
            Pair(minTotalCalculatedWidth, baseSlotWidth)
        } else {
            val stretchedSlotWidth = if (entryCount > 0) viewportWidth / entryCount else viewportWidth
            Pair(viewportWidth, stretchedSlotWidth)
        }
    } else {
        val canvasSlotWidthForCalc = if (entryCount > 0) (viewportWidth / entryCount) else viewportWidth
        Pair(viewportWidth, canvasSlotWidthForCalc)
    }

    val availableTextWidthPx = with(LocalDensity.current) {
        if (style.enableHorizontalScroll) {
            actualSlotWidthDp.toPx()
        } else {
            if (entryCount > 0) (viewportWidth / (entryCount * 2)).toPx() else 0f
        }
    }

    return Triple(totalChartWidthDp, actualSlotWidthDp, availableTextWidthPx)
}

private fun preCalculateFooterSizes(
    density: Density,
    state: IBarChartState,
    style: ChartBackgroundStyle,
    legendState: ChartLegendState?,
    legendStyle: ChartLegendStyle,
    availableTextWidthPx: Float,
    canvasWidth: Float
): Pair<Float, Float> {
    val labelPaddingPx = with(density) { style.xAxisLabelStyle.padding.toPx() }
    val maxLabelHeightPx = if (style.showXAxisLabels && state.entries.isNotEmpty()) {
        val xAxisLabelPaint = style.xAxisLabelStyle.getTextPaint(density)
        state.entries.maxOfOrNull { entry ->
            measureTextHeight(
                strategy = style.xAxisLabelStyle.longLabelStrategy,
                textPaint = xAxisLabelPaint,
                text = entry.label,
                availableWidth = availableTextWidthPx
            )
        } ?: 0f
    } else {
        0f
    }

    val legendPaddingTopPx = with(density) { 8.dp.toPx() }
    val legendHeightPx = if (legendState != null && legendState.isEnabled && legendState.entries.isNotEmpty()) {
        val legendTextPaint = legendStyle.textStyle.getTextPaint(density)
        measureLegendHeight(
            legendState = legendState,
            legendTextPaint = legendTextPaint,
            canvasWidth = canvasWidth,
            density = density
        )
    } else {
        0f
    }

    var totalHeightPx = 0f

    if (maxLabelHeightPx > 0f) {
        totalHeightPx += labelPaddingPx + maxLabelHeightPx
    }

    if (legendHeightPx > 0f) {
        totalHeightPx += legendPaddingTopPx + legendHeightPx
    }

    return Pair(totalHeightPx, maxLabelHeightPx)
}

private fun measureTextHeight(
    strategy: LongLabelStrategy,
    textPaint: TextPaint,
    text: String,
    availableWidth: Float
): Float {
    return when (strategy) {
        LongLabelStrategy.MultiLine -> {
            val lines = getLinesForMeasurement(availableWidth, text, textPaint)
            lines.size * textPaint.fontSpacing
        }
        LongLabelStrategy.Diagonal -> {
            val textWidth = textPaint.measureText(text)
            (textWidth * sin(Math.toRadians(45.0))).toFloat() + textPaint.fontSpacing
        }
        LongLabelStrategy.Abbreviate -> {
            textPaint.fontSpacing
        }
    }
}

private fun getLinesForMeasurement(availableWidth: Float, text: String, textPaint: TextPaint): List<String> {
    val availableWidthPx = availableWidth.toInt().coerceAtLeast(1)
    val words = text.trim().split(Regex("\\s+"))
    val lines = mutableListOf<String>()
    var currentLine = ""

    for (word in words) {
        val candidate = if (currentLine.isEmpty()) word else "$currentLine $word"
        if (textPaint.measureText(candidate) <= availableWidthPx) {
            currentLine = candidate
        } else {
            if (currentLine.isNotEmpty()) lines.add(currentLine)
            currentLine = word
        }
    }
    if (currentLine.isNotEmpty()) lines.add(currentLine)
    return lines
}

private fun measureLegendHeight(
    legendState: ChartLegendState,
    legendTextPaint: TextPaint,
    canvasWidth: Float,
    density: Density
): Float {
    val circleRadius = with(density) { 4.dp.toPx() }
    val circleTextPadding = with(density) { 8.dp.toPx() }
    val itemHorizontalSpacing = with(density) { 16.dp.toPx() }
    val itemVerticalSpacing = with(density) { 8.dp.toPx() }
    val itemLineHeight = legendTextPaint.fontSpacing + itemVerticalSpacing

    var currentX = 0f + circleRadius
    var lineCount = 1

    legendState.entries.forEach { entry ->
        val textWidth = legendTextPaint.measureText(entry.label)
        val itemWidth = (circleRadius * 2) + circleTextPadding + textWidth + itemHorizontalSpacing

        if (currentX + itemWidth > canvasWidth) {
            lineCount++
            currentX = 0f + circleRadius + itemWidth
        } else {
            currentX += itemWidth
        }
    }

    return (lineCount * itemLineHeight) - itemVerticalSpacing
}


private fun DrawScope.drawLegend(
    legendState: ChartLegendState,
    legendStyle: ChartLegendStyle,
    canvasWidth: Float,
    baseLineY: Float,
    topPadding: Dp,
    xAxisMaxHeight: Float,
    scrollOffset: Float
) {
    if (!legendState.isEnabled || legendState.entries.isEmpty()) return

    val legendTextPaint = legendStyle.textStyle.getTextPaint(drawContext.density).apply {
        textAlign = Paint.Align.LEFT
    }
    val legendPaddingTop = 8.dp.toPx()

    val circleRadius = 4.dp.toPx()
    val circleTextPadding = 8.dp.toPx()
    val itemHorizontalSpacing = 16.dp.toPx()
    val itemVerticalSpacing = 8.dp.toPx()
    val itemLineHeight = legendTextPaint.fontSpacing + itemVerticalSpacing

    val startY = baseLineY + topPadding.toPx() + xAxisMaxHeight + legendPaddingTop
    val firstLineBaseY = startY - legendTextPaint.fontMetrics.ascent

    var currentX = scrollOffset + circleRadius
    var currentY = firstLineBaseY

    legendState.entries.forEachIndexed { index, entry ->
        val textWidth = legendTextPaint.measureText(entry.label)
        val itemWidth = (circleRadius * 2) + circleTextPadding + textWidth + itemHorizontalSpacing

        if (currentX + itemWidth > scrollOffset + canvasWidth) {
            currentX = scrollOffset + circleRadius
            currentY += itemLineHeight
        }

        val circleCenterY = currentY + (legendTextPaint.fontMetrics.ascent / 2f) + (legendTextPaint.fontMetrics.descent / 2f)
        drawCircle(
            color = legendStyle.colors.getOrElse(index) { Color.Black },
            radius = circleRadius,
            center = Offset(currentX, circleCenterY)
        )

        val textX = currentX + circleRadius + circleTextPadding
        drawContext.canvas.nativeCanvas.drawText(
            entry.label,
            textX,
            currentY,
            legendTextPaint
        )

        currentX += itemWidth
    }
}


private fun DrawScope.drawYAxisGridLines(
    style: ChartBackgroundStyle,
    maxValue: Float,
    zeroY: Float,
    chartHeight: Dp
) {
    if (style.showYAxisLines) {
        val step = maxValue / style.yAxisSteps
        val chartHeightPx = chartHeight.toPx()

        for (i in 0..style.yAxisSteps) {
            val y = zeroY - (i * step / maxValue) * chartHeightPx
            drawYAxisLine(style, y)
        }
    }
}

private fun DrawScope.drawYAxisLabels(
    style: ChartBackgroundStyle,
    maxValue: Float,
    zeroY: Float,
    chartHeight: Dp,
    scrollOffset: Float,
    gutterWidthPx: Float
) {
    if (style.showYAxisLabels) {
        val step = maxValue / style.yAxisSteps
        val yAxisLabelPaint = style.yAxisLabelStyle.getTextPaint(drawContext.density).apply {
            textAlign = Paint.Align.RIGHT
        }
        val chartHeightPx = chartHeight.toPx()
        val labelPaddingPx = style.yAxisLabelStyle.padding.toPx()
        val labelXPos = gutterWidthPx - labelPaddingPx

        for (i in 0..style.yAxisSteps) {
            val y = zeroY - (i * step / maxValue) * chartHeightPx
            drawYAxisLabel(
                style = style,
                i = i,
                step = step,
                y = y,
                yAxisLabelPaint = yAxisLabelPaint,
                scrollOffset = scrollOffset,
                labelXPos = labelXPos
            )
        }
    }
}

private fun DrawScope.drawBaseLine(
    style: ChartBackgroundStyle,
    zeroY: Float
) {
    drawLine(
        color = style.gridLineColor,
        start = Offset(0f, zeroY),
        end = Offset(size.width, zeroY),
        strokeWidth = style.gridLineWidth.toPx()
    )
}

private fun DrawScope.drawYAxisLabel(
    style: ChartBackgroundStyle,
    i: Int,
    step: Float,
    y: Float,
    yAxisLabelPaint: TextPaint,
    scrollOffset: Float,
    labelXPos: Float
) {
    if (style.showYAxisLabels) {
        drawContext.canvas.nativeCanvas.drawText(
            (i * step).toInt().toString(),
            scrollOffset + labelXPos,
            y - style.yAxisLabelStyle.padding.toPx(),
            yAxisLabelPaint
        )
    }
}

private fun DrawScope.drawYAxisLine(
    style: ChartBackgroundStyle,
    y: Float
) {
    if (style.showYAxisLines) {
        drawLine(
            color = style.gridLineColor,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = style.gridLineWidth.toPx()
        )
    }
}