package br.com.fitnesspro.compose.components.dialog

import androidx.compose.runtime.Composable
import br.com.android.firebase.toolkit.analytics.logButtonClick
import br.com.android.ui.compose.components.dialog.message.BaseMessageDialog
import br.com.android.ui.compose.components.dialog.message.MessageDialogState
import br.com.android.ui.compose.components.dialog.message.enums.EnumDialogType
import br.com.fitnesspro.compose.components.dialog.enums.EnumFitnessProMessageDialogTags
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics

@Composable
fun FitnessProMessageDialog(state: MessageDialogState) {
    BaseMessageDialog(
        type = state.dialogType,
        show = state.showDialog,
        onDismissRequest = state.onHideDialog,
        message = state.dialogMessage,
        onConfirm = {
            when (state.dialogType) {
                EnumDialogType.ERROR, EnumDialogType.INFORMATION -> {
                    Firebase.analytics.logButtonClick(
                        EnumFitnessProMessageDialogTags.FITNESS_PRO_MESSAGE_DIALOG_OK_BUTTON
                    )
                }

                EnumDialogType.CONFIRMATION -> {
                    Firebase.analytics.logButtonClick(
                        EnumFitnessProMessageDialogTags.FITNESS_PRO_MESSAGE_DIALOG_CONFIRM_BUTTON
                    )
                }
            }

            state.onConfirm()
        },
        onCancel = {
            Firebase.analytics.logButtonClick(
                EnumFitnessProMessageDialogTags.FITNESS_PRO_MESSAGE_DIALOG_CANCEL_BUTTON
            )
            state.onCancel()
        }
    )
}