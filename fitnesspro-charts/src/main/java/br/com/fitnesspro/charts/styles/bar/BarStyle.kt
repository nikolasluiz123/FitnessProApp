package br.com.fitnesspro.charts.styles.bar

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.charts.styles.IAnimatedStyle

data class BarStyle(
    val fillColor: Color,
    val borderColor: Color = Color.Transparent,
    val borderWidth: Dp = 1.dp,
    val shape: Shape = RoundedCornerShape(4.dp, 4.dp, 0.dp, 0.dp),
    override val animationDuration: Int = 1000,
    override val animationDelay: Long = 200L
): IAnimatedStyle