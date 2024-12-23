package br.com.fitnesspro.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fitnesspro.R
import br.com.fitnesspro.compose.components.state.Field
import br.com.fitnesspro.compose.components.tabs.Tab
import br.com.fitnesspro.core.enums.EnumDateTimePatterns.DATE
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.core.extensions.parseToLocalDate
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.repository.UserRepository
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOUser
import br.com.fitnesspro.ui.bottomsheet.registeruser.EnumOptionsBottomSheetRegisterUser
import br.com.fitnesspro.ui.navigation.RegisterUserScreenArgs
import br.com.fitnesspro.ui.navigation.registerUserArguments
import br.com.fitnesspro.ui.screen.registeruser.decorator.AcademyGroupDecorator
import br.com.fitnesspro.ui.screen.registeruser.enums.EnumTabsRegisterUserScreen
import br.com.fitnesspro.ui.state.RegisterUserUIState
import br.com.fitnesspro.usecase.person.EnumValidatedPersonFields
import br.com.fitnesspro.usecase.person.SavePersonUseCase
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
    private val savePersonUseCase: SavePersonUseCase,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState: MutableStateFlow<RegisterUserUIState> = MutableStateFlow(RegisterUserUIState())
    val uiState get() = _uiState.asStateFlow()

    private val jsonArgs: String? = savedStateHandle[registerUserArguments]

    init {
        jsonArgs?.fromJsonNavParamToArgs(RegisterUserScreenArgs::class.java)?.let { args ->
            initialLoadUIState(args)
            loadUIStateWithAuthenticatedPerson()
        }
    }

    private fun loadUIStateWithAuthenticatedPerson() = viewModelScope.launch {
        userRepository.getAuthenticatedTOPerson()?.let { toPerson ->
            _uiState.update {
                it.copy(
                    title = getTitle(context = null, toPerson = toPerson),
                    subtitle = toPerson.name!!,
                    toPerson = toPerson,
                    academies = getAcademiesFromAuthenticatedPerson(toPerson),
                    isVisibleFieldPhone = isVisibleFieldPhone(context = null, toPerson = toPerson),
                    name = _uiState.value.name.copy(value = toPerson.name!!),
                    email = _uiState.value.email.copy(value = toPerson.toUser?.email!!),
                    birthDate = _uiState.value.birthDate.copy(
                        value = toPerson.birthDate?.format(DATE) ?: ""
                    ),
                    phone = _uiState.value.phone.copy(value = toPerson.phone ?: "")
                )
            }
        }
    }

    private suspend fun getAcademiesFromAuthenticatedPerson(toPerson: TOPerson): List<AcademyGroupDecorator> {
        return userRepository.getAcademies(toPerson.id!!)
    }

    fun updateAcademies() {
        viewModelScope.launch {
            _uiState.value.toPerson?.let {
                _uiState.value = _uiState.value.copy(academies = getAcademiesFromAuthenticatedPerson(it))
            }
        }
    }

    private fun initialLoadUIState(args: RegisterUserScreenArgs) {
        val tabs = getTabsWithDefaultState()

        _uiState.update { currentState ->
            currentState.copy(
                title = getTitle(context = args.context, toPerson = null),
                context = args.context,
                tabs = tabs,
                isVisibleFieldPhone = isVisibleFieldPhone(context = args.context, toPerson = null),
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

    private fun isVisibleFieldPhone(
        context: EnumOptionsBottomSheetRegisterUser?,
        toPerson: TOPerson?
    ): Boolean {
        return (context?.let {
            it in listOf(
                EnumOptionsBottomSheetRegisterUser.NUTRITIONIST,
                EnumOptionsBottomSheetRegisterUser.PERSONAL_TRAINER
            )
        } ?: toPerson?.toUser?.type) in listOf(
            EnumUserType.NUTRITIONIST,
            EnumUserType.PERSONAL_TRAINER
        )
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
            isEnabled = { _uiState.value.toPerson != null }
        )
    )

    /**
     * Função utilizada para recuperar o titulo que deve ser exibido na barra superior.
     */
    private fun getTitle(context: EnumOptionsBottomSheetRegisterUser?, toPerson: TOPerson?): String {
        return if (toPerson != null) {
            when(toPerson.toUser?.type!!) {
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

    fun saveUser(onSuccess: () -> Unit) {
        val toPerson = TOPerson(
            name = _uiState.value.name.value,
            birthDate = _uiState.value.birthDate.value.parseToLocalDate(DATE),
            phone = _uiState.value.phone.value,
            toUser = TOUser(
                email = _uiState.value.email.value,
                password = _uiState.value.password.value,
                type = getUserTypeFromContext(_uiState.value.context!!)
            ),
        )

        viewModelScope.launch {
            val validationResults = savePersonUseCase.execute(toPerson)

            if (validationResults.isEmpty()) {
                updateInfosAfterSave(toPerson)
                onSuccess()
            } else {
                showValidationMessages(validationResults)
            }
        }
    }

    private fun updateInfosAfterSave(toPerson: TOPerson) {
        _uiState.update {
            it.copy(
                title = getTitle(context = _uiState.value.context, toPerson = toPerson),
                subtitle = toPerson.name!!,
                toPerson = toPerson
            )
        }
    }

    private fun showValidationMessages(validationResults: List<Pair<EnumValidatedPersonFields, String>>) {
        validationResults.forEach {
            when (it.first) {
                EnumValidatedPersonFields.NAME -> {
                    _uiState.value = _uiState.value.copy(
                        name = _uiState.value.name.copy(errorMessage = it.second)
                    )
                }

                EnumValidatedPersonFields.EMAIL -> {
                    _uiState.value = _uiState.value.copy(
                        email = _uiState.value.email.copy(errorMessage = it.second)
                    )
                }

                EnumValidatedPersonFields.PASSWORD -> {
                    _uiState.value = _uiState.value.copy(
                        password = _uiState.value.password.copy(errorMessage = it.second)
                    )
                }

                EnumValidatedPersonFields.BIRTH_DATE -> {
                    _uiState.value = _uiState.value.copy(
                        birthDate = _uiState.value.birthDate.copy(errorMessage = it.second)
                    )
                }

                EnumValidatedPersonFields.PHONE -> {
                    _uiState.value = _uiState.value.copy(
                        phone = _uiState.value.phone.copy(errorMessage = it.second)
                    )
                }
            }
        }
    }

    private fun getUserTypeFromContext(context: EnumOptionsBottomSheetRegisterUser): EnumUserType {
        return when (context) {
            EnumOptionsBottomSheetRegisterUser.ACADEMY_MEMBER -> EnumUserType.ACADEMY_MEMBER
            EnumOptionsBottomSheetRegisterUser.PERSONAL_TRAINER -> EnumUserType.PERSONAL_TRAINER
            EnumOptionsBottomSheetRegisterUser.NUTRITIONIST -> EnumUserType.NUTRITIONIST
        }
    }
}