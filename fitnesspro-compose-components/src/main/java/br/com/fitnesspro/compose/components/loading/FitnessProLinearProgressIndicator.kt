package br.com.fitnesspro.compose.components.loading

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Indicador de progresso em linha.
 *
 * @param show Flag que indica se deve ou não ser exibido o componente.
 * @param modifier Modificadores específicos.
 *
 * @author Nikolas Luiz Schmitt
 */
@Composable
fun FitnessProLinearProgressIndicator(show: Boolean, modifier: Modifier = Modifier) {
    if (show) {
        LinearProgressIndicator(modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary)
    }
}