package br.com.fitnesspro.common.ui.viewmodel

import android.content.Context
import androidx.lifecycle.viewModelScope
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.ui.event.GlobalEvent
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.state.BottomSheetAuthenticationUIState
import br.com.fitnesspro.common.usecase.login.DefaultLoginUseCase
import br.com.fitnesspro.common.usecase.login.GoogleLoginUseCase
import br.com.fitnesspro.common.usecase.login.enums.EnumValidatedLoginFields
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.core.callback.showConfirmationDialog
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.extensions.isNetworkAvailable
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.core.validation.FieldValidationError
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BottomSheetAuthenticationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val defaultLoginUseCase: DefaultLoginUseCase,
    private val googleLoginUseCase: GoogleLoginUseCase,
    private val globalEvents: GlobalEvents
) : FitnessProViewModel() {

    private val _uiState: MutableStateFlow<BottomSheetAuthenticationUIState> = MutableStateFlow(BottomSheetAuthenticationUIState())
    val uiState get() = _uiState.asStateFlow()

    init {
        _uiState.update { currentState ->
            currentState.copy(
                email = initializeEmailTextField(),
                password = initializePasswordTextField(),
                messageDialogState = initializeMessageDialogState(),
                onDismissRequest = {
                    _uiState.value = _uiState.value.copy(showBottomSheet = false)
                },
                onToggleLoading = {
                    _uiState.value = _uiState.value.copy(showLoading = !_uiState.value.showLoading)
                },
            )
        }

        viewModelScope.launch {
            globalEvents.events.collect { event ->
                when (event) {
                    is GlobalEvent.TokenExpired -> {
                        _uiState.value = _uiState.value.copy(
                            showBottomSheet = true
                        )
                    }
                }
            }
        }
    }

    override fun getGlobalEventsBus(): GlobalEvents = globalEvents

    override fun getErrorMessageFrom(throwable: Throwable): String {
        return when {
            throwable is FirebaseAuthInvalidUserException && throwable.errorCode == "ERROR_USER_TOKEN_EXPIRED" -> {
                context.getString(R.string.firebase_user_token_expired_message)
            }

            else -> {
                context.getString(R.string.unknown_error_message)
            }
        }
    }


    override fun onShowErrorDialog(message: String) {
        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(message = message)
    }

    override fun onError(throwable: Throwable) {
        super.onError(throwable)

        if (_uiState.value.showLoading) {
            _uiState.value.onToggleLoading()
        }
    }

    fun login(onSuccess: () -> Unit) {
        launch {
            _uiState.value.onToggleLoading()

            val username = _uiState.value.email.value
            val password = _uiState.value.password.value

            val validationsResult = defaultLoginUseCase.execute(
                email = username,
                password = password,
                authAgain = true
            )

            if (validationsResult.isEmpty()) {
                if (!context.isNetworkAvailable()) {
                    _uiState.value.messageDialogState.onShowDialog?.showConfirmationDialog(
                        message = context.getString(R.string.validation_msg_firebase_auth_network_error),
                        onConfirm = onSuccess
                    )
                } else {
                    _uiState.value.onDismissRequest()
                }
            } else {
                showValidationMessages(validationsResult)
            }

            _uiState.value.onToggleLoading()
        }
    }

    fun loginWithGoogle() {
        launch {
            _uiState.value.onToggleLoading()

            googleLoginUseCase(authAgain = true)?.let { googleAuthResult ->
                when {
                    googleAuthResult.success.not() -> {
                        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(googleAuthResult.errorMessage!!)
                    }

                    else -> {
                        _uiState.value.onDismissRequest()
                    }
                }
            }

            _uiState.value.onToggleLoading()
        }
    }

    private fun showValidationMessages(validationsResult: List<FieldValidationError<EnumValidatedLoginFields>>) {
        val dialogValidations = validationsResult.firstOrNull { it.field == null }

        if (dialogValidations != null) {
            _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(dialogValidations.message)
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

    private fun initializeMessageDialogState(): MessageDialogState {
        return MessageDialogState(
            onShowDialog = { type, message, onConfirm, onCancel ->
                _uiState.value = _uiState.value.copy(
                    messageDialogState = _uiState.value.messageDialogState.copy(
                        dialogType = type,
                        dialogMessage = message,
                        showDialog = true,
                        onConfirm = onConfirm,
                        onCancel = onCancel
                    )
                )
            },
            onHideDialog = {
                _uiState.value = _uiState.value.copy(
                    messageDialogState = _uiState.value.messageDialogState.copy(
                        showDialog = false
                    )
                )
            }
        )
    }

    private fun initializePasswordTextField() = TextField(onChange = {
        _uiState.value = _uiState.value.copy(
            password = _uiState.value.password.copy(
                value = it,
                errorMessage = ""
            )
        )
    })

    private fun initializeEmailTextField() = TextField(onChange = {
        _uiState.value = _uiState.value.copy(
            email = _uiState.value.email.copy(
                value = it,
                errorMessage = ""
            )
        )
    })
}