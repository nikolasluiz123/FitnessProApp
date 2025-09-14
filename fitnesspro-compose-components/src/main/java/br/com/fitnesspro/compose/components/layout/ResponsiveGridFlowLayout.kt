package br.com.fitnesspro.compose.components.layout

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints

@Composable
fun ResponsiveGridFlowLayout(
    modifier: Modifier = Modifier,
    maxColumns: Int,
    content: @Composable () -> Unit
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        val placeables = subcompose(Unit, content).map { it.measure(Constraints()) }
        val maxChildWidth = placeables.maxOfOrNull { it.width } ?: 0
        val totalWidth = constraints.maxWidth

        if (maxChildWidth == 0 || totalWidth == 0) {
            return@SubcomposeLayout layout(0, 0) {}
        }

        val dynamicCols = (totalWidth.toFloat() / maxChildWidth.toFloat()).toInt()
        val columns = dynamicCols.coerceAtMost(maxColumns).coerceAtLeast(1)

        val itemWidth = totalWidth / columns
        val itemConstraints = constraints.copy(minWidth = itemWidth, maxWidth = itemWidth)

        val finalPlaceables = subcompose("contentWithWidth", content).map {
            it.measure(itemConstraints)
        }

        var x = 0
        var y = 0
        var maxHeightInRow = 0
        val totalHeight = (finalPlaceables.chunked(columns).sumOf { row ->
            row.maxOfOrNull { it.height } ?: 0
        })

        layout(totalWidth, totalHeight) {
            finalPlaceables.forEach { placeable ->
                if (x + placeable.width > totalWidth) {
                    x = 0
                    y += maxHeightInRow
                    maxHeightInRow = 0
                }

                placeable.placeRelative(x, y)

                x += placeable.width
                maxHeightInRow = maxOf(maxHeightInRow, placeable.height)
            }
        }
    }
}