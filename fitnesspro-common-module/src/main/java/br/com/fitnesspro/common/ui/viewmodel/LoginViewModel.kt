package br.com.fitnesspro.common.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.usecase.login.EnumLoginValidationTypes
import br.com.fitnesspro.common.usecase.login.EnumValidatedLoginFields
import br.com.fitnesspro.common.usecase.login.LoginUseCase
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.core.enums.EnumDialogType
import br.com.fitnesspro.core.validation.FieldValidationError
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

    private val _uiState: MutableStateFlow<br.com.fitnesspro.common.ui.state.LoginUIState> = MutableStateFlow(
        br.com.fitnesspro.common.ui.state.LoginUIState()
    )
    val uiState get() = _uiState.asStateFlow()

    init {
        initialUIStateLoad()
        loadEmailAuthenticatedUser()
    }

    private fun loadEmailAuthenticatedUser() {
        viewModelScope.launch {
            userRepository.getAuthenticatedTOUser()?.apply {
                _uiState.value = _uiState.value.copy(
                    email = _uiState.value.email.copy(
                        value = email!!
                    )
                )
            }
        }
    }

    private fun initialUIStateLoad() {
        _uiState.update { currentState ->
            currentState.copy(
                email = TextField(onChange = {
                    _uiState.value = _uiState.value.copy(
                        email = _uiState.value.email.copy(
                            value = it,
                            errorMessage = ""
                        )
                    )
                }),
                password = TextField(onChange = {
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

    private fun showValidationMessages(validationsResult: List<FieldValidationError<EnumValidatedLoginFields, EnumLoginValidationTypes>>) {
        val dialogValidations = validationsResult.firstOrNull { it.field == null }

        if (dialogValidations != null) {
            _uiState.value.onShowDialog?.onShow(
                type = EnumDialogType.ERROR,
                message = dialogValidations.message,
                onConfirm = { _uiState.value.onHideDialog.invoke() },
                onCancel = { _uiState.value.onHideDialog.invoke() }
            )

            return
        }

        validationsResult.forEach {
            when(it.field!!) {
                EnumValidatedLoginFields.EMAIL -> {
                    _uiState.value = _uiState.value.copy(
                        email = _uiState.value.email.copy(
                            errorMessage = it.message
                        )
                    )
                }
                EnumValidatedLoginFields.PASSWORD -> {
                    _uiState.value = _uiState.value.copy(
                        password = _uiState.value.password.copy(
                            errorMessage = it.message
                        )
                    )
                }
            }
        }

    }
}