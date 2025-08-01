package br.com.fitnesspro.common.ui.viewmodel

import android.content.Context
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.AcademyRepository
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.ui.bottomsheet.registeruser.EnumOptionsBottomSheetRegisterUser
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.navigation.RegisterUserScreenArgs
import br.com.fitnesspro.common.ui.navigation.registerUserArguments
import br.com.fitnesspro.common.ui.screen.registeruser.decorator.AcademyGroupDecorator
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumTabsRegisterUserScreen.ACADEMY
import br.com.fitnesspro.common.ui.screen.registeruser.enums.EnumTabsRegisterUserScreen.GENERAL
import br.com.fitnesspro.common.ui.state.RegisterUserUIState
import br.com.fitnesspro.common.usecase.person.EnumValidatedPersonFields
import br.com.fitnesspro.common.usecase.person.SavePersonUseCase
import br.com.fitnesspro.compose.components.fields.menu.MenuItem
import br.com.fitnesspro.compose.components.fields.state.DatePickerTextField
import br.com.fitnesspro.compose.components.fields.state.DropDownTextField
import br.com.fitnesspro.compose.components.fields.state.TabState
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.compose.components.tabs.Tab
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.callback.showInformationDialog
import br.com.fitnesspro.core.enums.EnumDateTimePatterns.DATE_ONLY_NUMBERS
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.core.extensions.parseToLocalDate
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.to.TOPerson
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RegisterUserViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val savePersonUseCase: SavePersonUseCase,
    private val personRepository: PersonRepository,
    private val academyRepository: AcademyRepository,
    private val globalEvents: GlobalEvents,
    savedStateHandle: SavedStateHandle
) : FitnessProViewModel() {

    private val _uiState: MutableStateFlow<RegisterUserUIState> = MutableStateFlow(RegisterUserUIState())
    val uiState get() = _uiState.asStateFlow()

    private val jsonArgs: String? = savedStateHandle[registerUserArguments]

    init {
        jsonArgs?.fromJsonNavParamToArgs(RegisterUserScreenArgs::class.java)?.let { args ->
            initialLoadUIState(args)
            loadUIStateWithAuthenticatedPerson(args)
            loadUIStateWithPersonAuthService(args)

            showDialogRegisterUserAuthenticatedWithService(args)
        }
    }

    override fun getGlobalEventsBus(): GlobalEvents = globalEvents

    override fun getErrorMessageFrom(throwable: Throwable): String {
        return when (throwable) {
            is FirebaseAuthUserCollisionException -> {
                context.getString(R.string.firebase_user_collision_error_message)
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

    private fun initialLoadUIState(args: RegisterUserScreenArgs) {
        _uiState.update { currentState ->
            currentState.copy(
                title = getTitle(context = args.context, toPersonAuthenticated = null),
                context = args.context,
                tabState = TabState(
                    tabs = getTabsWithDefaultState(),
                    onSelectTab = { selectedTab ->
                        _uiState.value = _uiState.value.copy(
                            tabState = _uiState.value.tabState.copy(
                                tabs = getTabListWithSelectedTab(selectedTab)
                            )
                        )
                    }
                ),
                isVisibleFieldPhone = isVisibleFieldPhone(context = args.context, toPerson = null),
                name = initializeNameTextField(),
                email = initializeEmailTextField(),
                password = initializePasswordTextField(),
                birthDate = initializeBirthDatePickerField(),
                phone = initializePhoneTextField(),
                userType = initializeUserTypeDropDownMenu(),
                messageDialogState = initializeMessageDialogState(),
                onToggleLoading = {
                    _uiState.value = _uiState.value.copy(
                        showLoading = _uiState.value.showLoading.not()
                    )
                }
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

    private fun initializeUserTypeDropDownMenu(): DropDownTextField<EnumUserType?> {
        val items = EnumUserType.entries.map {
            MenuItem<EnumUserType?>(
                value = it,
                label = it.getLabel(context)!!
            )
        }

        return DropDownTextField(
            dataList = items,
            dataListFiltered = items.toMutableList(),
            onDropDownDismissRequest = {
                _uiState.value = _uiState.value.copy(
                    userType = _uiState.value.userType.copy(expanded = false)
                )
            },
            onDropDownExpandedChange = {
                _uiState.value = _uiState.value.copy(
                    userType = _uiState.value.userType.copy(expanded = it)
                )
            },
            onDataListItemClick = {
                _uiState.value = _uiState.value.copy(
                    userType = _uiState.value.userType.copy(
                        value = it.value?.getLabel(context)!!,
                        expanded = false,
                        errorMessage = ""
                    ),
                    toPerson = _uiState.value.toPerson.copy(
                        user = _uiState.value.toPerson.user?.copy(type = it.value)
                    )
                )
            },
            onChange = {
                // TODO - Talvez o filtro
            }
        )
    }

    private fun loadUIStateWithAuthenticatedPerson(args: RegisterUserScreenArgs) = launch {
        if (args.context == null && args.toPersonAuthService == null) {
            val authenticatedTOPerson = personRepository.getAuthenticatedTOPerson()
            authenticatedTOPerson?.user?.password = null

            authenticatedTOPerson?.let { toPerson ->
                _uiState.update {
                    it.copy(
                        title = getTitle(context = null, toPersonAuthenticated = toPerson),
                        subtitle = toPerson.name!!,
                        toPerson = toPerson,
                        academies = getAcademiesFromAuthenticatedPerson(toPerson.id!!),
                        isVisibleFieldPhone = isVisibleFieldPhone(context = null, toPerson = toPerson),
                        name = it.name.copy(value = toPerson.name!!),
                        email = it.email.copy(value = toPerson.user?.email!!),
                        birthDate = it.birthDate.copy(value = toPerson.birthDate?.format(DATE_ONLY_NUMBERS) ?: ""),
                        phone = it.phone.copy(value = toPerson.phone ?: ""),
                        tabState = it.tabState.copy(tabs = getTabListAllEnabled())
                    )
                }
            }
        }
    }

    private fun loadUIStateWithPersonAuthService(args: RegisterUserScreenArgs) {
        args.toPersonAuthService?.let {  toPerson ->
            _uiState.update {
                it.copy(
                    title = getTitle(authenticationService = true),
                    toPerson = toPerson,
                    isVisibleFieldPhone = isVisibleFieldPhone(context = null, toPerson = toPerson),
                    name = it.name.copy(value = toPerson.name!!),
                    email = it.email.copy(value = toPerson.user?.email!!),
                    phone = it.phone.copy(value = toPerson.phone ?: ""),
                    isRegisterServiceAuth = true
                )
            }
        }
    }

    private fun showDialogRegisterUserAuthenticatedWithService(args: RegisterUserScreenArgs) {
        if (args.toPersonAuthService != null) {
            _uiState.value.messageDialogState.onShowDialog?.showInformationDialog(
                message = context.getString(R.string.register_user_screen_message_register_user_authenticated_with_service)
            )
        }
    }

    private suspend fun getAcademiesFromAuthenticatedPerson(personId: String): List<AcademyGroupDecorator> {
        return academyRepository.getAcademiesFromPerson(personId)
    }

    fun updateAcademies() {
        launch {
            _uiState.value.toPerson.id?.let { personId ->
                _uiState.value = _uiState.value.copy(
                    academies = getAcademiesFromAuthenticatedPerson(personId)
                )
            }
        }
    }

    private fun initializePhoneTextField(): TextField {
        return TextField(onChange = {
            _uiState.value = _uiState.value.copy(
                phone = _uiState.value.phone.copy(
                    value = it,
                    errorMessage = ""
                ),
                toPerson = _uiState.value.toPerson.copy(phone = it)
            )
        })
    }

    private fun initializeBirthDatePickerField(): DatePickerTextField {
        return DatePickerTextField(
            onDatePickerOpenChange = { newOpen ->
                _uiState.value = _uiState.value.copy(
                    birthDate = _uiState.value.birthDate.copy(datePickerOpen = newOpen)
                )
            },
            onDateChange = { newDate ->
                _uiState.value = _uiState.value.copy(
                    birthDate = _uiState.value.birthDate.copy(
                        value = newDate.format(DATE_ONLY_NUMBERS),
                        errorMessage = ""
                    ),
                )

                _uiState.value.birthDate.onDatePickerDismiss()
            },
            onDatePickerDismiss = {
                _uiState.value = _uiState.value.copy(
                    birthDate = _uiState.value.birthDate.copy(datePickerOpen = false)
                )
            },
            onChange = { text ->
                if (text.isDigitsOnly()) {
                    _uiState.value = _uiState.value.copy(
                        birthDate = _uiState.value.birthDate.copy(
                            value = text,
                            errorMessage = ""
                        ),
                        toPerson = _uiState.value.toPerson.copy(
                            birthDate = text.parseToLocalDate(DATE_ONLY_NUMBERS)
                        )
                    )
                }
            }
        )
    }

    private fun initializePasswordTextField(): TextField {
        return TextField(onChange = {
            _uiState.value = _uiState.value.copy(
                password = _uiState.value.password.copy(
                    value = it,
                    errorMessage = ""
                ),
                toPerson = _uiState.value.toPerson.copy(
                    user = _uiState.value.toPerson.user?.copy(password = it)
                )
            )
        })
    }

    private fun initializeEmailTextField(): TextField {
        return TextField(onChange = {
            _uiState.value = _uiState.value.copy(
                email = _uiState.value.email.copy(
                    value = it,
                    errorMessage = ""
                ),
                toPerson = _uiState.value.toPerson.copy(
                    user = _uiState.value.toPerson.user?.copy(email = it)
                )
            )
        })
    }

    private fun initializeNameTextField(): TextField {
        return TextField(onChange = {
            _uiState.value = _uiState.value.copy(
                name = _uiState.value.name.copy(
                    value = it,
                    errorMessage = ""
                ),
                toPerson = _uiState.value.toPerson.copy(name = it)
            )
        })
    }

    private fun isVisibleFieldPhone(
        context: EnumOptionsBottomSheetRegisterUser?,
        toPerson: TOPerson?
    ): Boolean {
        return if (context != null) {
            context in listOf(
                EnumOptionsBottomSheetRegisterUser.NUTRITIONIST,
                EnumOptionsBottomSheetRegisterUser.PERSONAL_TRAINER
            )
        } else {
            toPerson?.user?.type in listOf(
                EnumUserType.NUTRITIONIST,
                EnumUserType.PERSONAL_TRAINER
            )
        }
    }

    /**
     * Função utilizada para recuperar o titulo que deve ser exibido na barra superior.
     */
    private fun getTitle(
        context: EnumOptionsBottomSheetRegisterUser? = null,
        toPersonAuthenticated: TOPerson? = null,
        authenticationService: Boolean = false
    ): String {
        return when {
            toPersonAuthenticated != null -> {
                when(toPersonAuthenticated.user?.type!!) {
                    EnumUserType.ACADEMY_MEMBER -> this.context.getString(R.string.register_user_screen_title_academy_member)
                    EnumUserType.PERSONAL_TRAINER -> this.context.getString(R.string.register_user_screen_title_personal_trainer)
                    EnumUserType.NUTRITIONIST -> this.context.getString(R.string.register_user_screen_title_nutritionist)
                    else -> ""
                }
            }

            context != null -> {
                when (context) {
                    EnumOptionsBottomSheetRegisterUser.ACADEMY_MEMBER -> this.context.getString(R.string.register_user_screen_title_new_academy_member)
                    EnumOptionsBottomSheetRegisterUser.PERSONAL_TRAINER -> this.context.getString(R.string.register_user_screen_title_new_personal_trainer)
                    EnumOptionsBottomSheetRegisterUser.NUTRITIONIST -> this.context.getString(R.string.register_user_screen_title_new_nutritionist)
                }
            }

            authenticationService -> {
                this.context.getString(R.string.register_user_screen_title_authentication_service)
            }

            else -> ""
        }
    }

    fun saveUser(onSuccess: () -> Unit) {
        launch {
            val toPerson = _uiState.value.toPerson
            toPerson.user!!.type = getUserTypeFromContext(_uiState.value.context)

            val validationResults = savePersonUseCase.execute(toPerson, _uiState.value.isRegisterServiceAuth)

            if (validationResults.isEmpty()) {
                updateInfosAfterSave()
                onSuccess()
            } else {
                _uiState.value.onToggleLoading()
                showValidationMessages(validationResults)
            }
        }
    }

    private fun updateInfosAfterSave() {
        val toPerson = _uiState.value.toPerson

        _uiState.update {
            it.copy(
                title = getTitle(context = _uiState.value.context, toPersonAuthenticated = toPerson),
                subtitle = toPerson.name!!,
                toPerson = toPerson,
                tabState = _uiState.value.tabState.copy(
                    tabs = getTabListAllEnabled()
                ),
            )
        }
    }

    private fun getTabsWithDefaultState(): MutableList<Tab> {
        return mutableListOf(
            Tab(
                enum = GENERAL,
                selected = true,
                enabled = true
            ),
            Tab(
                enum = ACADEMY,
                selected = false,
                enabled = false
            )
        )
    }

    private fun getTabListWithSelectedTab(selectedTab: Tab): MutableList<Tab> {
        return _uiState.value.tabState.tabs.map { tab ->
            tab.copy(selected = tab.enum == selectedTab.enum)
        }.toMutableList()
    }

    private fun getTabListAllEnabled(): MutableList<Tab> {
        return _uiState.value.tabState.tabs.map { tab ->
            tab.copy(enabled = true)
        }.toMutableList()
    }

    private fun showValidationMessages(validationResults: List<FieldValidationError<EnumValidatedPersonFields>>) {
        validationResults.forEach {
            when (it.field) {
                EnumValidatedPersonFields.NAME -> {
                    _uiState.value = _uiState.value.copy(
                        name = _uiState.value.name.copy(errorMessage = it.message)
                    )
                }

                EnumValidatedPersonFields.EMAIL -> {
                    _uiState.value = _uiState.value.copy(
                        email = _uiState.value.email.copy(errorMessage = it.message)
                    )
                }

                EnumValidatedPersonFields.PASSWORD -> {
                    _uiState.value = _uiState.value.copy(
                        password = _uiState.value.password.copy(errorMessage = it.message)
                    )
                }

                EnumValidatedPersonFields.BIRTH_DATE -> {
                    _uiState.value = _uiState.value.copy(
                        birthDate = _uiState.value.birthDate.copy(errorMessage = it.message)
                    )
                }

                EnumValidatedPersonFields.PHONE -> {
                    _uiState.value = _uiState.value.copy(
                        phone = _uiState.value.phone.copy(errorMessage = it.message)
                    )
                }
            }
        }
    }

    private fun getUserTypeFromContext(context: EnumOptionsBottomSheetRegisterUser?): EnumUserType {
        return when (context) {
            EnumOptionsBottomSheetRegisterUser.ACADEMY_MEMBER -> EnumUserType.ACADEMY_MEMBER
            EnumOptionsBottomSheetRegisterUser.PERSONAL_TRAINER -> EnumUserType.PERSONAL_TRAINER
            EnumOptionsBottomSheetRegisterUser.NUTRITIONIST -> EnumUserType.NUTRITIONIST
            null -> _uiState.value.toPerson.user?.type!!
        }
    }
}