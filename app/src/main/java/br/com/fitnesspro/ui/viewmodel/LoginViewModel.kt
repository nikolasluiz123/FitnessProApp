package br.com.fitnesspro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fitnesspro.compose.components.state.Field
import br.com.fitnesspro.core.enums.EnumDialogType
import br.com.fitnesspro.repository.UserRepository
import br.com.fitnesspro.ui.state.LoginUIState
import br.com.fitnesspro.usecase.login.EnumValidatedLoginFields
import br.com.fitnesspro.usecase.login.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<LoginUIState> = MutableStateFlow(LoginUIState())
    val uiState get() = _uiState.asStateFlow()

    init {
        initialUIStateLoad()
        loadEmailAuthenticatedUser()
    }

    private fun loadEmailAuthenticatedUser() {
        viewModelScope.launch {
            userRepository.getAuthenticatedTOUser()?.apply {
                _uiState.value = _uiState.value.copy(
                    email = Field(value = email!!),
                )
            }
        }
    }

    private fun initialUIStateLoad() {
        _uiState.update { currentState ->
            currentState.copy(
                email = Field(onChange = {
                    _uiState.value = _uiState.value.copy(
                        email = _uiState.value.email.copy(
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
                onToggleLoading = {
                    _uiState.value = _uiState.value.copy(showLoading = !_uiState.value.showLoading)
                },
            )
        }
    }

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val username = _uiState.value.email.value
            val password = _uiState.value.password.value

            val validationsResult = loginUseCase.execute(username, password)

            if (validationsResult.isEmpty()) {
                onSuccess()
            } else {
                showValidationMessages(validationsResult)
            }

        }
    }

    private fun showValidationMessages(validationsResult: List<Pair<EnumValidatedLoginFields?, String>>) {
        val dialogValidations = validationsResult.firstOrNull { it.first == null }

        if (dialogValidations != null) {
            _uiState.value.onShowDialog?.onShow(
                type = EnumDialogType.ERROR,
                message = dialogValidations.second,
                onConfirm = { _uiState.value.onHideDialog.invoke() },
                onCancel = { _uiState.value.onHideDialog.invoke() }
            )

            return
        }

        validationsResult.forEach {
            when(it.first!!) {
                EnumValidatedLoginFields.EMAIL -> {
                    _uiState.value = _uiState.value.copy(
                        email = _uiState.value.email.copy(
                            errorMessage = it.second
                        )
                    )
                }
                EnumValidatedLoginFields.PASSWORD -> {
                    _uiState.value = _uiState.value.copy(
                        password = _uiState.value.password.copy(
                            errorMessage = it.second
                        )
                    )
                }
            }
        }

    }
}