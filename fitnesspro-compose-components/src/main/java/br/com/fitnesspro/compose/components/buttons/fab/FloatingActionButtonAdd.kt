package br.com.fitnesspro.compose.components.buttons.fab

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import br.com.fitnesspro.compose.components.R
import br.com.fitnesspro.core.R.*
import br.com.fitnesspro.core.theme.FitnessProTheme

/**
 * FAB que representa a ação de adicionar.
 *
 * @param modifier O modificador para aplicar ao componente.
 * @param onClick A ação a ser realizada quando o botão é clicado.
 * @author Nikolas Luiz Schmitt
 */
@Composable
fun FloatingActionButtonAdd(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { }
) {
    FitnessProFloatingActionButton(
        modifier = modifier,
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(id = drawable.ic_add_24dp),
            contentDescription = stringResource(R.string.label_add)
        )
    }
}

@Preview
@Composable
fun FloatingActionButtonAddPreview() {
    FitnessProTheme {
        Surface {
            FloatingActionButtonAdd()
        }
    }
}