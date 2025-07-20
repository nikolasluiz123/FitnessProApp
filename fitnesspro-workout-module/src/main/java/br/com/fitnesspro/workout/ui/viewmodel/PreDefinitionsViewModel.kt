package br.com.fitnesspro.workout.ui.viewmodel

import android.content.Context
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.FitnessProViewModel
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.workout.ui.state.PreDefinitionsUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PreDefinitionsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val globalEvents: GlobalEvents,
): FitnessProViewModel() {

    private val _uiState: MutableStateFlow<PreDefinitionsUIState> = MutableStateFlow(PreDefinitionsUIState())
    val uiState get() = _uiState.asStateFlow()

    init {
        initialLoadUIState()
    }

    private fun initialLoadUIState() {
        _uiState.value = _uiState.value.copy(
            onToggleBottomSheetNewPredefinition = {
                _uiState.value = _uiState.value.copy(
                    showBottomSheetNewPredefinition = !_uiState.value.showBottomSheetNewPredefinition
                )
            }
        )
    }

    override fun getErrorMessageFrom(throwable: Throwable): String {
        return context.getString(br.com.fitnesspro.common.R.string.unknown_error_message)
    }

    override fun onShowErrorDialog(message: String) {
        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(message = message)
    }

    override fun getGlobalEventsBus(): GlobalEvents {
        return globalEvents
    }
}