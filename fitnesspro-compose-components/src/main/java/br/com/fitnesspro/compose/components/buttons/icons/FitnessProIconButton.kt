package br.com.fitnesspro.compose.components.buttons.icons

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

@Composable
fun FitnessProIconButton(
    resId: Int,
    iconColor: Color = MaterialTheme.colorScheme.onPrimary,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescriptionResId: Int? = null,
    onClick: () -> Unit = { }
) {
    IconButton(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = iconColor,
            disabledContentColor = iconColor.copy(alpha = 0.5f)
        )
    ) {
        Icon(
            modifier = iconModifier,
            painter = painterResource(id = resId),
            contentDescription = contentDescriptionResId?.let { stringResource(it) }
        )
    }
}

@Composable
fun FitnessProIconButton(
    vector: ImageVector,
    iconColor: Color = MaterialTheme.colorScheme.onPrimary,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescriptionResId: Int? = null,
    onClick: () -> Unit = { }
) {
    IconButton(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = iconColor,
            disabledContentColor = iconColor.copy(alpha = 0.5f)
        )
    ) {
        Icon(
            modifier = iconModifier,
            imageVector = vector,
            contentDescription = contentDescriptionResId?.let { stringResource(it) }
        )
    }
}