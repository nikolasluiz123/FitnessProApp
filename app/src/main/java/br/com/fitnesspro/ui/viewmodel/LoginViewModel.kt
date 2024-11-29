package br.com.fitnesspro.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import br.com.fitnesspro.compose.components.state.Field
import br.com.fitnesspro.repository.UserRepository
import br.com.fitnesspro.ui.state.LoginUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<LoginUIState> = MutableStateFlow(LoginUIState())
    val uiState get() = _uiState.asStateFlow()

    init {
        _uiState.update { currentState ->
            currentState.copy(
                username = Field(onChange = {
                    _uiState.value = _uiState.value.copy(
                        username = _uiState.value.username.copy(
                            value = it,
                            errorMessage = ""
                        )
                    )
                }),
                password = Field(onChange = {
                    _uiState.value = _uiState.value.copy(
                        password = _uiState.value.password.copy(
                            value = it,
                            errorMessage = ""
                        )
                    )
                }),
                onShowDialog = { type, message, onConfirm, onCancel ->
                    _uiState.value = _uiState.value.copy(
                        dialogType = type,
                        showDialog = true,
                        dialogMessage = message,
                        onConfirm = onConfirm,
                        onCancel = onCancel
                    )
                },
                onHideDialog = { _uiState.value = _uiState.value.copy(showDialog = false) },
                onToggleLoading = { _uiState.value = _uiState.value.copy(showLoading = !_uiState.value.showLoading) },
            )
        }
    }
}