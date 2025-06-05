package br.com.fitnesspro.workout.ui.viewmodel

import android.content.Context
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.FitnessProViewModel
import br.com.fitnesspro.compose.components.fields.menu.MenuItem
import br.com.fitnesspro.compose.components.fields.menu.getLabelOrEmptyIfNullValue
import br.com.fitnesspro.compose.components.fields.state.DropDownTextField
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.core.callback.showConfirmationDialog
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.extensions.getFirstPartFullDisplayName
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.to.TOWorkoutGroup
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.repository.WorkoutGroupRepository
import br.com.fitnesspro.workout.ui.state.WorkoutGroupEditDialogUIState
import br.com.fitnesspro.workout.usecase.workout.EditWorkoutGroupUseCase
import br.com.fitnesspro.workout.usecase.workout.EnumValidatedWorkoutGroupFields
import br.com.fitnesspro.workout.usecase.workout.InactivateWorkoutGroupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class WorkoutGroupEditDialogViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val globalEvents: GlobalEvents,
    private val workoutGroupRepository: WorkoutGroupRepository,
    private val editWorkoutGroupUseCase: EditWorkoutGroupUseCase,
    private val inactivateWorkoutGroupUseCase: InactivateWorkoutGroupUseCase
): FitnessProViewModel() {

    private val _uiState: MutableStateFlow<WorkoutGroupEditDialogUIState> = MutableStateFlow(WorkoutGroupEditDialogUIState())
    val uiState get() = _uiState.asStateFlow()

    init {
        initialLoadUIState()
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

    fun loadUIStateWithDatabaseInfos(workoutGroupId: String) {
        launch {
            val toWorkoutGroup = workoutGroupRepository.findWorkoutGroupById(workoutGroupId)!!

            _uiState.value = _uiState.value.copy(
                toWorkoutGroup = toWorkoutGroup,
                title = getTitle(toWorkoutGroup),
                name = _uiState.value.name.copy(value = getWorkoutGroupNameOrDefault(toWorkoutGroup)),
                dayWeek = _uiState.value.dayWeek.copy(value = toWorkoutGroup.dayWeek?.getFirstPartFullDisplayName()!!)
            )
        }
    }

    fun onSave(onSuccess: () -> Unit) {
        launch {
            val validationResults = editWorkoutGroupUseCase(_uiState.value.toWorkoutGroup)

            if (validationResults.isEmpty()) {
                onSuccess()
            } else {
                _uiState.value.onToggleLoading()
                showFieldsValidationMessages(validationResults.toMutableList())
            }
        }
    }

    private fun showFieldsValidationMessages(validationResults: List<FieldValidationError<EnumValidatedWorkoutGroupFields>>) {
        validationResults.forEach {
            when (it.field!!) {
                EnumValidatedWorkoutGroupFields.GROUP_NAME -> {
                    _uiState.value = _uiState.value.copy(
                        name = _uiState.value.name.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedWorkoutGroupFields.DAY_WEEK -> {
                    _uiState.value = _uiState.value.copy(
                        dayWeek = _uiState.value.dayWeek.copy(
                            errorMessage = it.message
                        )
                    )
                }
            }
        }
    }

    fun onInactivate(onSuccess: () -> Unit) {
        _uiState.value.messageDialogState.onShowDialog?.showConfirmationDialog(
            message = context.getString(
                R.string.workout_group_inactivate_dialog_message,
                getWorkoutGroupNameOrDefault(_uiState.value.toWorkoutGroup)
            )
        ) {
            _uiState.value.onToggleLoading()

            launch {
                inactivateWorkoutGroupUseCase(_uiState.value.toWorkoutGroup.id!!)
                onSuccess()
            }
        }
    }

    private fun initialLoadUIState() {
        _uiState.update {
            it.copy(
                name = initializeTextFieldName(),
                dayWeek = initializeDropDownTextFieldDayWeek(),
                messageDialogState = initializeMessageDialogState(),
                onToggleLoading = {
                    _uiState.value = _uiState.value.copy(
                        showLoading = _uiState.value.showLoading.not()
                    )
                }
            )
        }
    }

    private fun initializeTextFieldName(): TextField {
        return TextField(
            onChange = { text ->
                _uiState.value = _uiState.value.copy(
                    name = _uiState.value.name.copy(
                        value = text,
                        errorMessage = ""
                    ),
                    toWorkoutGroup = _uiState.value.toWorkoutGroup.copy(name = text.ifEmpty { null })
                )
            }
        )
    }

    private fun initializeDropDownTextFieldDayWeek(): DropDownTextField<DayOfWeek> {
        val items = DayOfWeek.entries.map {
            MenuItem<DayOfWeek?>(
                value = it,
                label = it.getFirstPartFullDisplayName()
            )
        }

        return DropDownTextField(
            dataList = items,
            dataListFiltered = items,
            onDropDownDismissRequest = {
                _uiState.value = _uiState.value.copy(
                    dayWeek = _uiState.value.dayWeek.copy(expanded = false)
                )
            },
            onDropDownExpandedChange = {
                _uiState.value = _uiState.value.copy(
                    dayWeek = _uiState.value.dayWeek.copy(expanded = it)
                )
            },
            onDataListItemClick = {
                _uiState.value = _uiState.value.copy(
                    dayWeek = _uiState.value.dayWeek.copy(
                        value = it.getLabelOrEmptyIfNullValue(),
                        errorMessage = ""
                    ),
                    toWorkoutGroup = _uiState.value.toWorkoutGroup.copy(
                        dayWeek = it.value
                    )
                )

                _uiState.value.dayWeek.onDropDownDismissRequest()
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

    private fun getTitle(toWorkoutGroup: TOWorkoutGroup): String {
        val name = getWorkoutGroupNameOrDefault(toWorkoutGroup)
        return context.getString(R.string.workout_group_edit_dialog_title, name)
    }

    private fun getWorkoutGroupNameOrDefault(toWorkoutGroup: TOWorkoutGroup): String {
        return toWorkoutGroup.name ?: context.getString(R.string.workout_group_default_name)
    }


}