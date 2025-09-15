package br.com.fitnesspro.charts.composables.line

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import br.com.fitnesspro.charts.styles.line.LineStyle
import br.com.fitnesspro.charts.styles.line.enums.LineType
import kotlinx.coroutines.delay

private const val CURVE_SMOOTHNESS = 0.2f

@Composable
internal fun Line(
    points: List<Offset>,
    style: LineStyle,
    index: Int
) {
    if (points.isEmpty()) return

    val pathMeasure = remember { PathMeasure() }
    var startAnimation by remember { mutableStateOf(false) }
    val density = LocalDensity.current

    val path = remember(points, style.lineType) {
        when (style.lineType) {
            LineType.STRAIGHT -> createStraightPath(points)
            LineType.CURVED -> createCurvedPath(points)
        }
    }

    pathMeasure.setPath(path, false)
    val pathLength = pathMeasure.length

    val animatedProgress by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = style.animationDuration,
            easing = FastOutSlowInEasing
        ),
        label = "LineDrawAnimation"
    )

    LaunchedEffect(Unit) {
        delay(index * style.animationDelay)
        startAnimation = true
    }

    val phase = pathLength * (1f - animatedProgress)

    val lineStroke = Stroke(
        width = with(density) { style.width.toPx() },
        cap = StrokeCap.Round,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(pathLength, pathLength), phase)
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawPath(
            path = path,
            color = style.color,
            style = lineStroke
        )
    }
}

private fun createStraightPath(points: List<Offset>): Path {
    val path = Path()
    points.firstOrNull()?.let { first ->
        path.moveTo(first.x, first.y)
        points.drop(1).forEach { point ->
            path.lineTo(point.x, point.y)
        }
    }
    return path
}

private fun createCurvedPath(points: List<Offset>): Path {
    val path = Path()
    if (points.isEmpty()) return path

    path.moveTo(points.first().x, points.first().y)

    for (i in 0 until points.size - 1) {
        val p0 = points.getOrElse(i - 1) { points[i] }
        val p1 = points[i]
        val p2 = points[i + 1]
        val p3 = points.getOrElse(i + 2) { p2 }

        // Pontos de controle para a curva de Bezier Cúbica
        val cp1x = p1.x + (p2.x - p0.x) * CURVE_SMOOTHNESS
        val cp1y = p1.y + (p2.y - p0.y) * CURVE_SMOOTHNESS
        val cp2x = p2.x - (p3.x - p1.x) * CURVE_SMOOTHNESS
        val cp2y = p2.y - (p3.y - p1.y) * CURVE_SMOOTHNESS

        path.cubicTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y)
    }

    return path
}