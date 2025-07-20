package br.com.fitnesspro.workout.ui.state

import br.com.fitnesspro.core.state.ILoadingUIState
import br.com.fitnesspro.core.state.MessageDialogState

data class PreDefinitionsUIState(
    val onToggleBottomSheetNewPredefinition: () -> Unit = {},
    val showBottomSheetNewPredefinition: Boolean = false,
    val messageDialogState: MessageDialogState = MessageDialogState(),
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = {}
): ILoadingUIState