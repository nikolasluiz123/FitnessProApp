package br.com.fitnesspro.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fitnesspro.R
import br.com.fitnesspro.compose.components.state.Field
import br.com.fitnesspro.compose.components.tabs.Tab
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.extensions.dataStore
import br.com.fitnesspro.extensions.getUserSession
import br.com.fitnesspro.model.User
import br.com.fitnesspro.model.enums.EnumUserProfile
import br.com.fitnesspro.repository.UserRepository
import br.com.fitnesspro.service.data.access.dto.user.enums.EnumUserDTOValidationFields
import br.com.fitnesspro.service.data.access.webclients.result.ValidationResult
import br.com.fitnesspro.ui.bottomsheet.EnumOptionsBottomSheetRegisterUser
import br.com.fitnesspro.ui.navigation.RegisterUserScreenArgs
import br.com.fitnesspro.ui.navigation.registerUserArguments
import br.com.fitnesspro.ui.screen.registeruser.callback.OnServerError
import br.com.fitnesspro.ui.screen.registeruser.enums.EnumTabsRegisterUserScreen
import br.com.fitnesspro.ui.state.RegisterUserUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterUserViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState: MutableStateFlow<RegisterUserUIState> =
        MutableStateFlow(RegisterUserUIState())
    val uiState get() = _uiState.asStateFlow()

    private val jsonArgs: String? = savedStateHandle[registerUserArguments]

    init {
        jsonArgs?.fromJsonNavParamToArgs(RegisterUserScreenArgs::class.java)?.let { args ->
            initialLoadUIState(args)

            if (args.context == null) {
                viewModelScope.launch {
                    loadUIStateWithUserInfos()
                }
            }
        }
    }

    private suspend fun loadUIStateWithUserInfos() {
        context.dataStore.getUserSession()?.let { user ->
            _uiState.update { currentState ->
                currentState.copy(
                    user = user,
                    firstName = _uiState.value.firstName.copy(value = user.firstName),
                    lastName = _uiState.value.lastName.copy(value = user.lastName),
                    username = _uiState.value.username.copy(value = user.username),
                    password = _uiState.value.password.copy(value = user.password),
                    email = _uiState.value.email.copy(value = user.email),
                    title = getTitle(user = user),
                    subtitle = user.fullName,
                )
            }
        }
    }

    private fun initialLoadUIState(args: RegisterUserScreenArgs) {
        val tabs = getTabsWithDefaultState()

        _uiState.update { currentState ->
            currentState.copy(
                title = getTitle(sheetOption = args.context),
                context = args.context,
                tabs = tabs,
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
                firstName = Field(onChange = {
                    _uiState.value = _uiState.value.copy(
                        firstName = _uiState.value.firstName.copy(
                            value = it,
                            errorMessage = ""
                        )
                    )
                }),
                lastName = Field(onChange = {
                    _uiState.value = _uiState.value.copy(
                        lastName = _uiState.value.lastName.copy(
                            value = it,
                            errorMessage = ""
                        )
                    )
                }),
                username = Field(onChange = {
                    _uiState.value = _uiState.value.copy(
                        username = _uiState.value.username.copy(
                            value = it,
                            errorMessage = ""
                        )
                    )
                }),
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
                })
            )
        }
    }

    private fun getTabsWithDefaultState() = mutableListOf(
        Tab(
            enum = EnumTabsRegisterUserScreen.GENERAL,
            selected = mutableStateOf(true),
            isEnabled = { true }
        ),
        Tab(
            enum = EnumTabsRegisterUserScreen.ACADEMY,
            selected = mutableStateOf(false),
            isEnabled = { _uiState.value.user != null }
        )
    )

    /**
     * Função utilizada para recuperar o titulo que deve ser exibido na barra superior.
     */
    private fun getTitle(sheetOption: EnumOptionsBottomSheetRegisterUser? = null, user: User? = null): String {
        return if (user != null) {
            when(user.profile) {
                EnumUserProfile.STUDENT -> context.getString(R.string.register_user_screen_title_student)
                EnumUserProfile.TRAINER -> context.getString(R.string.register_user_screen_title_trainer)
                EnumUserProfile.NUTRITIONIST -> context.getString(R.string.register_user_screen_title_nutritionist)
            }
        } else {
            when (sheetOption) {
                EnumOptionsBottomSheetRegisterUser.STUDENT -> context.getString(R.string.register_user_screen_title_new_student)
                EnumOptionsBottomSheetRegisterUser.TRAINER -> context.getString(R.string.register_user_screen_title_new_trainer)
                EnumOptionsBottomSheetRegisterUser.NUTRITIONIST -> context.getString(R.string.register_user_screen_title_new_nutritionist)
                else -> ""
            }
        }
    }

    /**
     * Função utilizada para salvar o usuário.
     */
    suspend fun saveUser(onServerError: OnServerError): Boolean {
        val user = _uiState.value.user ?: User(
            firstName = _uiState.value.firstName.value,
            lastName = _uiState.value.lastName.value,
            username = _uiState.value.username.value,
            email = _uiState.value.email.value,
            password = _uiState.value.password.value,
            profile = getUserProfile()!!
        )

        val result = userRepository.saveUser(user)

        when (result) {
            is ValidationResult.Success -> {
                _uiState.value = _uiState.value.copy(
                    user = user,
                    firstName = _uiState.value.firstName.copy(errorMessage = ""),
                    lastName = _uiState.value.lastName.copy(errorMessage = ""),
                    email = _uiState.value.email.copy(errorMessage = ""),
                    password = _uiState.value.password.copy(errorMessage = ""),
                )
            }

            is ValidationResult.Error<*> -> {
                if (result.fieldErrors.isEmpty()) {
                    onServerError.onError((result.message!!))
                } else {
                    result.fieldErrors.forEach { (field, message) ->
                        when (field) {
                            EnumUserDTOValidationFields.FIRST_NAME -> {
                                _uiState.value = _uiState.value.copy(
                                    firstName = _uiState.value.firstName.copy(errorMessage = message)
                                )
                            }

                            EnumUserDTOValidationFields.LAST_NAME -> {
                                _uiState.value = _uiState.value.copy(
                                    lastName = _uiState.value.lastName.copy(errorMessage = message)
                                )
                            }

                            EnumUserDTOValidationFields.USERNAME -> {
                                _uiState.value = _uiState.value.copy(
                                    username = _uiState.value.username.copy(errorMessage = message)
                                )
                            }

                            EnumUserDTOValidationFields.EMAIL -> {
                                _uiState.value = _uiState.value.copy(
                                    email = _uiState.value.email.copy(errorMessage = message)
                                )
                            }

                            EnumUserDTOValidationFields.PASSWORD -> {
                                _uiState.value = _uiState.value.copy(
                                    password = _uiState.value.password.copy(errorMessage = message)
                                )
                            }
                        }
                    }
                }
            }
        }

        return result is ValidationResult.Success
    }

    /**
     * Função para recuperar o [EnumUserProfile] baseado no [EnumOptionsBottomSheetRegisterUser] que é definido ao clicar
     * no bottom sheet.
     */
    private fun getUserProfile(): EnumUserProfile? {
        return when (_uiState.value.context) {
            EnumOptionsBottomSheetRegisterUser.STUDENT -> EnumUserProfile.STUDENT
            EnumOptionsBottomSheetRegisterUser.TRAINER -> EnumUserProfile.TRAINER
            EnumOptionsBottomSheetRegisterUser.NUTRITIONIST -> EnumUserProfile.NUTRITIONIST
            else -> null
        }
    }
}