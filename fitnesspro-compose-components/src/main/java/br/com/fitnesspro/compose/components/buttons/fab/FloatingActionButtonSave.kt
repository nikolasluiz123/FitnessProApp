package br.com.market.market.compose.components.button.fab

import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.fitnesspro.compose.components.R
import br.com.fitnesspro.compose.components.buttons.fab.FitnessProFloatingActionButton
import br.com.fitnesspro.core.R.drawable
import br.com.fitnesspro.core.theme.FitnessProTheme

/**
 * FAB que representa a ação de salvar.
 *
 * @param modifier O modificador para aplicar ao componente.
 * @param onClick A ação a ser realizada quando o botão é clicado.
 * @uthor Nikolas Luiz Schmitt
 */
@Composable
fun FloatingActionButtonSave(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FitnessProFloatingActionButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(id = drawable.ic_check_24dp),
            contentDescription = stringResource(R.string.label_save),
        )
    }
}



@Preview
@Composable
fun FloatingActionButtonSavePreview() {
    FitnessProTheme {
        Surface {
            FloatingActionButtonSave(
                onClick = { }
            )
        }
    }
}
