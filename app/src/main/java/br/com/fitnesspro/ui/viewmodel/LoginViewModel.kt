package br.com.fitnesspro.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fitnesspro.compose.components.state.Field
import br.com.fitnesspro.extensions.dataStore
import br.com.fitnesspro.extensions.getUserSession
import br.com.fitnesspro.repository.UserRepository
import br.com.fitnesspro.service.data.access.dto.user.enums.EnumAuthenticationDTOValidationFields
import br.com.fitnesspro.service.data.access.webclients.result.ValidationResult
import br.com.fitnesspro.ui.state.LoginUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

        viewModelScope.launch {
            loadUIStateWithUserInfos()
        }
    }

    private suspend fun loadUIStateWithUserInfos() {
        context.dataStore.getUserSession()?.let { user ->
            _uiState.update { currentState ->
                currentState.copy(
                    username = _uiState.value.username.copy(value = user.username),
                    password = _uiState.value.password.copy(value = user.password)
                )
            }
        }
    }

    fun login(onError: (String) -> Unit, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val singleResult = userRepository.authenticate(
                username = uiState.value.username.value,
                password = uiState.value.password.value
            )

            withContext(Main) {
                when (val result = singleResult.validationResult) {
                    is ValidationResult.Error<*> -> {
                        if (result.fieldErrors.isEmpty()) {
                            onError(result.message!!)
                        } else {
                            result.fieldErrors.forEach { (field, message) ->
                                when (field) {
                                    EnumAuthenticationDTOValidationFields.USERNAME -> {
                                        _uiState.value = _uiState.value.copy(
                                            username = _uiState.value.username.copy(errorMessage = message)
                                        )
                                    }

                                    EnumAuthenticationDTOValidationFields.PASSWORD -> {
                                        _uiState.value = _uiState.value.copy(
                                            password = _uiState.value.password.copy(errorMessage = message)
                                        )
                                    }
                                }
                            }

                            onError("")
                        }
                    }
                    ValidationResult.Success -> onSuccess()
                }
            }
        }
    }
}