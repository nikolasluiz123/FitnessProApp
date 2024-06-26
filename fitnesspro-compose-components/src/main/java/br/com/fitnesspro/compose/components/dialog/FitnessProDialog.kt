package br.com.fitnesspro.compose.components.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import br.com.fitnesspro.compose.components.R
import br.com.fitnesspro.core.enums.EnumDialogType

/**
 * Dialog genérica para exibir qualquer mensagem necessária de acordo com as regras da tela
 * que estiver sendo implementada.
 *
 * @param type Tipo da Dialog, baseado nesse tipo são feitas configurações específicas.
 * @param show Indica se a Dialog deve ser exibida.
 * @param onDismissRequest Callback para fechar a Dialog.
 * @param message Mensagem a ser exibida na Dialog.
 * @param onConfirm Callback para confirmar a Dialog.
 * @param onCancel Callback para cancelar a Dialog.
 *
 * @author Nikolas Luiz Schmitt
 */
@Composable
fun FitnessProDialog(
    type: EnumDialogType,
    show: Boolean,
    onDismissRequest: () -> Unit,
    message: String,
    onConfirm: () -> Unit = { },
    onCancel: () -> Unit = { }
) {
    val scrollState = rememberScrollState()

    if (show) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = stringResource(type.titleResId), style = MaterialTheme.typography.headlineMedium) },
            text = {
                Box(modifier = Modifier.verticalScroll(state = scrollState)) {
                    Text(text = message, style = MaterialTheme.typography.bodyMedium)
                }
            },
            confirmButton = {
                when (type) {
                    EnumDialogType.ERROR -> {
                        DialogTextButton(R.string.label_ok, onDismissRequest, onConfirm)
                    }

                    EnumDialogType.CONFIRMATION -> {
                        DialogTextButton(R.string.label_confirm, onDismissRequest, onConfirm)
                    }
                }
            },
            dismissButton = {
                when (type) {
                    EnumDialogType.ERROR -> {
                        // Dialog do tipo Error não tem botão negativo.
                    }

                    EnumDialogType.CONFIRMATION -> {
                        DialogTextButton(R.string.label_cancel, onDismissRequest, onCancel)
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            textContentColor = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun DialogTextButton(labelResId: Int, onDismissRequest: () -> Unit, onConfirm: () -> Unit) {
    TextButton(
        onClick = {
            onDismissRequest()
            onConfirm()
        }
    ) {
        Text(text = stringResource(id = labelResId))
    }
}