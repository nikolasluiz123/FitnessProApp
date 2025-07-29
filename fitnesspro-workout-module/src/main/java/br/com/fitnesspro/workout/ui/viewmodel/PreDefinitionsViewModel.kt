package br.com.fitnesspro.workout.ui.viewmodel

import android.content.Context
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.FitnessProViewModel
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.compose.components.filter.SimpleFilterState
import br.com.fitnesspro.core.callback.showConfirmationDialog
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.to.TOWorkoutGroupPreDefinition
import br.com.fitnesspro.workout.repository.ExercisePreDefinitionRepository
import br.com.fitnesspro.workout.ui.state.PreDefinitionGroupDialogUIState
import br.com.fitnesspro.workout.ui.state.PreDefinitionsUIState
import br.com.fitnesspro.workout.usecase.exercise.EditWorkoutGroupPreDefinitionUseCase
import br.com.fitnesspro.workout.usecase.exercise.InactivateWorkoutGroupPreDefinitionUseCase
import br.com.fitnesspro.workout.usecase.exercise.enums.EnumValidatedExercisePreDefinitionFields
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PreDefinitionsViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val globalEvents: GlobalEvents,
    private val exercisePreDefinitionRepository: ExercisePreDefinitionRepository,
    private val personRepository: PersonRepository,
    private val editWorkoutGroupPreDefinitionUseCase: EditWorkoutGroupPreDefinitionUseCase,
    private val inactivateWorkoutGroupPreDefinitionUseCase: InactivateWorkoutGroupPreDefinitionUseCase
): FitnessProViewModel() {

    private val _uiState: MutableStateFlow<PreDefinitionsUIState> = MutableStateFlow(PreDefinitionsUIState())
    val uiState get() = _uiState.asStateFlow()

    init {
        initialLoadUIState()
        loadStateWithDatabaseInfos()
    }

    private fun loadStateWithDatabaseInfos() {
        launch {
            val personId = personRepository.getAuthenticatedTOPerson()?.id!!

            _uiState.value = _uiState.value.copy(
                authenticatedPersonId = personId,
                predefinitions = exercisePreDefinitionRepository.getListGroupedPredefinitions(personId).flow
            )
        }
    }

    private fun initialLoadUIState() {
        _uiState.value = _uiState.value.copy(
            simpleFilterState = initializeSimpleFilterState(),
            messageDialogState = initializeMessageDialogState(),
            workoutGroupPreDefinitionGroupDialogUIState = PreDefinitionGroupDialogUIState(
                name = initializeEditWorkoutGroupPreDefinitionTextFieldName(),
                onShowDialogChange = { show ->
                    _uiState.value = _uiState.value.copy(
                        workoutGroupPreDefinitionGroupDialogUIState = _uiState.value.workoutGroupPreDefinitionGroupDialogUIState.copy(
                            showDialog = show,
                        )
                    )

                    if (!show) {
                        _uiState.value = _uiState.value.copy(workoutGroupPreDefinitionIdEdited = null)
                    }
                },
                onToggleLoading = {
                    _uiState.value = _uiState.value.copy(
                        workoutGroupPreDefinitionGroupDialogUIState = _uiState.value.workoutGroupPreDefinitionGroupDialogUIState.copy(
                            showLoading = !_uiState.value.workoutGroupPreDefinitionGroupDialogUIState.showLoading
                        )
                    )
                }
            ),
            onToggleBottomSheetNewPredefinition = {
                _uiState.value = _uiState.value.copy(
                    showBottomSheetNewPredefinition = !_uiState.value.showBottomSheetNewPredefinition
                )
            }
        )
    }

    private fun initializeEditWorkoutGroupPreDefinitionTextFieldName(): TextField {
        return TextField(
            onChange = { text ->
                _uiState.value = _uiState.value.copy(
                    workoutGroupPreDefinitionGroupDialogUIState = _uiState.value.workoutGroupPreDefinitionGroupDialogUIState.copy(
                        name = _uiState.value.workoutGroupPreDefinitionGroupDialogUIState.name.copy(
                            value = text,
                            errorMessage = ""
                        ),
                        toWorkoutGroupPreDefinition = _uiState.value.workoutGroupPreDefinitionGroupDialogUIState.toWorkoutGroupPreDefinition.copy(
                            name = text.ifEmpty { null }
                        )
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

    private fun initializeSimpleFilterState(): SimpleFilterState {
        return SimpleFilterState(
            onSimpleFilterChange = { filterText ->
                _uiState.value = _uiState.value.copy(
                    simpleFilterState = _uiState.value.simpleFilterState.copy(
                        quickFilter = filterText
                    )
                )

                _uiState.value.authenticatedPersonId?.let { personId ->
                    _uiState.value = _uiState.value.copy(
                        predefinitions = exercisePreDefinitionRepository.getListGroupedPredefinitions(
                            authenticatedPersonId = personId,
                            simpleFilter = filterText
                        ).flow
                    )
                }
            },
            onExpandedChange = {
                _uiState.value = _uiState.value.copy(
                    simpleFilterState = _uiState.value.simpleFilterState.copy(
                        simpleFilterExpanded = it
                    )
                )
            }
        )
    }

    fun onSaveWorkoutGroupPreDefinition(onSuccess: () -> Unit) {
        launch {
            val validationResults = editWorkoutGroupPreDefinitionUseCase(
                toWorkoutGroup = _uiState.value.workoutGroupPreDefinitionGroupDialogUIState.toWorkoutGroupPreDefinition
            )

            if (validationResults.isEmpty()) {
                onSuccess()
            } else {
                _uiState.value.workoutGroupPreDefinitionGroupDialogUIState.onToggleLoading()
                showFieldsValidationMessagesWorkoutGroupPreDefinitionDialog(validationResults.toMutableList())
            }
        }
    }

    private fun showFieldsValidationMessagesWorkoutGroupPreDefinitionDialog(validationResults: List<FieldValidationError<EnumValidatedExercisePreDefinitionFields>>) {
        validationResults.forEach {
            when (it.field!!) {
                EnumValidatedExercisePreDefinitionFields.GROUP -> {
                    _uiState.value = _uiState.value.copy(
                        workoutGroupPreDefinitionGroupDialogUIState = _uiState.value.workoutGroupPreDefinitionGroupDialogUIState.copy(
                            name = _uiState.value.workoutGroupPreDefinitionGroupDialogUIState.name.copy(
                                errorMessage = it.message
                            )
                        )
                    )
                }
            }
        }
    }

    fun onInactivateWorkoutGroupPreDefinition(onSuccess: () -> Unit) {
        _uiState.value.workoutGroupPreDefinitionGroupDialogUIState.toWorkoutGroupPreDefinition.id?.let { workoutGroupPreDefinitionId ->

            _uiState.value.messageDialogState.onShowDialog?.showConfirmationDialog(
                message = context.getString(
                    br.com.fitnesspro.workout.R.string.workout_group_inactivate_dialog_message,
                    _uiState.value.workoutGroupPreDefinitionGroupDialogUIState.toWorkoutGroupPreDefinition.name!!
                )
            ) {
                _uiState.value.workoutGroupPreDefinitionGroupDialogUIState.onToggleLoading()

                launch {
                    inactivateWorkoutGroupPreDefinitionUseCase(workoutGroupPreDefinitionId)
                    onSuccess()
                }
            }
        }
    }

    fun onLoadDataWorkoutGroupPreDefinitionEdition() {
        launch {
            val toWorkoutGroup = exercisePreDefinitionRepository.findTOWorkoutGroupPreDefinitionById(_uiState.value.workoutGroupPreDefinitionIdEdited!!)!!

            _uiState.value = _uiState.value.copy(
                workoutGroupPreDefinitionGroupDialogUIState = _uiState.value.workoutGroupPreDefinitionGroupDialogUIState.copy(
                    toWorkoutGroupPreDefinition = toWorkoutGroup,
                    title = getEditWorkoutDialogTitle(toWorkoutGroup),
                    name = _uiState.value.workoutGroupPreDefinitionGroupDialogUIState.name.copy(value = toWorkoutGroup.name!!),
                    showLoading = false
                )
            )
        }
    }

    private fun getEditWorkoutDialogTitle(toWorkoutGroup: TOWorkoutGroupPreDefinition): String {
        return context.getString(br.com.fitnesspro.workout.R.string.workout_group_pre_definition_edit_dialog_title, toWorkoutGroup.name)
    }

    override fun getErrorMessageFrom(throwable: Throwable): String {
        return context.getString(br.com.fitnesspro.common.R.string.unknown_error_message)
    }

    override fun onShowErrorDialog(message: String) {
        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(message = message)
    }

    override fun getGlobalEventsBus(): GlobalEvents {
        return globalEvents
    }
}