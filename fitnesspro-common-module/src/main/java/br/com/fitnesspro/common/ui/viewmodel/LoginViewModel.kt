package br.com.fitnesspro.common.ui.viewmodel

import android.content.Context
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.state.LoginUIState
import br.com.fitnesspro.common.usecase.login.DefaultLoginUseCase
import br.com.fitnesspro.common.usecase.login.GoogleLoginUseCase
import br.com.fitnesspro.common.usecase.login.enums.EnumLoginValidationTypes
import br.com.fitnesspro.common.usecase.login.enums.EnumValidatedLoginFields
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.core.callback.showConfirmationDialog
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.extensions.isNetworkAvailable
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.to.TOPerson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val defaultLoginUseCase: DefaultLoginUseCase,
    private val googleLoginUseCase: GoogleLoginUseCase,
    private val userRepository: UserRepository,
    private val globalEvents: GlobalEvents
) : FitnessProViewModel() {

    private val _uiState: MutableStateFlow<LoginUIState> = MutableStateFlow(LoginUIState())
    val uiState get() = _uiState.asStateFlow()

    init {
        initialUIStateLoad()
        loadEmailAuthenticatedUser()
    }

    override fun getGlobalEventsBus(): GlobalEvents = globalEvents

    override fun onShowError(throwable: Throwable) {
        if (_uiState.value.showLoading) {
            _uiState.value.onToggleLoading()
        }

        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(
            message = context.getString(R.string.unknown_error_message)
        )
    }

    private fun loadEmailAuthenticatedUser() {
        launch {
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
                email = initializeEmailTextField(),
                password = initializePasswordTextField(),
                messageDialogState = initializeMessageDialogState(),
                onToggleLoading = {
                    _uiState.value = _uiState.value.copy(showLoading = !_uiState.value.showLoading)
                },
            )
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

    fun login(onSuccess: () -> Unit) {
        launch {
            _uiState.value.onToggleLoading()

            val username = _uiState.value.email.value
            val password = _uiState.value.password.value

            val validationsResult = defaultLoginUseCase.execute(username, password)

            if (validationsResult.isEmpty()) {
                if (!context.isNetworkAvailable()) {
                    _uiState.value.messageDialogState.onShowDialog?.showConfirmationDialog(
                        message = context.getString(R.string.validation_msg_firebase_auth_network_error),
                        onConfirm = onSuccess
                    )
                } else {
                    onSuccess()
                }
            } else {
                showValidationMessages(validationsResult)
            }

            _uiState.value.onToggleLoading()
        }
    }

    fun loginWithGoogle(onUserNotExistsLocal: (TOPerson) -> Unit, onSuccess: () -> Unit) {
        launch {
            _uiState.value.onToggleLoading()

            val googleAuthResult = googleLoginUseCase()

            when {
                googleAuthResult.success.not() -> {
                    _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(googleAuthResult.errorMessage!!)
                }

                googleAuthResult.userExists -> {
                    onSuccess()
                }

                else -> {
                    onUserNotExistsLocal(googleAuthResult.toPerson!!)
                }
            }

            _uiState.value.onToggleLoading()
        }
    }

    private fun showValidationMessages(validationsResult: List<FieldValidationError<EnumValidatedLoginFields, EnumLoginValidationTypes>>) {
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
}