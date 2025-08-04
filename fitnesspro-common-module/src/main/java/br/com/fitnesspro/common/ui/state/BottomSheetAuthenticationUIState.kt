package br.com.fitnesspro.common.ui.state

import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.core.state.ILoadingUIState
import br.com.fitnesspro.core.state.IThrowableUIState
import br.com.fitnesspro.core.state.MessageDialogState

data class BottomSheetAuthenticationUIState(
    val email: TextField = TextField(),
    val password: TextField = TextField(),
    val showBottomSheet: Boolean = false,
    val onDismissRequest: () -> Unit = { },
    override val messageDialogState: MessageDialogState = MessageDialogState(),
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = { },
) : ILoadingUIState, IThrowableUIState