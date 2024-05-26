package br.com.fitnesspro.compose.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.core.theme.FitnessProTheme
import br.com.fitnesspro.core.theme.ButtonTextStyle
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
    enabled: Boolean = true
) {
    Button(
        modifier = modifier,
        onClick = onClickListener,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Text(text = label, style = ButtonTextStyle)
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
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer),
        onClick = onClickListener,
        enabled = enabled,
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.primary,
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
    enabled: Boolean = true
) {
    TextButton(
        modifier = modifier,
        onClick = onClickListener,
        enabled = enabled,
    ) {
        Text(text = label, style = TextButtonTextStyle)
    }
}

@Preview
@Composable
private fun FitnessProButtonPreview() {
    FitnessProTheme {
        Surface {
            FitnessProButton(
                label = "Button",
                onClickListener = {}
            )
        }
    }
}

@Preview
@Composable
private fun FitnessProOutlinedButtonPreview() {
    FitnessProTheme {
        Surface {
            FitnessProOutlinedButton(
                label = "Button",
                onClickListener = {}
            )
        }
    }
}

@Preview
@Composable
private fun FitnessProTextButtonPreview() {
    FitnessProTheme {
        Surface {
            FitnessProTextButton(
                label = "Button",
                onClickListener = {}
            )
        }
    }
}