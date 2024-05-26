package br.com.fitnesspro.compose.components.buttons

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.compose.components.R.*
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
    onClick: () -> Unit = { }
) {
    IconButton(
        modifier = modifier.size(48.dp),
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = stringResource(string.rounded_button_google_content_description),
            tint = Color.White
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
    onClick: () -> Unit = { }
) {
    IconButton(
        modifier = modifier.size(48.dp),
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_facebook),
            contentDescription = stringResource(string.rounded_button_facebook_content_description),
            tint = Color.White
        )
    }
}

@Preview
@Composable
private fun RoundedGoogleButtonPreview() {
    FitnessProTheme {
        Surface {
            RoundedGoogleButton()
        }
    }
}

@Preview
@Composable
private fun RoundedFacebookButtonPreview() {
    FitnessProTheme {
        Surface {
            RoundedFacebookButton()
        }
    }
}