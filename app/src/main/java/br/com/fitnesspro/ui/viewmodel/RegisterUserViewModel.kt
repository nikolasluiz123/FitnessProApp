package br.com.fitnesspro.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import br.com.fitnesspro.R
import br.com.fitnesspro.compose.components.state.Field
import br.com.fitnesspro.compose.components.tabs.Tab
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.model.User
import br.com.fitnesspro.model.enums.EnumUserProfile
import br.com.fitnesspro.repository.UserRepository
import br.com.fitnesspro.service.data.access.dto.user.EnumUserDTOValidationFields
import br.com.fitnesspro.service.data.access.webclients.validation.ValidationResult
import br.com.fitnesspro.ui.bottomsheet.EnumOptionsBottomSheetRegisterUser
import br.com.fitnesspro.ui.navigation.RegisterUserScreenArgs
import br.com.fitnesspro.ui.navigation.registerUserArguments
import br.com.fitnesspro.ui.state.RegisterUserUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RegisterUserViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState: MutableStateFlow<RegisterUserUIState> = MutableStateFlow(RegisterUserUIState())
    val uiState get() = _uiState.asStateFlow()

    private val jsonArgs: String? = savedStateHandle[registerUserArguments]

    init {
        jsonArgs?.fromJsonNavParamToArgs(RegisterUserScreenArgs::class.java)?.let { args ->
            val tabs = mutableListOf(
                Tab(
                    index = 0,
                    labelResId = R.string.register_user_screen_label_tab_general,
                    selected = true,
                    enabled = true
                ),
                Tab(
                    index = 1,
                    labelResId = R.string.register_user_screen_label_tab_gym,
                    selected = false,
                    enabled = false
                )
            )

            _uiState.update { currentState ->
                currentState.copy(title = getTitle(args),
                    subtitle = getSubtitle(),
                    context = args.context,
                    tabs = tabs,
                    firstName = Field(onChange = {
                        _uiState.value = _uiState.value.copy(firstName = _uiState.value.firstName.copy(value = it, errorMessage = ""))
                    }),
                    lastName = Field(onChange = {
                        _uiState.value = _uiState.value.copy(lastName = _uiState.value.lastName.copy(value = it, errorMessage = ""))
                    }),
                    username = Field(onChange = {
                        _uiState.value = _uiState.value.copy(username = _uiState.value.username.copy(value = it, errorMessage = ""))
                    }),
                    email = Field(onChange = {
                        _uiState.value = _uiState.value.copy(email = _uiState.value.email.copy(value = it, errorMessage = ""))
                    }),
                    password = Field(onChange = {
                        _uiState.value = _uiState.value.copy(password = _uiState.value.password.copy(value = it, errorMessage = ""))
                    }),
                )
            }
        }

    }

    /**
     * Função utilizada para recuperar o titulo que deve ser exibido na barra superior.
     */
    private fun getTitle(args: RegisterUserScreenArgs): String {
        return when (args.context) {
            EnumOptionsBottomSheetRegisterUser.STUDENT -> {
                if (_uiState.value.user == null) {
                    context.getString(R.string.register_user_screen_title_new_student)
                } else {
                    context.getString(R.string.register_user_screen_title_student)
                }
            }

            EnumOptionsBottomSheetRegisterUser.TRAINER -> {
                if (_uiState.value.user == null) {
                    context.getString(R.string.register_user_screen_title_new_trainer)
                } else {
                    context.getString(R.string.register_user_screen_title_trainer)
                }
            }

            EnumOptionsBottomSheetRegisterUser.NUTRITIONIST -> {
                if (_uiState.value.user == null) {
                    context.getString(R.string.register_user_screen_title_new_nutritionist)
                } else {
                    context.getString(R.string.register_user_screen_title_nutritionist)
                }
            }
        }
    }

    /**
     * Função utilizada para recuperar o subtitulo que deve ser exibido na barra superior.
     */
    private fun getSubtitle(): String? {
        return _uiState.value.user?.firstName
    }

    /**
     * Função utilizada para salvar o usuário.
     */
    suspend fun saveUser(): Boolean {
        val user = User(
            firstName = _uiState.value.firstName.value,
            lastName = _uiState.value.lastName.value,
            username = _uiState.value.username.value,
            email = _uiState.value.email.value,
            password = _uiState.value.password.value,
            profile = getUserProfile()!!
        )

        val result = userRepository.register(user)

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

        return result is ValidationResult.Success
    }

    /**
     * Função para recuperar o [EnumUserProfile] baseado no [EnumOptionsBottomSheetRegisterUser] que é definido ao clicar
     * no bottom sheet.
     */
    private fun getUserProfile(): EnumUserProfile? {
        return when(_uiState.value.context) {
            EnumOptionsBottomSheetRegisterUser.STUDENT -> EnumUserProfile.STUDENT
            EnumOptionsBottomSheetRegisterUser.TRAINER -> EnumUserProfile.TRAINER
            EnumOptionsBottomSheetRegisterUser.NUTRITIONIST -> EnumUserProfile.NUTRITIONIST
            else -> null
        }
    }
}