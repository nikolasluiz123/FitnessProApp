package br.com.fitnesspro.common.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import br.com.android.ui.compose.components.dialog.message.showErrorDialog
import br.com.android.ui.compose.components.dialog.message.showInformationDialog
import br.com.android.ui.compose.components.fields.dropdown.MenuItem
import br.com.android.ui.compose.components.fields.validation.FieldValidationError
import br.com.android.ui.compose.components.tabs.state.Tab
import br.com.core.utils.enums.EnumDateTimePatterns
import br.com.core.utils.extensions.format
import br.com.core.utils.extensions.fromJsonNavParamToArgs
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
import br.com.fitnesspro.common.ui.viewmodel.base.FitnessProStatefulViewModel
import br.com.fitnesspro.common.usecase.person.EnumValidatedPersonFields
import br.com.fitnesspro.common.usecase.person.SavePersonUseCase
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
    @param:ApplicationContext private val context: Context,
    private val savePersonUseCase: SavePersonUseCase,
    private val personRepository: PersonRepository,
    private val academyRepository: AcademyRepository,
    private val globalEvents: GlobalEvents,
    savedStateHandle: SavedStateHandle
) : FitnessProStatefulViewModel() {

    private val _uiState: MutableStateFlow<RegisterUserUIState> = MutableStateFlow(RegisterUserUIState())
    val uiState get() = _uiState.asStateFlow()

    private val jsonArgs: String? = savedStateHandle[registerUserArguments]

    init {
        initialLoadUIState()
        loadUIStateWithPersonAuthService()
        showDialogRegisterUserAuthenticatedWithService()
    }

    override fun initialLoadUIState() {
        val args = jsonArgs?.fromJsonNavParamToArgs(RegisterUserScreenArgs::class.java)!!

        _uiState.value = _uiState.value.copy(
            title = getTitle(context = args.context, toPersonAuthenticated = null),
            context = args.context,
            isVisibleFieldPhone = isVisibleFieldPhone(context = args.context, toPerson = null),
            tabState = createTabState(
                getCurrentState = { _uiState.value.tabState },
                updateState = { _uiState.value = _uiState.value.copy(tabState = it) },
                tabs = getTabsWithDefaultState()
            ),
            name = createTextFieldState(
                getCurrentState = { _uiState.value.name },
                updateState = {
                    _uiState.value = _uiState.value.copy(
                        name = it,
                        toPerson = _uiState.value.toPerson.copy(name = it.value)
                    )
                }
            ),
            email = createTextFieldState(
                getCurrentState = { _uiState.value.email },
                updateState = {
                    _uiState.value = _uiState.value.copy(
                        email = it,
                        toPerson = _uiState.value.toPerson.copy(user = _uiState.value.toPerson.user?.copy(email = it.value))
                    )
                }
            ),
            password = createTextFieldState(
                getCurrentState = { _uiState.value.password },
                updateState = {
                    _uiState.value = _uiState.value.copy(
                        password = it,
                        toPerson = _uiState.value.toPerson.copy(user = _uiState.value.toPerson.user?.copy(password = it.value))
                    )
                }
            ),
            birthDate = createDatePickerFieldState(
                getCurrentState = { _uiState.value.birthDate },
                updateState = { _uiState.value = _uiState.value.copy(birthDate = it) },
                onDateChange = {
                    _uiState.value = _uiState.value.copy(
                        toPerson = _uiState.value.toPerson.copy(birthDate = it)
                    )
                }
            ),
            phone = createTextFieldState(
                getCurrentState = { _uiState.value.phone },
                updateState = {
                    _uiState.value = _uiState.value.copy(
                        phone = it,
                        toPerson = _uiState.value.toPerson.copy(phone = it.value)
                    )
                }
            ),
            userType = createDropDownTextFieldState(
                items = getMenuItemsUserType(),
                getCurrentState = { _uiState.value.userType },
                updateState = { _uiState.value = _uiState.value.copy(userType = it) },
                onItemClick = {
                    _uiState.value = _uiState.value.copy(
                        toPerson = _uiState.value.toPerson.copy(user = _uiState.value.toPerson.user?.copy(type = it))
                    )
                }
            ),
            messageDialogState = createMessageDialogState(
                getCurrentState = { _uiState.value.messageDialogState },
                updateState = { _uiState.value = _uiState.value.copy(messageDialogState = it) }
            ),
            onToggleLoading = {
                _uiState.value = _uiState.value.copy(
                    showLoading = _uiState.value.showLoading.not()
                )
            }
        )
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

    private fun getMenuItemsUserType(): List<MenuItem<EnumUserType?>> = EnumUserType.entries.map {
        MenuItem(
            value = it,
            label = it.getLabel(context)!!
        )
    }

    fun loadUIStateWithAuthenticatedPerson() {
        val args = jsonArgs?.fromJsonNavParamToArgs(RegisterUserScreenArgs::class.java)!!

        if (args.context == null && args.toPersonAuthService == null) {
            launch {
                val authenticatedTOPerson = personRepository.getAuthenticatedTOPerson()
                authenticatedTOPerson?.user?.password = null

                authenticatedTOPerson?.let { toPerson ->
                    _uiState.value = _uiState.value.copy(
                        title = getTitle(context = null, toPersonAuthenticated = toPerson),
                        subtitle = toPerson.name!!,
                        toPerson = toPerson,
                        academies = getAcademiesFromAuthenticatedPerson(toPerson.id!!),
                        isVisibleFieldPhone = isVisibleFieldPhone(
                            context = null,
                            toPerson = toPerson
                        ),
                        name = _uiState.value.name.copy(value = toPerson.name!!),
                        email = _uiState.value.email.copy(value = toPerson.user?.email!!),
                        birthDate = _uiState.value.birthDate.copy(
                            value = toPerson.birthDate?.format(EnumDateTimePatterns.DATE_ONLY_NUMBERS) ?: ""
                        ),
                        phone = _uiState.value.phone.copy(value = toPerson.phone ?: ""),
                        tabState = _uiState.value.tabState.copy(tabs = getTabListAllEnabled()),
                        executeLoad = false
                    )
                }
            }
        }
    }

    private fun loadUIStateWithPersonAuthService() {
        val args = jsonArgs?.fromJsonNavParamToArgs(RegisterUserScreenArgs::class.java)!!

        args.toPersonAuthService?.let {  toPerson ->
            _uiState.value = _uiState.value.copy(
                title = getTitle(authenticationService = true),
                toPerson = toPerson,
                isVisibleFieldPhone = isVisibleFieldPhone(context = null, toPerson = toPerson),
                name = _uiState.value.name.copy(value = toPerson.name!!),
                email = _uiState.value.email.copy(value = toPerson.user?.email!!),
                phone = _uiState.value.phone.copy(value = toPerson.phone ?: ""),
                isRegisterServiceAuth = true
            )
        }
    }

    private fun showDialogRegisterUserAuthenticatedWithService() {
        val args = jsonArgs?.fromJsonNavParamToArgs(RegisterUserScreenArgs::class.java)!!

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