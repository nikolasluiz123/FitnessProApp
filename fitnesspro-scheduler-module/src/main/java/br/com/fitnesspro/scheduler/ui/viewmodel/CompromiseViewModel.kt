package br.com.fitnesspro.scheduler.ui.viewmodel

import android.content.Context
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.compose.components.fields.state.DatePickerTextField
import br.com.fitnesspro.compose.components.fields.state.DayWeeksSelectorField
import br.com.fitnesspro.compose.components.fields.state.PagedDialogListTextField
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.compose.components.fields.state.TimePickerTextField
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.enums.EnumDialogType
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.core.extensions.parseToLocalDate
import br.com.fitnesspro.core.extensions.parseToLocalTime
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.model.enums.EnumCompromiseType.FIRST
import br.com.fitnesspro.model.enums.EnumCompromiseType.RECURRENT
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.repository.SchedulerRepository
import br.com.fitnesspro.scheduler.ui.navigation.CompromiseScreenArgs
import br.com.fitnesspro.scheduler.ui.navigation.compromiseArguments
import br.com.fitnesspro.scheduler.ui.state.CompromiseUIState
import br.com.fitnesspro.scheduler.usecase.scheduler.ConfirmationSchedulerUseCase
import br.com.fitnesspro.scheduler.usecase.scheduler.InactivateSchedulerUseCase
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
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CompromiseViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository,
    private val schedulerRepository: SchedulerRepository,
    private val saveCompromiseUseCase: SaveCompromiseUseCase,
    private val confirmationSchedulerUseCase: ConfirmationSchedulerUseCase,
    private val inactivateSchedulerUseCase: InactivateSchedulerUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState: MutableStateFlow<CompromiseUIState> = MutableStateFlow(CompromiseUIState())
    val uiState get() = _uiState.asStateFlow()

    private val jsonArgs: String? = savedStateHandle[compromiseArguments]

    init {
        initialUIStateLoad()
        loadUIStateWithDatabaseInfos()
    }

    private fun initialUIStateLoad() {
        val args = jsonArgs?.fromJsonNavParamToArgs(CompromiseScreenArgs::class.java)!!

        _uiState.update {
            it.copy(
                subtitle = getSubtitle(args.date),
                professional = initializeProfessionalPagedDialogListField(),
                member = initializeMemberPagedDialogListField(),
                dateStart = initializeDateStartDatePickerField(),
                dateEnd = initializeDateEndDatePickerField(),
                hourStart = initializeHourStartTimePickerField(),
                hourEnd = initializeHourEndTimePickerField(),
                observation = initializeObservationField(),
                dayWeeksSelectorField = DayWeeksSelectorField(
                    onSelect = {
                        _uiState.value = _uiState.value.copy(
                            recurrentConfig = _uiState.value.recurrentConfig.copy(
                                dayWeeks = _uiState.value.dayWeeksSelectorField.selected.toList()
                            )
                        )
                    }
                ),
                recurrent = args.recurrent,
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
                toScheduler = _uiState.value.toScheduler.copy(scheduledDate = args.date)
            )
        }
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
        return TimePickerTextField(
            onTimePickerOpenChange = { newOpen ->
                _uiState.value = _uiState.value.copy(
                    hourEnd = _uiState.value.hourEnd.copy(timePickerOpen = newOpen)
                )
            },
            onTimeChange = { newTime ->
                _uiState.value = _uiState.value.copy(
                    hourEnd = _uiState.value.hourEnd.copy(
                        value = newTime.format(EnumDateTimePatterns.TIME_ONLY_NUMBERS),
                        errorMessage = ""
                    ),
                    toScheduler = _uiState.value.toScheduler.copy(end = newTime)
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
                            end = text.parseToLocalTime(EnumDateTimePatterns.TIME_ONLY_NUMBERS)
                        )
                    )
                }
            }
        )
    }

    private fun initializeHourStartTimePickerField(): TimePickerTextField {
        return TimePickerTextField(
            onTimePickerOpenChange = { newOpen ->
                _uiState.value = _uiState.value.copy(
                    hourStart = _uiState.value.hourStart.copy(timePickerOpen = newOpen)
                )
            },
            onTimeChange = { newTime ->
                _uiState.value = _uiState.value.copy(
                    hourStart = _uiState.value.hourStart.copy(
                        value = newTime.format(EnumDateTimePatterns.TIME_ONLY_NUMBERS),
                        errorMessage = ""
                    ),
                    toScheduler = _uiState.value.toScheduler.copy(start = newTime)
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
                            start = text.parseToLocalTime(EnumDateTimePatterns.TIME_ONLY_NUMBERS)
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
                        value = newDate.format(EnumDateTimePatterns.DATE_ONLY_NUMBERS),
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
                            dateEnd = text.parseToLocalDate(EnumDateTimePatterns.DATE_ONLY_NUMBERS)
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
                        value = newDate.format(EnumDateTimePatterns.DATE_ONLY_NUMBERS),
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
                            dateStart = text.parseToLocalDate(EnumDateTimePatterns.DATE_ONLY_NUMBERS)
                        )
                    )
                }
            }
        )
    }

    private fun initializeMemberPagedDialogListField(): PagedDialogListTextField<PersonTuple> {
        return PagedDialogListTextField(
            dialogTitle = context.getString(R.string.compromise_screen_label_member_list),
            onShow = {
                _uiState.value = _uiState.value.copy(
                    member = _uiState.value.member.copy(show = true)
                )
            },
            onHide = {
                _uiState.value = _uiState.value.copy(
                    member = _uiState.value.member.copy(show = false)
                )
            },
            onChange = { newText ->
                _uiState.value = _uiState.value.copy(
                    member = _uiState.value.member.copy(
                        value = newText,
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

                _uiState.value.member.onHide()
            },
            onSimpleFilterChange = { filter ->
                _uiState.value = _uiState.value.copy(
                    member = _uiState.value.member.copy(
                        dataList = getListMembers(filter)
                    )
                )
            }
        )
    }

    private fun initializeProfessionalPagedDialogListField(): PagedDialogListTextField<PersonTuple> {
        return PagedDialogListTextField(
            dialogTitle = context.getString(R.string.compromise_screen_label_professional_list),
            onShow = {
                _uiState.value = _uiState.value.copy(
                    professional = _uiState.value.professional.copy(show = true)
                )
            },
            onHide = {
                _uiState.value = _uiState.value.copy(
                    professional = _uiState.value.professional.copy(show = false)
                )
            },
            onChange = { newText ->
                _uiState.value = _uiState.value.copy(
                    professional = _uiState.value.professional.copy(
                        value = newText,
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

                _uiState.value.professional.onHide()
            },
            onSimpleFilterChange = { filter ->
                _uiState.value = _uiState.value.copy(
                    professional = _uiState.value.professional.copy(
                        dataList = getListProfessional(filter)
                    )
                )
            }
        )
    }

    private fun loadUIStateWithDatabaseInfos() {
        viewModelScope.launch {
            val toPerson = userRepository.getAuthenticatedTOPerson()!!
            val userType = toPerson.toUser?.type!!
            val args = jsonArgs?.fromJsonNavParamToArgs(CompromiseScreenArgs::class.java)!!
            val menuItemListProfessional = getListProfessional(toPerson.id!!)
            val menuItemListMembers = getListMembers(toPerson.id!!)

            _uiState.update { state ->
                state.copy(
                    title = getTitle(
                        userType = userType,
                        recurrent = args.recurrent,
                    ),
                    userType = userType,
                    professional = _uiState.value.professional.copy(
                        dataList = menuItemListProfessional,
                    ),
                    member = _uiState.value.member.copy(
                        dataList = menuItemListMembers,
                    ),
                    toScheduler = getToSchedulerWithDefaultInfos(toPerson, args)
                )
            }

            initializeEditionInfos()
        }
    }

    private fun getToSchedulerWithDefaultInfos(
        authenticatedPerson: TOPerson,
        args: CompromiseScreenArgs
    ): TOScheduler {
        return when (authenticatedPerson.toUser?.type!!) {
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
                        value = to.start!!.format(EnumDateTimePatterns.TIME_ONLY_NUMBERS)
                    ),
                    hourEnd = _uiState.value.hourEnd.copy(
                        value = to.end!!.format(EnumDateTimePatterns.TIME_ONLY_NUMBERS)
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

    private fun getListProfessional(authenticatedPersonId: String, simpleFilter: String = ""): Flow<PagingData<PersonTuple>> {
        val types = listOf(EnumUserType.PERSONAL_TRAINER, EnumUserType.NUTRITIONIST)

        return userRepository.getListTOPersonWithUserType(
            types = types,
            simpleFilter = simpleFilter,
            authenticatedPersonId = authenticatedPersonId
        ).flow
    }

    private fun getListMembers(authenticatedPersonId: String, simpleFilter: String = ""): Flow<PagingData<PersonTuple>> {
        val types = listOf(EnumUserType.ACADEMY_MEMBER)

        return userRepository.getListTOPersonWithUserType(
            types = types,
            simpleFilter = simpleFilter,
            authenticatedPersonId = authenticatedPersonId
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
                    if (toScheduler?.id != null) {
                        context.getString(R.string.compromise_screen_title_recurrent_compromise)
                    } else {
                        context.getString(R.string.compromise_screen_title_new_recurrent_compromise)
                    }
                } else {
                    if (toScheduler?.id != null) {
                        val situation = _uiState.value.toScheduler.situation!!.getLabel(context)!!
                        context.getString(R.string.compromise_screen_title_compromise_with_situation, situation)
                    } else {
                        context.getString(R.string.compromise_screen_title_new_compromise)
                    }
                }
            }
            EnumUserType.NUTRITIONIST -> {
                if (toScheduler?.id != null) {
                    val situation = _uiState.value.toScheduler.situation!!.getLabel(context)!!
                    context.getString(R.string.compromise_screen_title_compromise_with_situation, situation)
                } else {
                    context.getString(R.string.compromise_screen_title_new_compromise)
                }
            }
            EnumUserType.ACADEMY_MEMBER -> {
                if (toScheduler?.id != null) {
                    context.getString(R.string.compromise_screen_title_compromise)
                } else {
                    context.getString(R.string.compromise_screen_title_new_sugestion)
                }
            }
        }
    }

    private fun getSubtitle(date: LocalDate?): String? {
        return date?.format(EnumDateTimePatterns.DATE)
    }

    private fun getSubtitle(toScheduler: TOScheduler): String {
        return context.getString(
            R.string.compromise_screen_subtitle,
            toScheduler.scheduledDate!!.format(EnumDateTimePatterns.DATE),
            toScheduler.start!!.format(EnumDateTimePatterns.TIME),
            toScheduler.end!!.format(EnumDateTimePatterns.TIME)
        )
    }

    fun saveCompromise(onSuccess: (EnumSchedulerType) -> Unit) {
        viewModelScope.launch {
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
                showFieldsValidationMessages(validationResults)
            }
        }
    }

    private fun showFieldsValidationMessages(validationResults: MutableList<FieldValidationError<EnumValidatedCompromiseFields, EnumCompromiseValidationTypes>>) {
        val dialogValidations = validationResults.firstOrNull { it.field == null }

        if (dialogValidations != null) {
            _uiState.value.onShowDialog?.onShow(
                type = EnumDialogType.ERROR,
                message = dialogValidations.message,
                onConfirm = { _uiState.value.onHideDialog.invoke() },
                onCancel = { _uiState.value.onHideDialog.invoke() }
            )

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
                    _uiState.value.onShowDialog?.onShow(
                        type = EnumDialogType.ERROR,
                        message = it.message,
                        onConfirm = { _uiState.value.onHideDialog.invoke() },
                        onCancel = { _uiState.value.onHideDialog.invoke() }
                    )
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

    fun onInactivateCompromiseClick(onSuccess: () -> Unit) {
        val state = _uiState.value
        val toScheduler = state.toScheduler

        state.onShowDialog?.onShow(
            type = EnumDialogType.CONFIRMATION,
            message = context.getString(
                R.string.compromise_screen_dialog_inactivation_message,
                toScheduler.scheduledDate!!.format(EnumDateTimePatterns.DATE),
                toScheduler.start!!.format(EnumDateTimePatterns.TIME),
                toScheduler.end!!.format(EnumDateTimePatterns.TIME)
            ),
            onConfirm = {
                viewModelScope.launch {
                    val result = inactivateSchedulerUseCase(toScheduler)

                    if (result != null) {
                        state.onShowDialog.onShow(
                            type = EnumDialogType.ERROR,
                            message = result.message,
                            onConfirm = { },
                            onCancel = { }
                        )
                    } else {
                        initializeEditionInfos()
                        onSuccess()
                    }

                }
            },
            onCancel = { }
        )
    }

    fun onScheduleConfirmClick(onSuccess: () -> Unit) {
        val state = _uiState.value
        val toScheduler = state.toScheduler

        val message = if (toScheduler.situation == EnumSchedulerSituation.SCHEDULED) {
            context.getString(
                R.string.compromise_screen_message_question_confirmation,
                toScheduler.scheduledDate!!.format(EnumDateTimePatterns.DATE),
                toScheduler.start!!.format(EnumDateTimePatterns.TIME),
                toScheduler.end!!.format(EnumDateTimePatterns.TIME)
            )
        } else {
            context.getString(
                R.string.compromise_screen_message_question_finalization,
                toScheduler.scheduledDate!!.format(EnumDateTimePatterns.DATE),
                toScheduler.start!!.format(EnumDateTimePatterns.TIME),
                toScheduler.end!!.format(EnumDateTimePatterns.TIME)
            )
        }

        state.onShowDialog?.onShow(
            type = EnumDialogType.CONFIRMATION,
            message = message,
            onConfirm = {
                viewModelScope.launch {
                    val result = confirmationSchedulerUseCase(toScheduler)

                    if (result != null) {
                        state.onShowDialog.onShow(
                            type = EnumDialogType.ERROR,
                            message = result.message,
                            onConfirm = { },
                            onCancel = { }
                        )
                    } else {
                        initializeEditionInfos()
                        onSuccess()
                    }
                }
            },
            onCancel = { }
        )
    }
}