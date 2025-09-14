package br.com.fitnesspro.compose.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.core.theme.ButtonTextStyle
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.TextButtonTextStyle

/**
 * Botão padrão com as definições de estilo do app.
 *
 * @param label Texto do botão.
 * @param onClickListener Ação a ser executada quando o botão for clicado.
 * @param modifier Modificador do botão.
 * @param enabled Indica se o botão está habilitado.
 *
 * @author Nikolas Luiz Schmitt
 */
@Composable
fun FitnessProButton(
    label: String,
    onClickListener: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
    )
) {
    Button(
        modifier = modifier,
        onClick = onClickListener,
        enabled = enabled,
        colors = colors
    ) {
        Text(
            text = label,
            style = ButtonTextStyle,
        )
    }
}

/**
 * Botão secundário com as definições de estilo do app.
 *
 * @param label Texto do botão.
 * @param onClickListener Ação a ser executada quando o botão for clicado.
 * @param modifier Modificador do botão.
 * @param enabled Indica se o botão está habilitado.
 *
 * @author Nikolas Luiz Schmitt
 */
@Composable
fun FitnessProOutlinedButton(
    label: String,
    onClickListener: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        modifier = modifier,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        onClick = onClickListener,
        enabled = enabled,
    ) {
        Text(
            text = label,
            style = ButtonTextStyle
        )
    }
}

/**
 * Botão somente com o texto, usado normalmente em dialogs.
 */
@Composable
fun FitnessProTextButton(
    label: String,
    onClickListener: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    enabled: Boolean = true
) {
    TextButton(
        modifier = modifier,
        onClick = onClickListener,
        enabled = enabled,
        colors = colors
    ) {
        Text(text = label, style = TextButtonTextStyle)
    }
}

@Composable
fun DefaultDialogTextButton(
    labelResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        modifier = modifier,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        ),
        onClick = {
            onClick()
        }
    ) {
        Text(text = stringResource(id = labelResId))
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun FitnessProButtonPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            FitnessProButton(
                label = "Button",
                onClickListener = {}
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun FitnessProOutlinedButtonPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            FitnessProOutlinedButton(
                label = "Button",
                onClickListener = {}
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun FitnessProTextButtonPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            FitnessProTextButton(
                label = "Button",
                onClickListener = {}
            )
        }
    }
}


@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun FitnessProButtonPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            FitnessProButton(
                label = "Button",
                onClickListener = {}
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun FitnessProOutlinedButtonPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            FitnessProOutlinedButton(
                label = "Button",
                onClickListener = {}
            )
        }
    }
}

@Preview(device = "id:small_phone", apiLevel = 35)
@Composable
private fun FitnessProTextButtonPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            FitnessProTextButton(
                label = "Button",
                onClickListener = {}
            )
        }
    }
}