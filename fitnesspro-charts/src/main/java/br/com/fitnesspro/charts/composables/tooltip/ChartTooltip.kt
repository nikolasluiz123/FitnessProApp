package br.com.fitnesspro.charts.composables.tooltip

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import br.com.fitnesspro.charts.styles.tooltip.ChartTooltipStyle
import java.text.NumberFormat

@Composable
internal fun ChartTooltip(
    value: Float,
    style: ChartTooltipStyle,
    modifier: Modifier = Modifier
) {
    val formattedValue = NumberFormat.getInstance().apply {
        maximumFractionDigits = 1
    }.format(value)

    Surface(
        modifier = modifier,
        shape = style.shape,
        color = style.backgroundColor,
        shadowElevation = style.shadowElevation
    ) {
        Text(
            text = formattedValue,
            color = style.textStyle.color,
            fontSize = style.textStyle.fontSize,
            fontWeight = style.textStyle.fontWeight,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(
                horizontal = style.horizontalPadding,
                vertical = style.verticalPadding
            )
        )
    }
}