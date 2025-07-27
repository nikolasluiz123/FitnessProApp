package br.com.fitnesspro.scheduler.ui.viewmodel

import android.content.Context
import androidx.core.text.isDigitsOnly
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.SchedulerConfigRepository
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.FitnessProViewModel
import br.com.fitnesspro.compose.components.fields.state.DatePickerTextField
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.enums.EnumDateTimePatterns.DATE_ONLY_NUMBERS
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.parseToLocalDate
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.repository.SchedulerRepository
import br.com.fitnesspro.scheduler.ui.screen.scheduler.decorator.SchedulerDecorator
import br.com.fitnesspro.scheduler.ui.state.NewSchedulerReportDialogUIState
import br.com.fitnesspro.scheduler.ui.state.NewSchedulerReportResult
import br.com.fitnesspro.scheduler.ui.state.SchedulerUIState
import br.com.fitnesspro.scheduler.usecase.report.GenerateSchedulerReportUseCase
import br.com.fitnesspro.scheduler.usecase.report.enums.EnumValidatedSchedulerReportFields
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SchedulerViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val schedulerRepository: SchedulerRepository,
    private val schedulerConfigRepository: SchedulerConfigRepository,
    private val personRepository: PersonRepository,
    private val globalEvents: GlobalEvents,
    private val generateSchedulerReportUseCase: GenerateSchedulerReportUseCase
) : FitnessProViewModel() {

    private val _uiState: MutableStateFlow<SchedulerUIState> = MutableStateFlow(SchedulerUIState())
    val uiState get() = _uiState.asStateFlow()

    init {
        initialLoadUIState()
        loadUIStateWithDatabaseInfos()
        updateSchedules()
    }

    override fun getGlobalEventsBus(): GlobalEvents = globalEvents

    override fun onShowErrorDialog(message: String) {
        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(message = message)
    }

    override fun getErrorMessageFrom(throwable: Throwable): String {
        return context.getString(br.com.fitnesspro.common.R.string.unknown_error_message)
    }

    override fun onError(throwable: Throwable) {
        super.onError(throwable)

        if (_uiState.value.newSchedulerReportDialogUIState.showLoading) {
            _uiState.value.newSchedulerReportDialogUIState.onToggleLoading()
        }
    }

    private fun initialLoadUIState() {
        _uiState.update {
            it.copy(
                title = context.getString(R.string.schedule_screen_title),
                onSelectYearMonth = { newYearMonth ->
                    _uiState.value = _uiState.value.copy(selectedYearMonth = newYearMonth)
                    updateSchedules()
                },
                messageDialogState = initializeMessageDialogState(),
                newSchedulerReportDialogUIState = initializeNewSchedulerReportDialogUIState()
            )
        }
    }

    private fun initializeNewSchedulerReportDialogUIState(): NewSchedulerReportDialogUIState {
        return NewSchedulerReportDialogUIState(
            name = initReportNameTextField(),
            dateStart = initReportDateStartDatePickerField(),
            dateEnd = initReportDateEndDatePickerField(),
            onDismissRequest = initReportOnDismiss(),
            onToggleLoading = initReportOnToggleLoading(),
            onShow = initReportOnShowDialog()
        )
    }

    private fun initReportOnShowDialog(): () -> Unit = {
        _uiState.value = _uiState.value.copy(
            newSchedulerReportDialogUIState = _uiState.value.newSchedulerReportDialogUIState.copy(
                showDialog = true
            )
        )
    }

    private fun initReportOnToggleLoading(): () -> Unit = {
        _uiState.value = _uiState.value.copy(
            newSchedulerReportDialogUIState = _uiState.value.newSchedulerReportDialogUIState.copy(
                showLoading = _uiState.value.newSchedulerReportDialogUIState.showLoading.not()
            )
        )
    }

    private fun initReportOnDismiss(): () -> Unit = {
        _uiState.value = _uiState.value.copy(
            newSchedulerReportDialogUIState = _uiState.value.newSchedulerReportDialogUIState.copy(
                showDialog = false
            )
        )
    }

    private fun initReportDateEndDatePickerField(): DatePickerTextField = DatePickerTextField(
        onDatePickerOpenChange = { newOpen ->
            _uiState.value = _uiState.value.copy(
                newSchedulerReportDialogUIState = _uiState.value.newSchedulerReportDialogUIState.copy(
                    dateEnd = _uiState.value.newSchedulerReportDialogUIState.dateEnd.copy(
                        datePickerOpen = newOpen
                    )
                )
            )
        },
        onDateChange = { newDate ->
            _uiState.value = _uiState.value.copy(
                newSchedulerReportDialogUIState = _uiState.value.newSchedulerReportDialogUIState.copy(
                    dateEnd = _uiState.value.newSchedulerReportDialogUIState.dateEnd.copy(
                        value = newDate.format(DATE_ONLY_NUMBERS),
                        errorMessage = ""
                    )
                )
            )

            _uiState.value.newSchedulerReportDialogUIState.dateEnd.onDatePickerDismiss()
        },
        onDatePickerDismiss = {
            _uiState.value = _uiState.value.copy(
                newSchedulerReportDialogUIState = _uiState.value.newSchedulerReportDialogUIState.copy(
                    dateEnd = _uiState.value.newSchedulerReportDialogUIState.dateEnd.copy(
                        datePickerOpen = false
                    )
                )
            )
        },
        onChange = { text ->
            if (text.isDigitsOnly()) {
                _uiState.value = _uiState.value.copy(
                    newSchedulerReportDialogUIState = _uiState.value.newSchedulerReportDialogUIState.copy(
                        dateEnd = _uiState.value.newSchedulerReportDialogUIState.dateEnd.copy(
                            value = text,
                            errorMessage = ""
                        ),
                        result = _uiState.value.newSchedulerReportDialogUIState.result.copy(
                            dateEnd = text.parseToLocalDate(DATE_ONLY_NUMBERS)
                        )
                    )
                )
            }
        }
    )

    private fun initReportDateStartDatePickerField(): DatePickerTextField = DatePickerTextField(
        onDatePickerOpenChange = { newOpen ->
            _uiState.value = _uiState.value.copy(
                newSchedulerReportDialogUIState = _uiState.value.newSchedulerReportDialogUIState.copy(
                    dateStart = _uiState.value.newSchedulerReportDialogUIState.dateStart.copy(
                        datePickerOpen = newOpen
                    )
                )
            )
        },
        onDateChange = { newDate ->
            _uiState.value = _uiState.value.copy(
                newSchedulerReportDialogUIState = _uiState.value.newSchedulerReportDialogUIState.copy(
                    dateStart = _uiState.value.newSchedulerReportDialogUIState.dateStart.copy(
                        value = newDate.format(DATE_ONLY_NUMBERS),
                        errorMessage = ""
                    )
                )
            )

            _uiState.value.newSchedulerReportDialogUIState.dateStart.onDatePickerDismiss()
        },
        onDatePickerDismiss = {
            _uiState.value = _uiState.value.copy(
                newSchedulerReportDialogUIState = _uiState.value.newSchedulerReportDialogUIState.copy(
                    dateStart = _uiState.value.newSchedulerReportDialogUIState.dateStart.copy(
                        datePickerOpen = false
                    )
                )
            )
        },
        onChange = { text ->
            if (text.isDigitsOnly()) {
                _uiState.value = _uiState.value.copy(
                    newSchedulerReportDialogUIState = _uiState.value.newSchedulerReportDialogUIState.copy(
                        dateStart = _uiState.value.newSchedulerReportDialogUIState.dateStart.copy(
                            value = text,
                            errorMessage = ""
                        ),
                        result = _uiState.value.newSchedulerReportDialogUIState.result.copy(
                            dateStart = text.parseToLocalDate(DATE_ONLY_NUMBERS)
                        )
                    )
                )
            }
        }
    )

    private fun initReportNameTextField(): TextField {
        return TextField(
            onChange = { text ->
                _uiState.value = _uiState.value.copy(
                    newSchedulerReportDialogUIState = _uiState.value.newSchedulerReportDialogUIState.copy(
                        name = _uiState.value.newSchedulerReportDialogUIState.name.copy(
                            value = text,
                            errorMessage = ""
                        ),
                        result = _uiState.value.newSchedulerReportDialogUIState.result.copy(
                            reportName = text
                        )
                    )
                )
            }
        )
    }

    private fun loadUIStateWithDatabaseInfos() {
        launch {
            val toPerson = personRepository.getAuthenticatedTOPerson()!!
            val userType = toPerson.user?.type!!

            _uiState.update {
                it.copy(
                    userType = userType,
                    toSchedulerConfig = schedulerConfigRepository.getTOSchedulerConfigByPersonId(toPerson.id!!),
                    isVisibleFabRecurrentScheduler = userType == EnumUserType.PERSONAL_TRAINER
                )
            }
        }
    }

    fun updateSchedules() {
        launch {
            val groupedTOSchedulers = schedulerRepository.getSchedulerList(
                yearMonth = _uiState.value.selectedYearMonth,
                canceledSchedules = false
            ).groupBy { it.dateTimeStart!!.toLocalDate() }

            val decorators = groupedTOSchedulers.map { (date, schedules) ->
                SchedulerDecorator(
                    date = date,
                    count = schedules.size
                )
            }

            _uiState.value = _uiState.value.copy(schedules = decorators)
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

    fun onGenerateReport(onSuccess: (filePath: String) -> Unit) {
        launch {
            val reportResult = _uiState.value.newSchedulerReportDialogUIState.result
            val validationResult = generateSchedulerReportUseCase(reportResult)

            if (validationResult.validations.isEmpty()) {
                onSuccess(validationResult.filePath!!)
            } else {
                _uiState.value.newSchedulerReportDialogUIState.onToggleLoading()
                showValidationMessages(validationResult.validations)
            }
        }
    }

    private fun showValidationMessages(validationResult: List<FieldValidationError<EnumValidatedSchedulerReportFields>>) {
        val dialogValidations = validationResult.firstOrNull { it.field == null }

        if (dialogValidations != null) {
            _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(dialogValidations.message)
            return
        }

        validationResult.forEach {
            when (it.field!!) {
                EnumValidatedSchedulerReportFields.NAME -> {
                    _uiState.value = _uiState.value.copy(
                        newSchedulerReportDialogUIState = _uiState.value.newSchedulerReportDialogUIState.copy(
                            name = _uiState.value.newSchedulerReportDialogUIState.name.copy(
                                errorMessage = it.message
                            )
                        )
                    )
                }
            }
        }
    }

    fun onShowReportDialog() {
        _uiState.value = _uiState.value.copy(
            newSchedulerReportDialogUIState = _uiState.value.newSchedulerReportDialogUIState.copy(
                result = NewSchedulerReportResult(),
                name = _uiState.value.newSchedulerReportDialogUIState.name.copy(
                    value = "",
                    errorMessage = "",
                ),
                dateStart = _uiState.value.newSchedulerReportDialogUIState.dateStart.copy(
                    value = "",
                    errorMessage = "",
                ),
                dateEnd = _uiState.value.newSchedulerReportDialogUIState.dateEnd.copy(
                    value = "",
                    errorMessage = "",
                )
            )
        )

        _uiState.value.newSchedulerReportDialogUIState.onShow()
    }
}