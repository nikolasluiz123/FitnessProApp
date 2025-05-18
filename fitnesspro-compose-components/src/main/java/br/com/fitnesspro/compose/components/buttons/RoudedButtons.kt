package br.com.fitnesspro.compose.components.buttons

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.compose.components.R.string
import br.com.fitnesspro.core.R
import br.com.fitnesspro.core.theme.FitnessProTheme

/**
 * Botão arredondado com o ícone da Google
 *
 * @param modifier o modificador do botão
 * @param onClick o evento de clique do botão
 *
 * @author Nikolas Luiz Schmitt
 */
@Composable
fun RoundedGoogleButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
    ),
    onClick: () -> Unit = { }
) {
    IconButton(
        modifier = modifier.size(48.dp),
        onClick = onClick,
        colors = colors,
        enabled = enabled
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = stringResource(string.rounded_button_google_content_description),
        )
    }
}

/**
 * Botão arredondado com o ícone do Facebook
 *
 * @param modifier o modificador do botão
 * @param onClick o evento de clique do botão
 *
 * @author Nikolas Luiz Schmitt
 */
@Composable
fun RoundedFacebookButton(
    modifier: Modifier = Modifier,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
    ),
    onClick: () -> Unit = { }
) {
    IconButton(
        modifier = modifier.size(48.dp),
        onClick = onClick,
        colors = colors
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_facebook),
            contentDescription = stringResource(string.rounded_button_facebook_content_description),
        )
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun RoundedGoogleButtonPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            RoundedGoogleButton()
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun RoundedFacebookButtonPreviewDark() {
    FitnessProTheme(darkTheme = true) {
        Surface {
            RoundedFacebookButton()
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun RoundedGoogleButtonPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            RoundedGoogleButton()
        }
    }
}

@Preview(device = "id:small_phone")
@Composable
private fun RoundedFacebookButtonPreviewLight() {
    FitnessProTheme(darkTheme = false) {
        Surface {
            RoundedFacebookButton()
        }
    }
}