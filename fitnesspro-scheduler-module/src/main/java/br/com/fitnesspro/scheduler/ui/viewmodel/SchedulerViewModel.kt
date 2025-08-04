package br.com.fitnesspro.scheduler.ui.viewmodel

import android.content.Context
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.SchedulerConfigRepository
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.base.FitnessProStatefulViewModel
import br.com.fitnesspro.core.callback.showErrorDialog
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
import javax.inject.Inject

@HiltViewModel
class SchedulerViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val schedulerRepository: SchedulerRepository,
    private val schedulerConfigRepository: SchedulerConfigRepository,
    private val personRepository: PersonRepository,
    private val globalEvents: GlobalEvents,
    private val generateSchedulerReportUseCase: GenerateSchedulerReportUseCase
) : FitnessProStatefulViewModel() {

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

    override fun initialLoadUIState() {
        _uiState.value = _uiState.value.copy(
            title = context.getString(R.string.schedule_screen_title),
            onSelectYearMonth = { newYearMonth ->
                _uiState.value = _uiState.value.copy(selectedYearMonth = newYearMonth)
                updateSchedules()
            },
            messageDialogState = createMessageDialogState(
                getCurrentState = { _uiState.value.messageDialogState },
                updateState = { newState -> _uiState.value = _uiState.value.copy(messageDialogState = newState) }
            ),
            newSchedulerReportDialogUIState = initializeNewSchedulerReportDialogUIState()
        )
    }

    private fun initializeNewSchedulerReportDialogUIState(): NewSchedulerReportDialogUIState {
        return NewSchedulerReportDialogUIState(
            name = createTextFieldState(
                getCurrentState = { _uiState.value.newSchedulerReportDialogUIState.name },
                updateState = { newState ->
                    _uiState.value = _uiState.value.copy(
                        newSchedulerReportDialogUIState = _uiState.value.newSchedulerReportDialogUIState.copy(
                            name = newState,
                            result = _uiState.value.newSchedulerReportDialogUIState.result.copy(reportName = newState.value)
                        )
                    )
                },
            ),
            dateStart = createDatePickerFieldState(
                getCurrentState = { _uiState.value.newSchedulerReportDialogUIState.dateStart },
                updateState = {
                    _uiState.value = _uiState.value.copy(
                        newSchedulerReportDialogUIState = _uiState.value.newSchedulerReportDialogUIState.copy(
                            dateStart = it
                        )
                    )
                },
                onDateChange = { newLocalDate ->
                    _uiState.value = _uiState.value.copy(
                        newSchedulerReportDialogUIState = _uiState.value.newSchedulerReportDialogUIState.copy(
                            result = _uiState.value.newSchedulerReportDialogUIState.result.copy(
                                dateStart = newLocalDate
                            )
                        )
                    )
                }
            ),
            dateEnd = createDatePickerFieldState(
                getCurrentState = { _uiState.value.newSchedulerReportDialogUIState.dateEnd },
                updateState = {
                    _uiState.value = _uiState.value.copy(
                        newSchedulerReportDialogUIState = _uiState.value.newSchedulerReportDialogUIState.copy(
                            dateEnd = it
                        )
                    )
                },
                onDateChange = { newLocalDate ->
                    _uiState.value = _uiState.value.copy(
                        newSchedulerReportDialogUIState = _uiState.value.newSchedulerReportDialogUIState.copy(
                            result = _uiState.value.newSchedulerReportDialogUIState.result.copy(
                                dateEnd = newLocalDate
                            )
                        )
                    )
                }
            ),
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

    private fun loadUIStateWithDatabaseInfos() {
        launch {
            val toPerson = personRepository.getAuthenticatedTOPerson()!!
            val userType = toPerson.user?.type!!

            _uiState.value = _uiState.value.copy(
                userType = userType,
                toSchedulerConfig = schedulerConfigRepository.getTOSchedulerConfigByPersonId(toPerson.id!!),
                isVisibleFabRecurrentScheduler = userType == EnumUserType.PERSONAL_TRAINER
            )
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