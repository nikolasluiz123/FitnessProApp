package br.com.fitnesspro.workout.ui.viewmodel

import android.content.Context
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.FitnessProViewModel
import br.com.fitnesspro.compose.components.fields.menu.MenuItem
import br.com.fitnesspro.compose.components.fields.menu.getLabelOrEmptyIfNullValue
import br.com.fitnesspro.compose.components.fields.state.DropDownTextField
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.compose.components.filter.SimpleFilterState
import br.com.fitnesspro.core.callback.showConfirmationDialog
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.dateNow
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.core.extensions.getFirstPartFullDisplayName
import br.com.fitnesspro.core.extensions.toIntOrNull
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.to.TOWorkout
import br.com.fitnesspro.to.TOWorkoutGroup
import br.com.fitnesspro.workout.repository.WorkoutGroupRepository
import br.com.fitnesspro.workout.repository.WorkoutRepository
import br.com.fitnesspro.workout.ui.navigation.DayWeekExercisesScreenArgs
import br.com.fitnesspro.workout.ui.navigation.dayWeekExercisesScreenArguments
import br.com.fitnesspro.workout.ui.screen.dayweek.exercices.decorator.DayWeekExercicesGroupDecorator
import br.com.fitnesspro.workout.ui.state.DayWeekExercisesUIState
import br.com.fitnesspro.workout.ui.state.WorkoutGroupEditDialogUIState
import br.com.fitnesspro.workout.usecase.workout.EditWorkoutGroupUseCase
import br.com.fitnesspro.workout.usecase.workout.EnumValidatedWorkoutGroupFields
import br.com.fitnesspro.workout.usecase.workout.InactivateWorkoutGroupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.DayOfWeek
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class DayWeekExercisesViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val globalEvents: GlobalEvents,
    private val workoutRepository: WorkoutRepository,
    private val workoutGroupRepository: WorkoutGroupRepository,
    private val editWorkoutGroupUseCase: EditWorkoutGroupUseCase,
    private val inactivateWorkoutGroupUseCase: InactivateWorkoutGroupUseCase,
    savedStateHandle: SavedStateHandle
): FitnessProViewModel() {

    private val _uiState: MutableStateFlow<DayWeekExercisesUIState> = MutableStateFlow(DayWeekExercisesUIState())
    val uiState get() = _uiState.asStateFlow()

    private val jsonArgs: String? = savedStateHandle[dayWeekExercisesScreenArguments]

    init {
        initialLoadUIState()
        loadUIStateWithDatabaseInfos()
    }

    override fun onError(throwable: Throwable) {
        super.onError(throwable)

        if (_uiState.value.showLoading) {
            _uiState.value.onToggleLoading()
        }

        if (_uiState.value.workoutGroupEditDialogUIState.showLoading) {
            _uiState.value.workoutGroupEditDialogUIState.onToggleLoading()
        }
    }

    override fun getGlobalEventsBus(): GlobalEvents = globalEvents

    override fun getErrorMessageFrom(throwable: Throwable): String {
        return context.getString(R.string.unknown_error_message)
    }

    override fun onShowErrorDialog(message: String) {
        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(message = message)
    }

    private fun initialLoadUIState() {
        _uiState.value = _uiState.value.copy(
            simpleFilterState = initializeSimpleFilterState(),
            messageDialogState = initializeMessageDialogState(),
            workoutGroupEditDialogUIState = WorkoutGroupEditDialogUIState(
                name = initializeEditWorkoutGroupTextFieldName(),
                order = initializeEditWorkoutGroupTextFieldOrder(),
                dayWeek = initializeEditWorkoutGroupDropDownTextFieldDayWeek(),
                onShowDialogChange = { show ->
                    _uiState.value = _uiState.value.copy(
                        workoutGroupEditDialogUIState = _uiState.value.workoutGroupEditDialogUIState.copy(
                            showDialog = show,
                        )
                    )

                    if (!show) {
                        _uiState.value = _uiState.value.copy(workoutGroupIdEdited = null)
                    }
                },
                onToggleLoading = {
                    _uiState.value = _uiState.value.copy(
                        workoutGroupEditDialogUIState = _uiState.value.workoutGroupEditDialogUIState.copy(
                            showLoading = !_uiState.value.workoutGroupEditDialogUIState.showLoading
                        )
                    )
                }
            )
        )
    }

    private fun initializeEditWorkoutGroupTextFieldName(): TextField {
        return TextField(
            onChange = { text ->
                _uiState.value = _uiState.value.copy(
                    workoutGroupEditDialogUIState = _uiState.value.workoutGroupEditDialogUIState.copy(
                        name = _uiState.value.workoutGroupEditDialogUIState.name.copy(
                            value = text,
                            errorMessage = ""
                        ),
                        toWorkoutGroup = _uiState.value.workoutGroupEditDialogUIState.toWorkoutGroup.copy(
                            name = text.ifEmpty { null }
                        )
                    )
                )
            }
        )
    }

    private fun initializeEditWorkoutGroupTextFieldOrder(): TextField {
        return TextField(
            onChange = { text ->
                if (text.isDigitsOnly()) {
                    _uiState.value = _uiState.value.copy(
                        workoutGroupEditDialogUIState = _uiState.value.workoutGroupEditDialogUIState.copy(
                            order = _uiState.value.workoutGroupEditDialogUIState.order.copy(
                                value = text,
                                errorMessage = ""
                            ),
                            toWorkoutGroup = _uiState.value.workoutGroupEditDialogUIState.toWorkoutGroup.copy(
                                order = text.toIntOrNull()
                            )
                        )
                    )
                }
            }
        )
    }

    private fun initializeEditWorkoutGroupDropDownTextFieldDayWeek(): DropDownTextField<DayOfWeek> {
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
                    workoutGroupEditDialogUIState = _uiState.value.workoutGroupEditDialogUIState.copy(
                        dayWeek = _uiState.value.workoutGroupEditDialogUIState.dayWeek.copy(expanded = false)
                    ),
                )
            },
            onDropDownExpandedChange = {
                _uiState.value = _uiState.value.copy(
                    workoutGroupEditDialogUIState = _uiState.value.workoutGroupEditDialogUIState.copy(
                        dayWeek = _uiState.value.workoutGroupEditDialogUIState.dayWeek.copy(expanded = it)
                    )
                )
            },
            onDataListItemClick = {
                _uiState.value = _uiState.value.copy(
                    workoutGroupEditDialogUIState = _uiState.value.workoutGroupEditDialogUIState.copy(
                        dayWeek = _uiState.value.workoutGroupEditDialogUIState.dayWeek.copy(
                            value = it.getLabelOrEmptyIfNullValue(),
                            errorMessage = ""
                        ),
                        toWorkoutGroup = _uiState.value.workoutGroupEditDialogUIState.toWorkoutGroup.copy(
                            dayWeek = it.value
                        )
                    )
                )

                _uiState.value.workoutGroupEditDialogUIState.dayWeek.onDropDownDismissRequest()
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

                _uiState.value = _uiState.value.copy(
                    filteredGroups = _uiState.value.groups.toMutableList().filter(filterText)
                )
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

    private fun loadUIStateWithDatabaseInfos() {
        val args = jsonArgs?.fromJsonNavParamToArgs(DayWeekExercisesScreenArgs::class.java)!!

        loadWorkoutData(args)
        initialLoadGroupsList(args)
    }

    private fun loadWorkoutData(args: DayWeekExercisesScreenArgs) = launch {
        val toWorkout = workoutRepository.findWorkoutById(args.workoutId)

        _uiState.value = _uiState.value.copy(
            title = getTitle(toWorkout),
            subtitle = getSubtitle(toWorkout),
            workout = toWorkout,
            isOverDue = dateNow(ZoneOffset.UTC) > toWorkout?.dateEnd!!,
            deleteEnabled = toWorkout.active
        )
    }

    private fun initialLoadGroupsList(args: DayWeekExercisesScreenArgs) {
        launch {
            val groups = workoutGroupRepository.getListDayWeekExercisesGroupDecorator(workoutId = args.workoutId)

            _uiState.value = _uiState.value.copy(
                groups = groups,
                filteredGroups = groups
            )
        }
    }

    private fun getTitle(workout: TOWorkout?): String {
        return context.getString(R.string.day_week_exercises_title, workout?.memberName)
    }

    private fun getSubtitle(workout: TOWorkout?): String {
        return when {
            workout == null -> ""

            dateNow(ZoneOffset.UTC) > workout.dateEnd!! -> {
                context.getString(
                    R.string.day_week_exercises_subtitle_over_due,
                    workout.dateEnd!!.format(EnumDateTimePatterns.DATE)
                )
            }

            else -> {
                context.getString(
                    R.string.day_week_exercises_subtitle,
                    workout.dateStart!!.format(EnumDateTimePatterns.DATE),
                    workout.dateEnd!!.format(EnumDateTimePatterns.DATE)
                )
            }
        }
    }

    fun updateExercises() {
        launch {
            val args = jsonArgs?.fromJsonNavParamToArgs(DayWeekExercisesScreenArgs::class.java)!!
            val groups = workoutGroupRepository.getListDayWeekExercisesGroupDecorator(workoutId = args.workoutId)

            _uiState.value = _uiState.value.copy(
                groups = groups,
                filteredGroups = groups.toMutableList().filter(_uiState.value.simpleFilterState.quickFilter)
            )
        }
    }

    fun deleteWorkout() {
        _uiState.value.messageDialogState.onShowDialog?.showConfirmationDialog(
            message = context.getString(R.string.day_week_exercises_delete_confirmation_message),
            onConfirm = {
                launch {
                    // TODO - Chamar o UseCase para inativar o Workout, WorkoutGroup, Exercise, deletar VideoExercise
                }
            }
        )
    }

    private fun getEditWorkoutDialogTitle(toWorkoutGroup: TOWorkoutGroup): String {
        val name = getWorkoutGroupNameOrDefault(toWorkoutGroup)
        return context.getString(br.com.fitnesspro.workout.R.string.workout_group_edit_dialog_title, name)
    }

    private fun getWorkoutGroupNameOrDefault(toWorkoutGroup: TOWorkoutGroup): String {
        return toWorkoutGroup.name ?: context.getString(br.com.fitnesspro.workout.R.string.workout_group_default_name)
    }

    fun onLoadDataWorkoutGroupEdition() {
        launch {
            val toWorkoutGroup = workoutGroupRepository.findWorkoutGroupById(_uiState.value.workoutGroupIdEdited)!!

            _uiState.value = _uiState.value.copy(
                workoutGroupEditDialogUIState = _uiState.value.workoutGroupEditDialogUIState.copy(
                    toWorkoutGroup = toWorkoutGroup,
                    title = getEditWorkoutDialogTitle(toWorkoutGroup),
                    name = _uiState.value.workoutGroupEditDialogUIState.name.copy(value = getWorkoutGroupNameOrDefault(toWorkoutGroup)),
                    dayWeek = _uiState.value.workoutGroupEditDialogUIState.dayWeek.copy(value = toWorkoutGroup.dayWeek?.getFirstPartFullDisplayName()!!),
                    order = _uiState.value.workoutGroupEditDialogUIState.order.copy(value = toWorkoutGroup.order.toString()),
                    showLoading = false
                )
            )
        }
    }

    fun onSaveWorkoutGroup(onSuccess: () -> Unit) {
        launch {
            val validationResults = editWorkoutGroupUseCase(_uiState.value.workoutGroupEditDialogUIState.toWorkoutGroup)

            if (validationResults.isEmpty()) {
                onSuccess()
                updateExercises()
            } else {
                _uiState.value.onToggleLoading()
                showFieldsValidationMessagesWorkoutGroupDialog(validationResults.toMutableList())
            }
        }
    }

    private fun showFieldsValidationMessagesWorkoutGroupDialog(validationResults: List<FieldValidationError<EnumValidatedWorkoutGroupFields>>) {
        validationResults.forEach {
            when (it.field!!) {
                EnumValidatedWorkoutGroupFields.GROUP_NAME -> {
                    _uiState.value = _uiState.value.copy(
                        workoutGroupEditDialogUIState = _uiState.value.workoutGroupEditDialogUIState.copy(
                            name = _uiState.value.workoutGroupEditDialogUIState.name.copy(
                                errorMessage = it.message
                            )
                        )
                    )
                }

                EnumValidatedWorkoutGroupFields.DAY_WEEK -> {
                    _uiState.value = _uiState.value.copy(
                        workoutGroupEditDialogUIState = _uiState.value.workoutGroupEditDialogUIState.copy(
                            dayWeek = _uiState.value.workoutGroupEditDialogUIState.dayWeek.copy(
                                errorMessage = it.message
                            )
                        )
                    )
                }
            }
        }
    }

    fun onInactivateWorkoutGroup(onSuccess: () -> Unit) {
        _uiState.value.messageDialogState.onShowDialog?.showConfirmationDialog(
            message = context.getString(
                br.com.fitnesspro.workout.R.string.workout_group_inactivate_dialog_message,
                getWorkoutGroupNameOrDefault(_uiState.value.workoutGroupEditDialogUIState.toWorkoutGroup)
            )
        ) {
            _uiState.value.onToggleLoading()

            launch {
                inactivateWorkoutGroupUseCase(_uiState.value.workoutGroupEditDialogUIState.toWorkoutGroup.id!!)
                updateExercises()
                onSuccess()
            }
        }
    }

    fun List<DayWeekExercicesGroupDecorator>.filter(quickFilter: String): List<DayWeekExercicesGroupDecorator> {
        return this.mapNotNull { dayWeekGroup ->
            when {
                dayWeekGroup.label.contains(quickFilter, ignoreCase = true) -> {
                    dayWeekGroup
                }
                else -> {
                    val matchingWorkoutGroups = dayWeekGroup.items.mapNotNull { workoutGroup ->
                        when {
                            workoutGroup.label.contains(quickFilter, ignoreCase = true) -> {
                                workoutGroup
                            }
                            else -> {
                                val matchingExercises = workoutGroup.items.filter { exercise ->
                                    exercise.name?.contains(quickFilter, ignoreCase = true) == true
                                }
                                if (matchingExercises.isNotEmpty()) {
                                    workoutGroup.copy(items = matchingExercises)
                                } else {
                                    null
                                }
                            }
                        }
                    }

                    if (matchingWorkoutGroups.isNotEmpty()) {
                        dayWeekGroup.copy(items = matchingWorkoutGroups)
                    } else {
                        null
                    }
                }
            }
        }
    }
}