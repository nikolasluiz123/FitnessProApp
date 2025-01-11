package br.com.fitnesspro.compose.components.buttons.icons

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import br.com.fitnesspro.core.R
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.GREY_700

@Composable
fun IconButtonArrowDown(
    modifier: Modifier = Modifier,
    iconColor: Color = GREY_700,
    enabled: Boolean = true,
    contentDescriptionResId: Int? = null,
    onClick: () -> Unit = { }
) {
    FitnessProIconButton(
        modifier = modifier,
        resId = R.drawable.ic_arrow_down_24dp,
        iconColor = iconColor,
        enabled = enabled,
        contentDescriptionResId = contentDescriptionResId,
        onClick = onClick
    )
}

@Preview
@Composable
private fun IconButtonArrawDownPreview() {
    FitnessProTheme {
        Surface {
            IconButtonArrowDown()

        }
    }
}