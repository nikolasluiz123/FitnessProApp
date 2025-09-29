package br.com.fitnesspro.common.ui.viewmodel

import android.content.Context
import br.com.android.ui.compose.components.dialog.message.showConfirmationDialog
import br.com.android.ui.compose.components.dialog.message.showErrorDialog
import br.com.android.ui.compose.components.fields.validation.FieldValidationError
import br.com.core.android.utils.extensions.isNetworkAvailable
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.state.LoginUIState
import br.com.fitnesspro.common.ui.viewmodel.base.FitnessProStatefulViewModel
import br.com.fitnesspro.common.usecase.login.DefaultLoginUseCase
import br.com.fitnesspro.common.usecase.login.GoogleLoginUseCase
import br.com.fitnesspro.common.usecase.login.enums.EnumValidatedLoginFields
import br.com.fitnesspro.to.TOPerson
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val defaultLoginUseCase: DefaultLoginUseCase,
    private val googleLoginUseCase: GoogleLoginUseCase,
    private val userRepository: UserRepository,
    private val globalEvents: GlobalEvents
) : FitnessProStatefulViewModel() {

    private val _uiState: MutableStateFlow<LoginUIState> = MutableStateFlow(LoginUIState())
    val uiState get() = _uiState.asStateFlow()

    init {
        initialLoadUIState()
        loadEmailAuthenticatedUser()
    }

    override fun initialLoadUIState() {
        _uiState.value = _uiState.value.copy(
            email = createTextFieldState(
                getCurrentState = { _uiState.value.email },
                updateState = { _uiState.value = _uiState.value.copy(email = it) }
            ),
            password = createTextFieldState(
                getCurrentState = { _uiState.value.password },
                updateState = { _uiState.value = _uiState.value.copy(password = it) }
            ),
            messageDialogState = createMessageDialogState(
                getCurrentState = { _uiState.value.messageDialogState },
                updateState = { _uiState.value = _uiState.value.copy(messageDialogState = it) }
            ),
            onToggleLoading = {
                _uiState.value = _uiState.value.copy(showLoading = !_uiState.value.showLoading)
            },
        )
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

    private fun loadEmailAuthenticatedUser() {
        launch {
            userRepository.getAuthenticatedTOUser()?.apply {
                _uiState.value = _uiState.value.copy(
                    email = _uiState.value.email.copy(value = email!!)
                )
            }
        }
    }

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

            googleLoginUseCase()?.let { googleAuthResult ->
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
}