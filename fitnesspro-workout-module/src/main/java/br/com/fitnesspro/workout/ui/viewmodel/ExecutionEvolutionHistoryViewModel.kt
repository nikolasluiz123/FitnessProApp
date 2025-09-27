package br.com.fitnesspro.workout.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.base.FitnessProStatefulViewModel
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.tuple.WorkoutTuple
import br.com.fitnesspro.workout.repository.ExerciseExecutionRepository
import br.com.fitnesspro.workout.repository.WorkoutRepository
import br.com.fitnesspro.workout.ui.navigation.ExecutionEvolutionHistoryScreenArgs
import br.com.fitnesspro.workout.ui.navigation.executionEvolutionHistoryScreenRouteArguments
import br.com.fitnesspro.workout.ui.state.ExecutionEvolutionHistoryUIState
import br.com.fitnesspro.workout.ui.state.reports.NewRegisterEvolutionReportDialogUIState
import br.com.fitnesspro.workout.ui.state.reports.NewRegisterEvolutionReportResult
import br.com.fitnesspro.workout.usecase.reports.EnumValidatedRegisterEvolutionReportFields
import br.com.fitnesspro.workout.usecase.reports.GenerateWorkoutEvolutionReportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ExecutionEvolutionHistoryViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val globalEvents: GlobalEvents,
    private val exerciseExecutionRepository: ExerciseExecutionRepository,
    private val workoutRepository: WorkoutRepository,
    private val generateWorkoutEvolutionReportUseCase: GenerateWorkoutEvolutionReportUseCase,
    savedStateHandle: SavedStateHandle
): FitnessProStatefulViewModel() {

    private val _uiState: MutableStateFlow<ExecutionEvolutionHistoryUIState> = MutableStateFlow(ExecutionEvolutionHistoryUIState())
    val uiState get() = _uiState.asStateFlow()

    val jsonArgs: String? = savedStateHandle[executionEvolutionHistoryScreenRouteArguments]

    init {
        initialLoadUIState()
    }

    override fun initialLoadUIState() {
        _uiState.value = _uiState.value.copy(
            simpleFilterState = createSimpleFilterState(
                getCurrentState = { _uiState.value.simpleFilterState },
                updateState = { newState -> _uiState.value = _uiState.value.copy(simpleFilterState = newState) },
                onSimpleFilterChange = {
                    launch {
                        loadHistory(it)
                    }
                }
            ),
            messageDialogState = createMessageDialogState(
                getCurrentState = { _uiState.value.messageDialogState },
                updateState = { newState -> _uiState.value = _uiState.value.copy(messageDialogState = newState) }
            ),
            newRegisterEvolutionReportDialogUIState = initializeNewRegisterEvolutionReportDialogUIState()
        )
    }

    private fun initializeNewRegisterEvolutionReportDialogUIState(): NewRegisterEvolutionReportDialogUIState {
        return NewRegisterEvolutionReportDialogUIState(
            name = createTextFieldState(
                getCurrentState = { _uiState.value.newRegisterEvolutionReportDialogUIState.name },
                updateState = { newState ->
                    _uiState.value = _uiState.value.copy(
                        newRegisterEvolutionReportDialogUIState = _uiState.value.newRegisterEvolutionReportDialogUIState.copy(
                            name = newState,
                            result = _uiState.value.newRegisterEvolutionReportDialogUIState.result.copy(reportName = newState.value)
                        )
                    )
                },
            ),
            workout = createPagedDialogListTextField(
                getCurrentState = { _uiState.value.newRegisterEvolutionReportDialogUIState.workout },
                updateState = {
                    _uiState.value = _uiState.value.copy(
                        newRegisterEvolutionReportDialogUIState = _uiState.value.newRegisterEvolutionReportDialogUIState.copy(
                            workout = it
                        )
                    )
                },
                dialogTitle = context.getString(br.com.fitnesspro.workout.R.string.new_register_evolution_report_dialog_workout_title),
                getDataListFlow = {
                    getListWorkouts(it)
                },
                onDataListItemClick = {
                    _uiState.value = _uiState.value.copy(
                        newRegisterEvolutionReportDialogUIState = _uiState.value.newRegisterEvolutionReportDialogUIState.copy(
                            result = _uiState.value.newRegisterEvolutionReportDialogUIState.result.copy(workoutId = it.id)
                        )
                    )
                }
            ),
            dateStart = createDatePickerFieldState(
                getCurrentState = { _uiState.value.newRegisterEvolutionReportDialogUIState.dateStart },
                updateState = {
                    _uiState.value = _uiState.value.copy(
                        newRegisterEvolutionReportDialogUIState = _uiState.value.newRegisterEvolutionReportDialogUIState.copy(
                            dateStart = it
                        )
                    )
                },
                onDateChange = { newLocalDate ->
                    _uiState.value = _uiState.value.copy(
                        newRegisterEvolutionReportDialogUIState = _uiState.value.newRegisterEvolutionReportDialogUIState.copy(
                            result = _uiState.value.newRegisterEvolutionReportDialogUIState.result.copy(
                                dateStart = newLocalDate
                            )
                        )
                    )
                }
            ),
            dateEnd = createDatePickerFieldState(
                getCurrentState = { _uiState.value.newRegisterEvolutionReportDialogUIState.dateEnd },
                updateState = {
                    _uiState.value = _uiState.value.copy(
                        newRegisterEvolutionReportDialogUIState = _uiState.value.newRegisterEvolutionReportDialogUIState.copy(
                            dateEnd = it
                        )
                    )
                },
                onDateChange = { newLocalDate ->
                    _uiState.value = _uiState.value.copy(
                        newRegisterEvolutionReportDialogUIState = _uiState.value.newRegisterEvolutionReportDialogUIState.copy(
                            result = _uiState.value.newRegisterEvolutionReportDialogUIState.result.copy(
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

    private suspend fun getListWorkouts(simpleFilter: String? = null): Flow<PagingData<WorkoutTuple>> {
        return workoutRepository.getWorkoutsFromPerson(simpleFilter).flow
    }

    private fun initReportOnShowDialog(): () -> Unit = {
        _uiState.value = _uiState.value.copy(
            newRegisterEvolutionReportDialogUIState = _uiState.value.newRegisterEvolutionReportDialogUIState.copy(
                showDialog = true
            )
        )
    }

    private fun initReportOnToggleLoading(): () -> Unit = {
        _uiState.value = _uiState.value.copy(
            newRegisterEvolutionReportDialogUIState = _uiState.value.newRegisterEvolutionReportDialogUIState.copy(
                showLoading = _uiState.value.newRegisterEvolutionReportDialogUIState.showLoading.not()
            )
        )
    }

    private fun initReportOnDismiss(): () -> Unit = {
        _uiState.value = _uiState.value.copy(
            newRegisterEvolutionReportDialogUIState = _uiState.value.newRegisterEvolutionReportDialogUIState.copy(
                showDialog = false
            )
        )
    }

    fun onExecuteLoad() {
        launch {
            loadHistory()
            loadUIStateWithDatabaseInfos()

            _uiState.value.executeLoad = false
        }
    }

    private suspend fun loadUIStateWithDatabaseInfos() {
        _uiState.value = _uiState.value.copy(
            newRegisterEvolutionReportDialogUIState = _uiState.value.newRegisterEvolutionReportDialogUIState.copy(
                workout = _uiState.value.newRegisterEvolutionReportDialogUIState.workout.copy(
                    dialogListState = _uiState.value.newRegisterEvolutionReportDialogUIState.workout.dialogListState.copy(
                        dataList = getListWorkouts()
                    )
                )
            )
        )
    }

    private suspend fun loadHistory(simpleFilter: String = _uiState.value.simpleFilterState.quickFilter) {
        val args = jsonArgs?.fromJsonNavParamToArgs(ExecutionEvolutionHistoryScreenArgs::class.java)!!

        _uiState.value = _uiState.value.copy(
            history = exerciseExecutionRepository.getListExecutionHistoryGrouped(
                simpleFilter = simpleFilter,
                personMemberId = args.personMemberId
            ).flow
        )
    }

    fun onGenerateReport(onSuccess: (filePath: String) -> Unit) {
        launch {
            val reportResult = _uiState.value.newRegisterEvolutionReportDialogUIState.result
            val validationResult = generateWorkoutEvolutionReportUseCase(reportResult)

            if (validationResult.validations.isEmpty()) {
                onSuccess(validationResult.filePath!!)
            } else {
                _uiState.value.newRegisterEvolutionReportDialogUIState.onToggleLoading()
                showValidationMessages(validationResult.validations)
            }
        }
    }

    private fun showValidationMessages(validationResult: List<FieldValidationError<EnumValidatedRegisterEvolutionReportFields>>) {
        val dialogValidations = validationResult.firstOrNull { it.field == null }

        if (dialogValidations != null) {
            _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(dialogValidations.message)
            return
        }

        validationResult.forEach {
            when (it.field!!) {
                EnumValidatedRegisterEvolutionReportFields.NAME -> {
                    _uiState.value = _uiState.value.copy(
                        newRegisterEvolutionReportDialogUIState = _uiState.value.newRegisterEvolutionReportDialogUIState.copy(
                            name = _uiState.value.newRegisterEvolutionReportDialogUIState.name.copy(
                                errorMessage = it.message
                            )
                        )
                    )
                }

                EnumValidatedRegisterEvolutionReportFields.WORKOUT -> {
                    _uiState.value = _uiState.value.copy(
                        newRegisterEvolutionReportDialogUIState = _uiState.value.newRegisterEvolutionReportDialogUIState.copy(
                            workout = _uiState.value.newRegisterEvolutionReportDialogUIState.workout.copy(
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
            newRegisterEvolutionReportDialogUIState = _uiState.value.newRegisterEvolutionReportDialogUIState.copy(
                result = NewRegisterEvolutionReportResult(),
                name = _uiState.value.newRegisterEvolutionReportDialogUIState.name.copy(
                    value = "",
                    errorMessage = "",
                ),
                workout = _uiState.value.newRegisterEvolutionReportDialogUIState.workout.copy(
                    value = "",
                    errorMessage = "",
                ),
                dateStart = _uiState.value.newRegisterEvolutionReportDialogUIState.dateStart.copy(
                    value = "",
                    errorMessage = "",
                ),
                dateEnd = _uiState.value.newRegisterEvolutionReportDialogUIState.dateEnd.copy(
                    value = "",
                    errorMessage = "",
                ),
            )
        )

        _uiState.value.newRegisterEvolutionReportDialogUIState.onShow()
    }

    override fun onError(throwable: Throwable) {
        super.onError(throwable)

        if (_uiState.value.newRegisterEvolutionReportDialogUIState.showLoading) {
            _uiState.value.newRegisterEvolutionReportDialogUIState.onToggleLoading()
        }
    }

    override fun getErrorMessageFrom(throwable: Throwable): String {
        return context.getString(R.string.unknown_error_message)
    }

    override fun onShowErrorDialog(message: String) {
        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(message = message)
    }

    override fun getGlobalEventsBus(): GlobalEvents {
        return globalEvents
    }
}