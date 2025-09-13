package br.com.fitnesspro.charts.styles.text

import android.graphics.Paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.com.fitnesspro.charts.styles.text.enums.LongLabelStrategy

data class ChartTextStyle(
    val color: Color = Color.Black,
    val fontSize: TextUnit = 12.sp,
    val fontWeight: FontWeight = FontWeight.Normal,
    val textAlign: Paint.Align = Paint.Align.CENTER,
    val padding: Dp = 4.dp,
    val longLabelStrategy: LongLabelStrategy = LongLabelStrategy.MultiLine
)