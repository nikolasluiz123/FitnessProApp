package br.com.fitnesspro.compose.components.buttons.icons

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import br.com.fitnesspro.core.R
import br.com.fitnesspro.core.theme.GREY_700

@Composable
fun IconButtonTime(
    modifier: Modifier = Modifier,
    iconColor: Color = GREY_700,
    enabled: Boolean = true,
    contentDescriptionResId: Int? = br.com.fitnesspro.compose.components.R.string.label_hour,
    onClick: () -> Unit = { }
) {
    FitnessProIconButton(
        modifier = modifier,
        resId = R.drawable.ic_time_24dp,
        iconColor = iconColor,
        enabled = enabled,
        contentDescriptionResId = contentDescriptionResId,
        onClick = onClick
    )
}