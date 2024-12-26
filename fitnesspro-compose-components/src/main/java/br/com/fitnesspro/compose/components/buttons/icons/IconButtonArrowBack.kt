package br.com.fitnesspro.compose.components.buttons.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.fitnesspro.compose.components.R
import br.com.fitnesspro.core.theme.FitnessProTheme

/**
 * Botão com ícone de voltar.
 *
 * @param onClick Ação ao clicar.
 *
 * @author Nikolas Luiz Schmitt
 */
@Composable
fun IconButtonArrowBack(
    color: Color = Color.White,
    onClick: () -> Unit = { }
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.label_voltar),
            tint = color
        )
    }
}


@Preview
@Composable
fun IconButtonArrowBackPreview() {
    FitnessProTheme {
        Surface {
            IconButtonArrowBack()
        }
    }
}