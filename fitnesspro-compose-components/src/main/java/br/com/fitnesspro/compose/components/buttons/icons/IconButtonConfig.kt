package br.com.fitnesspro.compose.components.buttons.icons

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import br.com.fitnesspro.compose.components.R
import br.com.fitnesspro.core.theme.FitnessProTheme

@Composable
fun IconButtonConfig(
    modifier: Modifier = Modifier,
    iconColor: Color = Color.White,
    enabled: Boolean = true,
    contentDescriptionResId: Int? = R.string.label_config,
    onClick: () -> Unit = { }
) {
    FitnessProIconButton(
        modifier = modifier,
        resId = br.com.fitnesspro.core.R.drawable.ic_config_48dp,
        iconColor = iconColor,
        enabled = enabled,
        contentDescriptionResId = contentDescriptionResId,
        onClick = onClick
    )
}


@Preview
@Composable
fun IconButtonConfigPreview() {
    FitnessProTheme {
        Surface {
            IconButtonConfig()
        }
    }
}