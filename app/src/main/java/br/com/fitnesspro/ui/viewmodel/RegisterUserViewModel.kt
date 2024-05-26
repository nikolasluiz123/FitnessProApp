package br.com.fitnesspro.ui.viewmodel

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fitnesspro.R
import br.com.fitnesspro.compose.components.state.Field
import br.com.fitnesspro.compose.components.tabs.Tab
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.core.extensions.parseToLocalDate
import br.com.fitnesspro.core.validation.ValidationResult
import br.com.fitnesspro.model.EnumUserValidatedFields
import br.com.fitnesspro.model.User
import br.com.fitnesspro.repository.UserRepository
import br.com.fitnesspro.ui.bottomsheet.EnumOptionsBottomSheetRegisterUser
import br.com.fitnesspro.ui.navigation.RegisterUserScreenArgs
import br.com.fitnesspro.ui.navigation.registerUserArguments
import br.com.fitnesspro.ui.state.RegisterUserUIState
import br.com.fitnesspro.usecase.RegisterUserUseCase
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
    private val registerUserUseCase: RegisterUserUseCase,
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
                    name = Field(onChange = {
                        _uiState.value = _uiState.value.copy(name = _uiState.value.name.copy(value = it, errorMessage = ""))
                    }),
                    email = Field(onChange = {
                        _uiState.value =
                            _uiState.value.copy(email = _uiState.value.email.copy(value = it, errorMessage = ""))
                    }),
                    password = Field(onChange = {
                        _uiState.value =
                            _uiState.value.copy(password = _uiState.value.password.copy(value = it, errorMessage = ""))
                    }),
                    birthDate = Field(onChange = {
                        _uiState.value =
                            _uiState.value.copy(birthDate = _uiState.value.birthDate.copy(value = it, errorMessage = ""))
                    }),
                    phone = Field(onChange = {
                        _uiState.value =
                            _uiState.value.copy(phone = _uiState.value.phone.copy(value = it, errorMessage = ""))
                    })
                )
            }
        }

    }

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

    private fun getSubtitle(): String? {
        return _uiState.value.user?.name
    }

    suspend fun saveUser(): Boolean {
        val user = User().apply {
            name = _uiState.value.name.value
            email = _uiState.value.email.value
            password = _uiState.value.password.value
            birthDate = _uiState.value.birthDate.value.parseToLocalDate(EnumDateTimePatterns.DATE_ONLY_NUMBERS)
            phone = _uiState.value.phone.value
        }

        val result = registerUserUseCase.saveUser(user)

        when (result) {
            is ValidationResult.Success -> {
                _uiState.value = _uiState.value.copy(
                    user = user,
                    name = _uiState.value.name.copy(errorMessage = ""),
                    email = _uiState.value.email.copy(errorMessage = ""),
                    password = _uiState.value.password.copy(errorMessage = ""),
                    birthDate = _uiState.value.birthDate.copy(errorMessage = ""),
                    phone = _uiState.value.phone.copy(errorMessage = "")
                )
            }

            is ValidationResult.Error<*> -> {
                result.fieldErrors.forEach { (field, messageResId) ->
                    when (field) {
                        EnumUserValidatedFields.NAME -> {
                            _uiState.value = _uiState.value.copy(
                                name = _uiState.value.name.copy(errorMessage = context.getString(messageResId))
                            )
                        }

                        EnumUserValidatedFields.EMAIL -> {
                            _uiState.value = _uiState.value.copy(
                                email = _uiState.value.email.copy(errorMessage = context.getString(messageResId))
                            )
                        }

                        EnumUserValidatedFields.PASSWORD -> {
                            _uiState.value = _uiState.value.copy(
                                password = _uiState.value.password.copy(errorMessage = context.getString(messageResId))
                            )
                        }

                        EnumUserValidatedFields.BIRTH_DATE -> {
                            _uiState.value = _uiState.value.copy(
                                birthDate = _uiState.value.birthDate.copy(errorMessage = context.getString(messageResId))
                            )
                        }
                    }
                }
            }
        }

        return result is ValidationResult.Success
    }
}