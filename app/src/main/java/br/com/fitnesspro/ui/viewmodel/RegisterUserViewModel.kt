package br.com.fitnesspro.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import br.com.fitnesspro.R
import br.com.fitnesspro.compose.components.state.Field
import br.com.fitnesspro.compose.components.tabs.Tab
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.repository.UserRepository
import br.com.fitnesspro.ui.bottomsheet.EnumOptionsBottomSheetRegisterUser
import br.com.fitnesspro.ui.navigation.RegisterUserScreenArgs
import br.com.fitnesspro.ui.navigation.registerUserArguments
import br.com.fitnesspro.ui.screen.registeruser.enums.EnumTabsRegisterUserScreen
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
            initialLoadUIState(args)
        }
    }

    private fun initialLoadUIState(args: RegisterUserScreenArgs) {
        val tabs = getTabsWithDefaultState()

        _uiState.update { currentState ->
            currentState.copy(
                title = getTitle(context = args.context),
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
                name = Field(onChange = {
                    _uiState.value = _uiState.value.copy(
                        name = _uiState.value.name.copy(
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
                }),
                birthDate = Field(onChange = {
                    _uiState.value = _uiState.value.copy(
                        birthDate = _uiState.value.birthDate.copy(
                            value = it,
                            errorMessage = ""
                        )
                    )
                }),
                phone = Field(onChange = {
                    _uiState.value = _uiState.value.copy(
                        phone = _uiState.value.phone.copy(
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
    private fun getTitle(context: EnumOptionsBottomSheetRegisterUser? = null, user: User? = null): String {
        return if (user != null) {
            when(user.type!!) {
                EnumUserType.ACADEMY_MEMBER -> this.context.getString(R.string.register_user_screen_title_academy_member)
                EnumUserType.PERSONAL_TRAINER -> this.context.getString(R.string.register_user_screen_title_personal_trainer)
                EnumUserType.NUTRITIONIST -> this.context.getString(R.string.register_user_screen_title_nutritionist)
            }
        } else {
            when (context) {
                EnumOptionsBottomSheetRegisterUser.ACADEMY_MEMBER -> this.context.getString(R.string.register_user_screen_title_new_academy_member)
                EnumOptionsBottomSheetRegisterUser.PERSONAL_TRAINER -> this.context.getString(R.string.register_user_screen_title_new_personal_trainer)
                EnumOptionsBottomSheetRegisterUser.NUTRITIONIST -> this.context.getString(R.string.register_user_screen_title_new_nutritionist)
                else -> ""
            }
        }
    }
}