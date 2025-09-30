package br.com.fitnesspro.compose.components.buttons.rounded

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import br.com.fitnesspro.core.R

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
            contentDescription = stringResource(br.com.fitnesspro.compose.components.R.string.rounded_button_google_content_description),
        )
    }
}