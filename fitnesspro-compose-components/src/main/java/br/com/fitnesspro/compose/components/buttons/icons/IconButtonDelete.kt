package br.com.fitnesspro.compose.components.buttons.icons

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import br.com.fitnesspro.compose.components.R
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.GREY_800

/**
 * Botão com ícone de deletar.
 *
 * @param onClick Ação ao clicar.
 *
 * @author Nikolas Luiz Schmitt
 */
@Composable
fun IconButtonDelete(
    modifier: Modifier = Modifier,
    iconColor: Color = GREY_800,
    enabled: Boolean = true,
    contentDescriptionResId: Int? = R.string.label_delete,
    onClick: () -> Unit = { }
) {
    FitnessProIconButton(
        modifier = modifier,
        resId = br.com.fitnesspro.core.R.drawable.ic_delete_32dp,
        iconColor = iconColor,
        enabled = enabled,
        contentDescriptionResId = contentDescriptionResId,
        onClick = onClick
    )
}



@Preview
@Composable
fun IconButtonDeletePreview() {
    FitnessProTheme {
        Surface {
            IconButtonDelete()
        }
    }
}