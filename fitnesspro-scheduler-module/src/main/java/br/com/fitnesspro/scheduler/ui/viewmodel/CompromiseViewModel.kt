package br.com.fitnesspro.scheduler.ui.viewmodel

import android.content.Context
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.FitnessProViewModel
import br.com.fitnesspro.compose.components.fields.state.DatePickerTextField
import br.com.fitnesspro.compose.components.fields.state.DayWeeksSelectorField
import br.com.fitnesspro.compose.components.fields.state.PagedDialogListState
import br.com.fitnesspro.compose.components.fields.state.PagedDialogListTextField
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.compose.components.fields.state.TimePickerTextField
import br.com.fitnesspro.core.callback.showConfirmationDialog
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.enums.EnumDateTimePatterns.DATE
import br.com.fitnesspro.core.enums.EnumDateTimePatterns.DATE_ONLY_NUMBERS
import br.com.fitnesspro.core.enums.EnumDateTimePatterns.TIME
import br.com.fitnesspro.core.enums.EnumDateTimePatterns.TIME_ONLY_NUMBERS
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.core.extensions.getOffsetDateTime
import br.com.fitnesspro.core.extensions.parseTimeToOffsetDateTime
import br.com.fitnesspro.core.extensions.parseToLocalDate
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.firebase.api.firestore.repository.FirestoreChatRepository
import br.com.fitnesspro.model.enums.EnumCompromiseType.FIRST
import br.com.fitnesspro.model.enums.EnumCompromiseType.RECURRENT
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.repository.SchedulerRepository
import br.com.fitnesspro.scheduler.ui.navigation.ChatArgs
import br.com.fitnesspro.scheduler.ui.navigation.CompromiseScreenArgs
import br.com.fitnesspro.scheduler.ui.navigation.compromiseArguments
import br.com.fitnesspro.scheduler.ui.state.CompromiseUIState
import br.com.fitnesspro.scheduler.usecase.scheduler.CancelSchedulerUseCase
import br.com.fitnesspro.scheduler.usecase.scheduler.ConfirmationSchedulerUseCase
import br.com.fitnesspro.scheduler.usecase.scheduler.SaveCompromiseUseCase
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumCompromiseValidationTypes
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumSchedulerType
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields.DATE_END
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields.DATE_START
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields.DAY_OF_WEEKS
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields.HOUR_END
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields.HOUR_START
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields.MEMBER
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields.OBSERVATION
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumValidatedCompromiseFields.PROFESSIONAL
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOScheduler
import br.com.fitnesspro.tuple.PersonTuple
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CompromiseViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val personRepository: PersonRepository,
    private val schedulerRepository: SchedulerRepository,
    private val saveCompromiseUseCase: SaveCompromiseUseCase,
    private val confirmationSchedulerUseCase: ConfirmationSchedulerUseCase,
    private val cancelSchedulerUseCase: CancelSchedulerUseCase,
    private val globalEvents: GlobalEvents,
    private val chatRepository: FirestoreChatRepository,
    savedStateHandle: SavedStateHandle
) : FitnessProViewModel() {

    private val _uiState: MutableStateFlow<CompromiseUIState> = MutableStateFlow(CompromiseUIState())
    val uiState get() = _uiState.asStateFlow()

    private val jsonArgs: String? = savedStateHandle[compromiseArguments]

    init {
        initialUIStateLoad()
        loadUIStateWithDatabaseInfos()
    }

    override fun getGlobalEventsBus(): GlobalEvents = globalEvents

    override fun getErrorMessageFrom(throwable: Throwable): String {
        return context.getString(br.com.fitnesspro.common.R.string.unknown_error_message)
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

    private fun initialUIStateLoad() {
        val args = jsonArgs?.fromJsonNavParamToArgs(CompromiseScreenArgs::class.java)!!

        _uiState.update {
            it.copy(
                subtitle = getSubtitle(args.date),
                professional = initializeProfessionalPagedDialogListField(args),
                member = initializeMemberPagedDialogListField(args),
                dateStart = initializeDateStartDatePickerField(),
                dateEnd = initializeDateEndDatePickerField(),
                hourStart = initializeHourStartTimePickerField(),
                hourEnd = initializeHourEndTimePickerField(),
                observation = initializeObservationField(),
                messageDialogState = initializeMessageDialogState(),
                dayWeeksSelectorField = initializeDayWeeksSelectorField(),
                recurrent = args.recurrent,
                onToggleLoading = {
                    _uiState.value = _uiState.value.copy(
                        showLoading = _uiState.value.showLoading.not()
                    )
                }
            )
        }
    }

    private fun initializeDayWeeksSelectorField(): DayWeeksSelectorField {
        return DayWeeksSelectorField(
            onSelect = {
                _uiState.value = _uiState.value.copy(
                    recurrentConfig = _uiState.value.recurrentConfig.copy(
                        dayWeeks = _uiState.value.dayWeeksSelectorField.selected.toList()
                    )
                )
            }
        )
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

    private fun initializeObservationField(): TextField {
        return TextField(
            onChange = { text ->
                _uiState.value = _uiState.value.copy(
                    observation = _uiState.value.observation.copy(
                        value = text,
                        errorMessage = ""
                    ),
                    toScheduler = _uiState.value.toScheduler.copy(observation = text.ifEmpty { null })
                )
            }
        )
    }

    private fun initializeHourEndTimePickerField(): TimePickerTextField {
        val args = jsonArgs?.fromJsonNavParamToArgs(CompromiseScreenArgs::class.java)!!

        return TimePickerTextField(
            onTimePickerOpenChange = { newOpen ->
                _uiState.value = _uiState.value.copy(
                    hourEnd = _uiState.value.hourEnd.copy(timePickerOpen = newOpen)
                )
            },
            onTimeChange = { newTime ->
                _uiState.value = _uiState.value.copy(
                    hourEnd = _uiState.value.hourEnd.copy(
                        value = newTime.format(TIME_ONLY_NUMBERS),
                        errorMessage = ""
                    ),
                    toScheduler = _uiState.value.toScheduler.copy(
                        dateTimeEnd = args.date!!.getOffsetDateTime(newTime)
                    )
                )
            },
            onTimeDismiss = {
                _uiState.value = _uiState.value.copy(
                    hourEnd = _uiState.value.hourEnd.copy(timePickerOpen = false)
                )
            },
            onChange = { text ->
                if (text.isDigitsOnly()) {
                    _uiState.value = _uiState.value.copy(
                        hourEnd = _uiState.value.hourEnd.copy(
                            value = text,
                            errorMessage = ""
                        ),
                        toScheduler = _uiState.value.toScheduler.copy(
                            dateTimeEnd = text.parseTimeToOffsetDateTime(args.date!!, TIME_ONLY_NUMBERS)
                        )
                    )
                }
            }
        )
    }

    private fun initializeHourStartTimePickerField(): TimePickerTextField {
        val args = jsonArgs?.fromJsonNavParamToArgs(CompromiseScreenArgs::class.java)!!

        return TimePickerTextField(
            onTimePickerOpenChange = { newOpen ->
                _uiState.value = _uiState.value.copy(
                    hourStart = _uiState.value.hourStart.copy(timePickerOpen = newOpen)
                )
            },
            onTimeChange = { newTime ->
                _uiState.value = _uiState.value.copy(
                    hourStart = _uiState.value.hourStart.copy(
                        value = newTime.format(TIME_ONLY_NUMBERS),
                        errorMessage = ""
                    ),
                    toScheduler = _uiState.value.toScheduler.copy(dateTimeStart = args.date!!.getOffsetDateTime(newTime))
                )
            },
            onTimeDismiss = {
                _uiState.value = _uiState.value.copy(
                    hourStart = _uiState.value.hourStart.copy(timePickerOpen = false)
                )
            },
            onChange = { text ->
                if (text.isDigitsOnly()) {
                    _uiState.value = _uiState.value.copy(
                        hourStart = _uiState.value.hourStart.copy(
                            value = text,
                            errorMessage = ""
                        ),
                        toScheduler = _uiState.value.toScheduler.copy(
                            dateTimeStart = text.parseTimeToOffsetDateTime(args.date!!, TIME_ONLY_NUMBERS)
                        )
                    )
                }
            }
        )
    }

    private fun initializeDateEndDatePickerField(): DatePickerTextField {
        return DatePickerTextField(
            onDatePickerOpenChange = { newOpen ->
                _uiState.value = _uiState.value.copy(
                    dateEnd = _uiState.value.dateEnd.copy(datePickerOpen = newOpen)
                )
            },
            onDateChange = { newDate ->
                _uiState.value = _uiState.value.copy(
                    dateEnd = _uiState.value.dateEnd.copy(
                        value = newDate.format(DATE_ONLY_NUMBERS),
                        errorMessage = ""
                    )
                )

                _uiState.value.dateEnd.onDatePickerDismiss()
            },
            onDatePickerDismiss = {
                _uiState.value = _uiState.value.copy(
                    dateEnd = _uiState.value.dateEnd.copy(datePickerOpen = false)
                )
            },
            onChange = { text ->
                if (text.isDigitsOnly()) {
                    _uiState.value = _uiState.value.copy(
                        dateEnd = _uiState.value.dateEnd.copy(
                            value = text,
                            errorMessage = ""
                        ),
                        recurrentConfig = _uiState.value.recurrentConfig.copy(
                            dateEnd = text.parseToLocalDate(DATE_ONLY_NUMBERS)
                        )
                    )
                }
            },
        )
    }

    private fun initializeDateStartDatePickerField(): DatePickerTextField {
        return DatePickerTextField(
            onDatePickerOpenChange = { newOpen ->
                _uiState.value = _uiState.value.copy(
                    dateStart = _uiState.value.dateStart.copy(datePickerOpen = newOpen)
                )
            },
            onDateChange = { newDate ->
                _uiState.value = _uiState.value.copy(
                    dateStart = _uiState.value.dateStart.copy(
                        value = newDate.format(DATE_ONLY_NUMBERS),
                        errorMessage = ""
                    ),
                )

                _uiState.value.dateStart.onDatePickerDismiss()
            },
            onDatePickerDismiss = {
                _uiState.value = _uiState.value.copy(
                    dateStart = _uiState.value.dateStart.copy(datePickerOpen = false)
                )
            },
            onChange = { text ->
                if (text.isDigitsOnly()) {
                    _uiState.value = _uiState.value.copy(
                        dateStart = _uiState.value.dateStart.copy(
                            value = text,
                            errorMessage = ""
                        ),
                        recurrentConfig = _uiState.value.recurrentConfig.copy(
                            dateStart = text.parseToLocalDate(DATE_ONLY_NUMBERS)
                        )
                    )
                }
            }
        )
    }

    private fun initializeMemberPagedDialogListField(args: CompromiseScreenArgs): PagedDialogListTextField<PersonTuple> {
        return PagedDialogListTextField(
            dialogListState = PagedDialogListState(
                dialogTitle = context.getString(R.string.compromise_screen_label_member_list),
                onShow = {
                    _uiState.value = _uiState.value.copy(
                        member = _uiState.value.member.copy(
                            dialogListState = _uiState.value.member.dialogListState.copy(show = true)
                        )
                    )
                },
                onHide = {
                    _uiState.value = _uiState.value.copy(
                        member = _uiState.value.member.copy(
                            dialogListState = _uiState.value.member.dialogListState.copy(show = false)
                        )
                    )
                },
                onDataListItemClick = { item ->
                    _uiState.value = _uiState.value.copy(
                        member = _uiState.value.member.copy(
                            value = item.getLabel(),
                            errorMessage = ""
                        ),
                        toScheduler = _uiState.value.toScheduler.copy(
                            academyMemberName = item.getLabel(),
                            academyMemberPersonId = item.id,
                        )
                    )

                    _uiState.value.member.dialogListState.onHide()
                },
                onSimpleFilterChange = { filter ->
                    _uiState.value = _uiState.value.copy(
                        member = _uiState.value.member.copy(
                            dialogListState = _uiState.value.member.dialogListState.copy(
                                dataList = getListMembers(
                                    authenticatedPerson = _uiState.value.authenticatedPerson,
                                    simpleFilter = filter,
                                    args = args
                                )
                            )
                        )
                    )
                },
            ),
            onChange = { newText ->
                _uiState.value = _uiState.value.copy(
                    member = _uiState.value.member.copy(
                        value = newText,
                    )
                )
            }
        )
    }

    private fun initializeProfessionalPagedDialogListField(args: CompromiseScreenArgs): PagedDialogListTextField<PersonTuple> {
        return PagedDialogListTextField(
            dialogListState = PagedDialogListState(
                dialogTitle = context.getString(R.string.compromise_screen_label_professional_list),
                onShow = {
                    _uiState.value = _uiState.value.copy(
                        professional = _uiState.value.professional.copy(
                            dialogListState = _uiState.value.professional.dialogListState.copy(show = true)
                        )
                    )
                },
                onHide = {
                    _uiState.value = _uiState.value.copy(
                        professional = _uiState.value.professional.copy(
                            dialogListState = _uiState.value.professional.dialogListState.copy(show = false)
                        )
                    )
                },
                onDataListItemClick = { item ->
                    _uiState.value = _uiState.value.copy(
                        professional = _uiState.value.professional.copy(
                            value = item.getLabel(),
                            errorMessage = ""
                        ),
                        toScheduler = _uiState.value.toScheduler.copy(
                            professionalName = item.getLabel(),
                            professionalPersonId = item.id,
                            professionalType = item.userType
                        )
                    )

                    _uiState.value.professional.dialogListState.onHide()
                },
                onSimpleFilterChange = { filter ->
                    _uiState.value = _uiState.value.copy(
                        professional = _uiState.value.professional.copy(
                            dialogListState = _uiState.value.professional.dialogListState.copy(
                                dataList = getListProfessional(
                                    authenticatedPerson = _uiState.value.authenticatedPerson,
                                    simpleFilter = filter,
                                    args = args
                                )
                            )
                        )
                    )
                },
            ),
            onChange = { newText ->
                _uiState.value = _uiState.value.copy(
                    professional = _uiState.value.professional.copy(
                        value = newText,
                    )
                )
            }
        )
    }

    private fun loadUIStateWithDatabaseInfos() {
        launch {
            val toPerson = personRepository.getAuthenticatedTOPerson()!!
            val userType = toPerson.user?.type!!
            val args = jsonArgs?.fromJsonNavParamToArgs(CompromiseScreenArgs::class.java)!!
            val menuItemListProfessional = getListProfessional(authenticatedPerson = toPerson, args = args)
            val menuItemListMembers = getListMembers(authenticatedPerson = toPerson, args = args)

            _uiState.update { state ->
                state.copy(
                    title = getTitle(
                        userType = userType,
                        recurrent = args.recurrent,
                    ),
                    userType = userType,
                    professional = _uiState.value.professional.copy(
                        dialogListState = _uiState.value.professional.dialogListState.copy(
                            dataList = menuItemListProfessional
                        )
                    ),
                    member = _uiState.value.member.copy(
                        dialogListState = _uiState.value.member.dialogListState.copy(
                            dataList = menuItemListMembers
                        )
                    ),
                    toScheduler = getToSchedulerWithDefaultInfos(toPerson, args),
                    authenticatedPerson = toPerson
                )
            }

            initializeEditionInfos()
        }
    }

    private fun getToSchedulerWithDefaultInfos(
        authenticatedPerson: TOPerson,
        args: CompromiseScreenArgs
    ): TOScheduler {
        return when (authenticatedPerson.user?.type!!) {
            EnumUserType.PERSONAL_TRAINER, EnumUserType.NUTRITIONIST -> {
                _uiState.value.toScheduler.copy(
                    professionalPersonId = authenticatedPerson.id,
                    situation = EnumSchedulerSituation.SCHEDULED,
                    compromiseType = if (args.recurrent) RECURRENT else FIRST
                )
            }

            EnumUserType.ACADEMY_MEMBER -> {
                _uiState.value.toScheduler.copy(
                    academyMemberPersonId = authenticatedPerson.id,
                    situation = EnumSchedulerSituation.SCHEDULED,
                    compromiseType = if (args.recurrent) RECURRENT else FIRST
                )
            }
        }
    }

    private suspend fun initializeEditionInfos() {
        val args = jsonArgs?.fromJsonNavParamToArgs(CompromiseScreenArgs::class.java)!!
        val id = args.schedulerId ?: _uiState.value.toScheduler.id
        val toScheduler = id?.let { schedulerRepository.getTOSchedulerById(it) }

        toScheduler?.let { to ->
            _uiState.update { state ->
                state.copy(
                    title = getTitle(
                        userType = _uiState.value.userType!!,
                        recurrent = args.recurrent,
                        toScheduler = to
                    ),
                    subtitle = getSubtitle(to),
                    professional = _uiState.value.professional.copy(
                        value = to.professionalName!!,
                        errorMessage = ""
                    ),
                    member = _uiState.value.member.copy(
                        value = to.academyMemberName!!,
                        errorMessage = ""
                    ),
                    hourStart = _uiState.value.hourStart.copy(
                        value = to.dateTimeStart!!.format(TIME_ONLY_NUMBERS)
                    ),
                    hourEnd = _uiState.value.hourEnd.copy(
                        value = to.dateTimeEnd!!.format(TIME_ONLY_NUMBERS)
                    ),
                    observation = _uiState.value.observation.copy(
                        value = to.observation ?: ""
                    ),
                    toScheduler = to,
                    isEnabledMessageButton = true,
                    isEnabledDeleteButton = true,
                    isEnabledConfirmButton = true
                )
            }
        }
    }

    private fun getListProfessional(
        authenticatedPerson: TOPerson,
        simpleFilter: String = "",
        args: CompromiseScreenArgs
    ): Flow<PagingData<PersonTuple>> {
        val types = listOf(EnumUserType.PERSONAL_TRAINER, EnumUserType.NUTRITIONIST)

        return personRepository.getListTOPersonWithUserType(
            types = types,
            simpleFilter = simpleFilter,
            schedulerDate = args.date,
            personsForSchedule = true,
            authenticatedPersonId = authenticatedPerson.id!!
        ).flow
    }

    private fun getListMembers(
        authenticatedPerson: TOPerson,
        simpleFilter: String = "",
        args: CompromiseScreenArgs
    ): Flow<PagingData<PersonTuple>> {
        val types = listOf(EnumUserType.ACADEMY_MEMBER)

        return personRepository.getListTOPersonWithUserType(
            types = types,
            simpleFilter = simpleFilter,
            schedulerDate = args.date,
            personsForSchedule = true,
            authenticatedPersonId = authenticatedPerson.id!!
        ).flow
    }

    private fun getTitle(
        userType: EnumUserType,
        recurrent: Boolean,
        toScheduler: TOScheduler? = null,
    ): String {
        return when (userType) {
            EnumUserType.PERSONAL_TRAINER -> {
                if (recurrent) {
                    getRecurrentCompromisseTitle(toScheduler)
                } else {
                    getUniqueCompromisseTitle(toScheduler)
                }
            }

            EnumUserType.NUTRITIONIST -> {
                getUniqueCompromisseTitle(toScheduler)
            }

            EnumUserType.ACADEMY_MEMBER -> {
                getCompromiseSuggestionTitle(toScheduler)
            }
        }
    }

    private fun getCompromiseSuggestionTitle(toScheduler: TOScheduler?): String {
        return if (toScheduler?.id != null) {
            getTitleWithSituation(toScheduler)
        } else {
            context.getString(R.string.compromise_screen_title_new_sugestion)
        }
    }

    private fun getRecurrentCompromisseTitle(toScheduler: TOScheduler?): String {
        return if (toScheduler?.id != null) {
            context.getString(R.string.compromise_screen_title_recurrent_compromise)
        } else {
            context.getString(R.string.compromise_screen_title_new_recurrent_compromise)
        }
    }

    private fun getUniqueCompromisseTitle(toScheduler: TOScheduler?): String {
        return if (toScheduler?.id != null) {
            getTitleWithSituation(toScheduler)
        } else {
            context.getString(R.string.compromise_screen_title_new_compromise)
        }
    }

    private fun getTitleWithSituation(toScheduler: TOScheduler): String {
        val situation = toScheduler.situation!!.getLabel(context)!!

        return context.getString(
            R.string.compromise_screen_title_compromise_with_situation,
            situation
        )
    }

    private fun getSubtitle(date: LocalDate?): String? {
        return date?.format(DATE)
    }

    private fun getSubtitle(toScheduler: TOScheduler): String {
        return context.getString(
            R.string.compromise_screen_subtitle,
            toScheduler.dateTimeStart!!.format(DATE),
            toScheduler.dateTimeStart!!.format(TIME),
            toScheduler.dateTimeEnd!!.format(TIME)
        )
    }

    fun saveCompromise(onSuccess: (EnumSchedulerType) -> Unit) {
        launch {
            val enumSchedulerType = getSchedulerType()

            val validationResults = saveCompromiseUseCase.execute(
                toScheduler = _uiState.value.toScheduler,
                type = enumSchedulerType,
                recurrentConfig = _uiState.value.recurrentConfig
            )

            if (validationResults.isEmpty()) {
                onSuccess(enumSchedulerType)
                initializeEditionInfos()
            } else {
                _uiState.value.onToggleLoading()
                showFieldsValidationMessages(validationResults)
            }
        }
    }

    private fun showFieldsValidationMessages(validationResults: MutableList<FieldValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>>) {
        val dialogValidations = validationResults.firstOrNull { it.field == null }

        if (dialogValidations != null) {
            _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(dialogValidations.message)
            return
        }

        validationResults.forEach {
            when (it.field!!) {
                MEMBER -> {
                    _uiState.value = _uiState.value.copy(
                        member = _uiState.value.member.copy(
                            errorMessage = it.message
                        )
                    )
                }

                PROFESSIONAL -> {
                    _uiState.value = _uiState.value.copy(
                        professional = _uiState.value.professional.copy(
                            errorMessage = it.message
                        )
                    )
                }

                DATE_START -> {
                    _uiState.value = _uiState.value.copy(
                        dateStart = _uiState.value.dateStart.copy(
                            errorMessage = it.message
                        )
                    )
                }

                DATE_END -> {
                    _uiState.value = _uiState.value.copy(
                        dateEnd = _uiState.value.dateEnd.copy(
                            errorMessage = it.message
                        )
                    )
                }

                HOUR_START -> {
                    _uiState.value = _uiState.value.copy(
                        hourStart = _uiState.value.hourStart.copy(
                            errorMessage = it.message
                        )
                    )
                }

                HOUR_END -> {
                    _uiState.value = _uiState.value.copy(
                        hourEnd = _uiState.value.hourEnd.copy(
                            errorMessage = it.message
                        )
                    )
                }

                OBSERVATION -> {
                    _uiState.value = _uiState.value.copy(
                        observation = _uiState.value.observation.copy(
                            errorMessage = it.message
                        )
                    )
                }

                DAY_OF_WEEKS -> {
                    _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(it.message)
                }
            }
        }
    }

    private fun getSchedulerType(): EnumSchedulerType {
        val state = _uiState.value

        return when {
            state.userType == EnumUserType.ACADEMY_MEMBER -> EnumSchedulerType.SUGGESTION
            state.recurrent -> EnumSchedulerType.RECURRENT
            else -> EnumSchedulerType.UNIQUE
        }
    }

    fun onCancelCompromiseClick(onSuccess: () -> Unit) {
        val state = _uiState.value
        val toScheduler = state.toScheduler

        _uiState.value.messageDialogState.onShowDialog?.showConfirmationDialog(
            message = context.getString(
                R.string.compromise_screen_dialog_inactivation_message,
                toScheduler.dateTimeStart!!.format(DATE),
                toScheduler.dateTimeStart!!.format(TIME),
                toScheduler.dateTimeEnd!!.format(TIME)
            )
        ) {
            state.onToggleLoading()

            launch {
                val validationError = cancelSchedulerUseCase(toScheduler, getSchedulerType())

                if (validationError != null) {
                    state.onToggleLoading()
                    _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(validationError.message)
                } else {
                    initializeEditionInfos()
                    onSuccess()
                }
            }
        }
    }

    fun onScheduleConfirmClick(onSuccess: () -> Unit) {
        val state = _uiState.value
        val toScheduler = state.toScheduler

        val message = if (toScheduler.situation == EnumSchedulerSituation.SCHEDULED) {
            context.getString(
                R.string.compromise_screen_message_question_confirmation,
                toScheduler.dateTimeStart!!.format(DATE),
                toScheduler.dateTimeStart!!.format(TIME),
                toScheduler.dateTimeEnd!!.format(TIME)
            )
        } else {
            context.getString(
                R.string.compromise_screen_message_question_finalization,
                toScheduler.dateTimeStart!!.format(DATE),
                toScheduler.dateTimeStart!!.format(TIME),
                toScheduler.dateTimeEnd!!.format(TIME)
            )
        }

        _uiState.value.messageDialogState.onShowDialog?.showConfirmationDialog(
            message = message
        ) {
            state.onToggleLoading()

            launch {
                val validationError = confirmationSchedulerUseCase(toScheduler, getSchedulerType())

                if (validationError != null) {
                    state.onToggleLoading()
                    _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(validationError.message)
                } else {
                    initializeEditionInfos()
                    onSuccess()
                }
            }
        }
    }

    fun onPrepareChatNavigation(onSuccess: (ChatArgs) -> Unit) {
        launch {
            val id = chatRepository.getChatIdFromPerson(
                senderPerson = _uiState.value.authenticatedPerson,
                receiverPerson = getPersonChatReceiver()!!
            )

            onSuccess(ChatArgs(id))
        }
    }

    private suspend fun getPersonChatReceiver(): TOPerson? {
        val personId = when (_uiState.value.userType) {
            EnumUserType.ACADEMY_MEMBER -> _uiState.value.toScheduler.professionalPersonId
            else -> _uiState.value.toScheduler.academyMemberPersonId
        }

        return personRepository.getTOPersonById(personId!!)
    }
}