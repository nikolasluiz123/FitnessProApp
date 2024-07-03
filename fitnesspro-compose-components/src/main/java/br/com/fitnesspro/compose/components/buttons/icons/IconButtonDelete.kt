package br.com.fitnesspro.compose.components.buttons.icons

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.fitnesspro.compose.components.R
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.GREY_500
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
    iconColor: Color = GREY_800,
    disabledIconColor: Color = GREY_500,
    enabled: Boolean = true,
    onClick: () -> Unit = { }
) {
    IconButton(
        enabled = enabled,
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(contentColor = iconColor, disabledContentColor = disabledIconColor)
    ) {
        Icon(
            painter = painterResource(id = br.com.fitnesspro.core.R.drawable.ic_delete_32dp),
            contentDescription = stringResource(R.string.label_delete)
        )
    }
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