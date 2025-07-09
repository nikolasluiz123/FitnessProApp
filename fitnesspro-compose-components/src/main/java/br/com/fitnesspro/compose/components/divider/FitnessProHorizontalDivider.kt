package br.com.fitnesspro.compose.components.divider

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun FitnessProHorizontalDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outline,
    thickness: Dp = 0.5.dp
) {
    HorizontalDivider(
        modifier = modifier,
        color = color,
        thickness = thickness
    )
}