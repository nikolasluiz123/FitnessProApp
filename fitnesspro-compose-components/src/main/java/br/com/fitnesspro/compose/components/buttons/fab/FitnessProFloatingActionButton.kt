package br.com.fitnesspro.compose.components.buttons.fab

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * FAB padrão do aplicativo.
 *
 * @param modifier O modificador para aplicar ao componente.
 * @param containerColor A cor de fundo do botão.
 * @param onClick A ação a ser realizada quando o botão é clicado.
 * @param content O conteúdo do botão, geralmente um ícone.
 *
 * @author Nikolas Luiz Schmitt
 */
@Composable
fun FitnessProFloatingActionButton(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit = { },
    content: @Composable () -> Unit
) {
    FloatingActionButton(
        modifier = modifier,
        containerColor = containerColor,
        onClick = onClick
    ) {
        content()
    }
}