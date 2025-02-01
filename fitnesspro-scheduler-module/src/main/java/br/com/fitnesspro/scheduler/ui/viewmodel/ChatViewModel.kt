package br.com.fitnesspro.scheduler.ui.viewmodel

import android.content.Context
import br.com.fitnesspro.common.ui.viewmodel.FitnessProViewModel
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.scheduler.ui.state.ChatUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    @ApplicationContext private val context: Context
): FitnessProViewModel() {

    private val _uiState: MutableStateFlow<ChatUIState> = MutableStateFlow(ChatUIState())
    val uiState get() = _uiState.asStateFlow()

    init {
        initialLoadUIState()

    }

    private fun initialLoadUIState() {
    }

    override fun onShowError(throwable: Throwable) {
        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(
            message = context.getString(br.com.fitnesspro.common.R.string.unknown_error_message)
        )
    }
}