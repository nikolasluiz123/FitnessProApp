package br.com.fitnesspro.workout.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.FitnessProViewModel
import br.com.fitnesspro.compose.components.filter.SimpleFilterState
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.dateNow
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.to.TOWorkout
import br.com.fitnesspro.to.TOWorkoutGroup
import br.com.fitnesspro.workout.repository.WorkoutGroupRepository
import br.com.fitnesspro.workout.repository.WorkoutRepository
import br.com.fitnesspro.workout.ui.navigation.DayWeekExercisesScreenArgs
import br.com.fitnesspro.workout.ui.navigation.dayWeekExercisesScreenArguments
import br.com.fitnesspro.workout.ui.screen.dayweek.exercices.decorator.DayWeekExercicesGroupDecorator
import br.com.fitnesspro.workout.ui.state.DayWeekExercisesUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class DayWeekExercisesViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val globalEvents: GlobalEvents,
    private val workoutRepository: WorkoutRepository,
    private val workoutGroupRepository: WorkoutGroupRepository,
    savedStateHandle: SavedStateHandle
): FitnessProViewModel() {

    private val _uiState: MutableStateFlow<DayWeekExercisesUIState> = MutableStateFlow(DayWeekExercisesUIState())
    val uiState get() = _uiState.asStateFlow()

    private val jsonArgs: String? = savedStateHandle[dayWeekExercisesScreenArguments]

    init {
        initialLoadUIState()
        loadUIStateWithDatabaseInfos()
    }

    override fun getGlobalEventsBus(): GlobalEvents = globalEvents

    override fun getErrorMessageFrom(throwable: Throwable): String {
        return context.getString(R.string.unknown_error_message)
    }

    override fun onShowErrorDialog(message: String) {
        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(message = message)
    }

    private fun initialLoadUIState() {
        _uiState.update { currentState ->
            currentState.copy(
                simpleFilterState = initializeSimpleFilterState(),
                messageDialogState = initializeMessageDialogState(),
                onShowWorkoutGroupEditDialog = {
                    _uiState.value = _uiState.value.copy(
                        showWorkoutGroupEditDialog = true
                    )
                },
                onDismissWorkoutGroupEditDialog = {
                    _uiState.value = _uiState.value.copy(
                        showWorkoutGroupEditDialog = false,
                        workoutGroupIdEdited = null
                    )
                }
            )
        }
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

    fun onChangeWorkoutGroup(workoutGroupId: String, onLoaded: (TOWorkoutGroup) -> Unit) {
        launch {
            workoutGroupRepository.findWorkoutGroupById(workoutGroupId)?.let(onLoaded)
        }
    }

    /**
     * Filtra a lista de [DayWeekExercicesGroupDecorator] de acordo com o termo informado, mantendo a estrutura hierárquica
     * e aplicando as seguintes regras:
     *
     * 1. **Filtro no nível DayWeekExercicesGroupDecorator (`label`):**
     *    - Se o `label` do `DayWeekExercicesGroupDecorator` contiver o termo (`quickFilter`), o objeto é incluído **completo** no resultado,
     *      incluindo todos os seus `WorkoutGroupDecorator` e `TOExercise`.
     *
     * 2. **Filtro no nível WorkoutGroupDecorator (`label`):**
     *    - Se não houver correspondência no `label` do `DayWeekExercicesGroupDecorator`, cada `WorkoutGroupDecorator` é avaliado.
     *    - Se o `label` do `WorkoutGroupDecorator` contiver o termo, o objeto é incluído **completo**, com todos os seus `TOExercise`.
     *
     * 3. **Filtro no nível TOExercise (`name`):**
     *    - Se não houver correspondência nos níveis superiores, cada `TOExercise` é avaliado.
     *    - Se o `name` do `TOExercise` contiver o termo, apenas esses exercícios correspondentes são incluídos, preservando a hierarquia:
     *      DayWeekExercicesGroupDecorator → WorkoutGroupDecorator → TOExercise.
     *
     * @param quickFilter Termo usado para buscar correspondências nos `label` e `name` dos objetos.
     *
     * @return Lista contendo apenas os elementos que possuem alguma correspondência com o termo, respeitando a hierarquia e as regras de propagação.
     *
     * ## Exemplo de uso:
     * ```
     * val filteredList = originalList.filter("Peito")
     * ```
     *
     * O resultado conterá:
     * - `DayWeekExercicesGroupDecorator` cujo `label` contenha "Peito" (completo).
     * - `WorkoutGroupDecorator` cujo `label` contenha "Peito" (completo).
     * - `TOExercise` cujo `name` contenha "Peito" (parcial, com hierarquia preservada).
     *
     * ## Considerações:
     * - Busca `contains` com `ignoreCase = true` para maior flexibilidade.
     * - Utiliza `copy` para criar novos objetos filtrados, evitando efeitos colaterais.
     * - Evita `NullPointerException` ao verificar `exercise.name`.
     */
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