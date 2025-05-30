package br.com.fitnesspro.workout.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.FitnessProViewModel
import br.com.fitnesspro.compose.components.fields.menu.MenuItem
import br.com.fitnesspro.compose.components.fields.menu.getLabelOrEmptyIfNullValue
import br.com.fitnesspro.compose.components.fields.state.DialogListState
import br.com.fitnesspro.compose.components.fields.state.DialogListTextField
import br.com.fitnesspro.compose.components.fields.state.DropDownTextField
import br.com.fitnesspro.compose.components.fields.state.PagedDialogListState
import br.com.fitnesspro.compose.components.fields.state.PagedDialogListTextField
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.core.extensions.getFirstPartFullDisplayName
import br.com.fitnesspro.core.extensions.toIntOrNull
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.core.validation.FieldValidationTypedError
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.to.TOWorkoutGroup
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.repository.ExercisePreDefinitionRepository
import br.com.fitnesspro.workout.repository.ExerciseRepository
import br.com.fitnesspro.workout.repository.WorkoutGroupRepository
import br.com.fitnesspro.workout.repository.WorkoutRepository
import br.com.fitnesspro.workout.ui.navigation.ExerciseScreenArgs
import br.com.fitnesspro.workout.ui.navigation.exerciseScreenArguments
import br.com.fitnesspro.workout.ui.state.ExerciseUIState
import br.com.fitnesspro.workout.usecase.EnumValidatedExerciseFields
import br.com.fitnesspro.workout.usecase.EnumValidatedExerciseType
import br.com.fitnesspro.workout.usecase.SaveExerciseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val globalEvents: GlobalEvents,
    private val exerciseRepository: ExerciseRepository,
    private val personRepository: PersonRepository,
    private val exercisePreDefinitionRepository: ExercisePreDefinitionRepository,
    private val workoutGroupRepository: WorkoutGroupRepository,
    private val workoutRepository: WorkoutRepository,
    private val saveExerciseUseCase: SaveExerciseUseCase,
    savedStateHandle: SavedStateHandle
): FitnessProViewModel() {

    private val _uiState: MutableStateFlow<ExerciseUIState> = MutableStateFlow(ExerciseUIState())
    val uiState get() = _uiState.asStateFlow()

    private val jsonArgs: String? = savedStateHandle[exerciseScreenArguments]

    init {
        initialLoadUIState()
        loadUIStateWithDatabaseInfos()
    }

    override fun getGlobalEventsBus(): GlobalEvents = globalEvents

    override fun getErrorMessageFrom(throwable: Throwable): String {
        return context.getString(br.com.fitnesspro.common.R.string.unknown_error_message)
    }

    override fun onShowErrorDialog(message: String) {
        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(message = message)
    }

    private fun initialLoadUIState() {
        val args = jsonArgs?.fromJsonNavParamToArgs(ExerciseScreenArgs::class.java)!!

        _uiState.update {
            it.copy(
                group = initializeDialogListTextFieldGroup(),
                exercise = initializePagedDialogListTextFieldExercise(),
                sets = initializeTextFieldSets(),
                reps = initializeTextFieldReps(),
                rest = initializeTextFieldRest(),
                unitRest = initializeDropDownTextFieldUnitRest(),
                duration = initializeTextFieldDuration(),
                unitDuration = initializeDropDownTextFieldUnitDuration(),
                observation = initializeTextFieldObservation(),
                messageDialogState = initializeMessageDialogState(),
                toExercise = _uiState.value.toExercise.copy(workoutId = args.workoutId)
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

    private fun initializeDialogListTextFieldGroup(): DialogListTextField<TOWorkoutGroup> {
        return DialogListTextField(
            dialogListState = DialogListState(
                dialogTitle = context.getString(R.string.exercise_screen_group_dialog_list_title),
                onShow = {
                    _uiState.value = _uiState.value.copy(
                        group = _uiState.value.group.copy(
                            dialogListState = _uiState.value.group.dialogListState.copy(show = true)
                        )
                    )
                },
                onHide = {
                    _uiState.value = _uiState.value.copy(
                        group = _uiState.value.group.copy(
                            dialogListState = _uiState.value.group.dialogListState.copy(show = false)
                        )
                    )
                },
                onDataListItemClick = { item ->
                    _uiState.value = _uiState.value.copy(
                        group = _uiState.value.group.copy(
                            value = item.getLabel(),
                            errorMessage = ""
                        ),
                        toExercise = _uiState.value.toExercise.copy(
                            workoutGroupId = item.id,
                            workoutGroupName = item.name
                        )
                    )

                    _uiState.value.group.dialogListState.onHide()
                },
                onSimpleFilterChange = { filter ->
                    launch {
                        _uiState.value = _uiState.value.copy(
                            group = _uiState.value.group.copy(
                                dialogListState = _uiState.value.group.dialogListState.copy(
                                    dataList = getListWorkoutGroups(simpleFilter = filter)
                                )
                            )
                        )
                    }
                },
            ),
            onChange = {
                _uiState.value = _uiState.value.copy(
                    group = _uiState.value.group.copy(
                        value = it,
                    ),
                    toExercise = _uiState.value.toExercise.copy(
                        workoutGroupId = null,
                        workoutGroupName = it
                    )
                )
            }
        )
    }

    private suspend fun getListWorkoutGroups(simpleFilter: String): List<TOWorkoutGroup> {
        val args = jsonArgs?.fromJsonNavParamToArgs(ExerciseScreenArgs::class.java)!!

        return workoutGroupRepository.getWorkoutGroupsFromWorkout(
            workoutId = args.workoutId,
            dayOfWeek = args.dayWeek,
            workoutGroupId = args.workoutGroupId,
            simpleFilter = simpleFilter
        )
    }

    private fun initializePagedDialogListTextFieldExercise(): PagedDialogListTextField<TOExercise> {
        return PagedDialogListTextField(
            dialogListState = PagedDialogListState(
                dialogTitle = context.getString(R.string.exercise_screen_exercise_dialog_list_title),
                onShow = {
                    _uiState.value = _uiState.value.copy(
                        exercise = _uiState.value.exercise.copy(
                            dialogListState = _uiState.value.exercise.dialogListState.copy(show = true)
                        )
                    )
                },
                onHide = {
                    _uiState.value = _uiState.value.copy(
                        exercise = _uiState.value.exercise.copy(
                            dialogListState = _uiState.value.exercise.dialogListState.copy(show = false)
                        )
                    )
                },
                onDataListItemClick = { item ->
                    _uiState.value = _uiState.value.copy(
                        exercise = _uiState.value.exercise.copy(
                            value = item.getLabel(),
                            errorMessage = ""
                        ),
                        toExercise = item
                    )

                    _uiState.value.exercise.dialogListState.onHide()
                },
                onSimpleFilterChange = { filter ->
                    _uiState.value = _uiState.value.copy(
                        exercise = _uiState.value.exercise.copy(
                            dialogListState = _uiState.value.exercise.dialogListState.copy(
                                dataList = getListExercisesPreDefinition(simpleFilter = filter)
                            )
                        )
                    )
                },
            ),
            onChange = { newText ->
                if (_uiState.value.toExercise.id != null) {
                    _uiState.value = _uiState.value.copy(
                        toExercise = TOExercise()
                    )
                }

                _uiState.value = _uiState.value.copy(
                    exercise = _uiState.value.exercise.copy(
                        value = newText,
                        errorMessage = ""
                    ),
                    toExercise = _uiState.value.toExercise.copy(
                        name = newText
                    )
                )
            }
        )
    }

    private fun getListExercisesPreDefinition(simpleFilter: String): Flow<PagingData<TOExercise>> {
        return exercisePreDefinitionRepository.getExercisesPreDefinitionFromWorkoutGroup(
            workoutGroupName = _uiState.value.exercise.value,
            authenticatedPersonId = _uiState.value.authenticatedPerson.id!!,
            simpleFilter = simpleFilter
        ).flow
    }

    private fun initializeTextFieldSets(): TextField {
        return TextField(
           onChange = {
               _uiState.value = _uiState.value.copy(
                   sets = _uiState.value.sets.copy(
                       value = it,
                       errorMessage = ""
                   ),
                   toExercise = _uiState.value.toExercise.copy(sets = it.toIntOrNull())
               )
           }
        )
    }

    private fun initializeTextFieldReps(): TextField {
        return TextField(
            onChange = {
                _uiState.value = _uiState.value.copy(
                    reps = _uiState.value.reps.copy(
                        value = it,
                        errorMessage = ""
                    ),
                    toExercise = _uiState.value.toExercise.copy(repetitions = it.toIntOrNull())
                )
            }
        )
    }

    private fun initializeTextFieldRest(): TextField {
        return TextField(
            onChange = {
                _uiState.value = _uiState.value.copy(
                    rest = _uiState.value.rest.copy(
                        value = it,
                        errorMessage = ""
                    )
                )
            }
        )
    }

    private fun initializeDropDownTextFieldUnitRest(): DropDownTextField<ChronoUnit?> {
        val items = getChronoUnitMenuItems()

        return DropDownTextField(
            dataList = items,
            dataListFiltered = items,
            onDropDownDismissRequest = {
                _uiState.value = _uiState.value.copy(
                    unitRest = _uiState.value.unitRest.copy(expanded = false)
                )
            },
            onDropDownExpandedChange = {
                _uiState.value = _uiState.value.copy(
                    unitRest = _uiState.value.unitRest.copy(expanded = it)
                )
            },
            onDataListItemClick = {
                _uiState.value = _uiState.value.copy(
                    unitRest = _uiState.value.unitRest.copy(
                        value = it.getLabelOrEmptyIfNullValue(),
                        errorMessage = ""
                    )
                )

                _uiState.value.unitRest.onDropDownDismissRequest()
            }
        )
    }

    private fun initializeTextFieldDuration(): TextField {
        return TextField(
            onChange = {
                _uiState.value = _uiState.value.copy(
                    duration = _uiState.value.duration.copy(
                        value = it,
                        errorMessage = ""
                    )
                )
            }
        )
    }

    private fun initializeDropDownTextFieldUnitDuration(): DropDownTextField<ChronoUnit?> {
        val items = getChronoUnitMenuItems()

        return DropDownTextField(
            dataList = items,
            dataListFiltered = items,
            onDropDownDismissRequest = {
                _uiState.value = _uiState.value.copy(
                    unitDuration = _uiState.value.unitDuration.copy(expanded = false)
                )
            },
            onDropDownExpandedChange = {
                _uiState.value = _uiState.value.copy(
                    unitDuration = _uiState.value.unitDuration.copy(expanded = it)
                )
            },
            onDataListItemClick = {
                _uiState.value = _uiState.value.copy(
                    unitDuration = _uiState.value.unitDuration.copy(
                        value = it.getLabelOrEmptyIfNullValue(),
                        errorMessage = ""
                    )
                )

                _uiState.value.unitDuration.onDropDownDismissRequest()
            }
        )
    }

    private fun getChronoUnitMenuItems(): List<MenuItem<ChronoUnit?>> {
        val units = ChronoUnit.entries.slice(ChronoUnit.SECONDS.ordinal..ChronoUnit.HOURS.ordinal)

        return units.map { unit ->
            MenuItem(
                label = getLabelFromChronoUnit(unit),
                value = unit
            )
        }
    }

    private fun initializeTextFieldObservation(): TextField {
        return TextField(
            onChange = {
                _uiState.value = _uiState.value.copy(
                    observation = _uiState.value.observation.copy(
                        value = it,
                        errorMessage = ""
                    )
                )
            }
        )
    }

    private fun loadUIStateWithDatabaseInfos() {
        loadTopBarInfos()
        loadWorkoutGroups()
    }

    private fun loadTopBarInfos() {
        launch {
            val args = jsonArgs?.fromJsonNavParamToArgs(ExerciseScreenArgs::class.java)!!
            val authenticatedPerson = personRepository.getAuthenticatedTOPerson()!!
            val workout = workoutRepository.findWorkoutById(args.workoutId)

            _uiState.value = _uiState.value.copy(
                title = getTitle(),
                subtitle = context.getString(
                    R.string.exercise_screen_subtitle,
                    workout?.memberName
                ),
                authenticatedPerson = authenticatedPerson
            )
        }
    }

    private fun loadWorkoutGroups() {
        launch {
            val args = jsonArgs?.fromJsonNavParamToArgs(ExerciseScreenArgs::class.java)!!
            val groups = workoutGroupRepository.getWorkoutGroupsFromWorkout(
                workoutId = args.workoutId,
                dayOfWeek = args.dayWeek,
                workoutGroupId = args.workoutGroupId
            )

            _uiState.value = _uiState.value.copy(
                group = _uiState.value.group.copy(
                    dialogListState = _uiState.value.group.dialogListState.copy(
                        dataList = groups
                    )
                )
            )

            groups.firstOrNull { it.id == args.workoutGroupId }?.let { selectedGroup ->
                _uiState.value = _uiState.value.copy(
                    group = _uiState.value.group.copy(
                        value = selectedGroup.name!!
                    ),
                    toExercise = _uiState.value.toExercise.copy(
                        workoutGroupId = selectedGroup.id,
                        workoutGroupName = selectedGroup.name
                    )
                )
            }
        }
    }

    private fun getLabelFromChronoUnit(unit: ChronoUnit): String {
        return when (unit) {
            ChronoUnit.SECONDS -> context.getString(R.string.exercise_screen_chrono_unit_seconds)
            ChronoUnit.MINUTES -> context.getString(R.string.exercise_screen_chrono_unit_minutes)
            ChronoUnit.HOURS -> context.getString(R.string.exercise_screen_chrono_unit_hours)
            else -> throw IllegalArgumentException("Valor invalido para recuperar um label de ChronoUnit")
        }
    }

    private suspend fun getTitle(): String {
        val args = jsonArgs?.fromJsonNavParamToArgs(ExerciseScreenArgs::class.java)!!

        return if (_uiState.value.toExercise.id != null) {
            val exercise = exerciseRepository.findById(_uiState.value.toExercise.id!!)

            if (args.dayWeek != null) {
                "${exercise.name} - ${args.dayWeek.getFirstPartFullDisplayName()}"
            } else {
                exercise.name!!
            }

        } else {
            if (args.dayWeek != null) {
                context.getString(
                    R.string.exercise_screen_title_new_from_day_week,
                    args.dayWeek.getFirstPartFullDisplayName()
                )
            } else {
                context.getString(R.string.exercise_screen_title_new)
            }
        }
    }

    fun saveExercise(onSuccess: () -> Unit) {
        launch {
            val validationResults = saveExerciseUseCase(_uiState.value.toExercise)

            if (validationResults.isEmpty()) {
                onSuccess()
            } else {
                _uiState.value.onToggleLoading()
                showFieldsValidationMessages(validationResults.toMutableList())
            }
        }
    }

    private fun showFieldsValidationMessages(validationResults: MutableList<FieldValidationTypedError<EnumValidatedExerciseFields, EnumValidatedExerciseType>>) {
        val dialogValidations = validationResults.filter(::isGlobalFieldsValidation)
        validationResults.removeAll(::isGlobalFieldsValidation)

        if (dialogValidations.isNotEmpty()) {
            _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(dialogValidations.first().message)
            return
        }

        val fieldValidations = validationResults.filter(::isFieldValidation)
        validationResults.removeAll(::isFieldValidation)

        fieldValidations.forEach {
            when (it.field!!) {
                EnumValidatedExerciseFields.EXERCISE_GROUP -> {
                    _uiState.value = _uiState.value.copy(
                        group = _uiState.value.group.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedExerciseFields.EXERCISE -> {
                    _uiState.value = _uiState.value.copy(
                        exercise = _uiState.value.exercise.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedExerciseFields.SETS -> {
                    _uiState.value = _uiState.value.copy(
                        sets = _uiState.value.sets.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedExerciseFields.REPETITIONS -> {
                    _uiState.value = _uiState.value.copy(
                        reps = _uiState.value.reps.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedExerciseFields.REST -> {
                    _uiState.value = _uiState.value.copy(
                        rest = _uiState.value.rest.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedExerciseFields.UNIT_REST -> {
                    _uiState.value = _uiState.value.copy(
                        unitRest = _uiState.value.unitRest.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedExerciseFields.DURATION -> {
                    _uiState.value = _uiState.value.copy(
                        duration = _uiState.value.duration.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedExerciseFields.UNIT_DURATION -> {
                    _uiState.value = _uiState.value.copy(
                        unitDuration = _uiState.value.unitDuration.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedExerciseFields.OBSERVATION -> {
                    _uiState.value = _uiState.value.copy(
                        observation = _uiState.value.observation.copy(
                            errorMessage = it.message
                        )
                    )
                }
            }
        }

        if (fieldValidations.isEmpty()) {
            val typedValidations = validationResults.filter(::isTypedValidation)

            // TODO - Exibir essas validações de algum jeito
        }
    }

    private fun isTypedValidation(error: FieldValidationTypedError<EnumValidatedExerciseFields, EnumValidatedExerciseType>): Boolean {
        return error.type != null
    }

    private fun isFieldValidation(error: FieldValidationTypedError<EnumValidatedExerciseFields, EnumValidatedExerciseType>): Boolean {
        return error.field != null
    }

    private fun isGlobalFieldsValidation(error: FieldValidationTypedError<EnumValidatedExerciseFields, EnumValidatedExerciseType>): Boolean {
        return error.field == null && error.type == null
    }
}