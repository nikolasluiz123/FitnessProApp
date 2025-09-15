package br.com.fitnesspro.charts.styles.tooltip

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.charts.styles.text.ChartTextStyle

data class ChartTooltipStyle(
    val textStyle: ChartTextStyle,
    val backgroundColor: Color = Color.Black,
    val shape: Shape = RoundedCornerShape(4.dp),
    val horizontalPadding: Dp = 0.dp,
    val verticalPadding: Dp = 0.dp,
    val shadowElevation: Dp = 2.dp
)