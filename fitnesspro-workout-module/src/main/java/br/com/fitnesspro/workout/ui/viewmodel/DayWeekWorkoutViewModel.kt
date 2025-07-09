package br.com.fitnesspro.workout.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.FitnessProViewModel
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.core.extensions.getFirstPartFullDisplayName
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.workout.repository.WorkoutGroupRepository
import br.com.fitnesspro.workout.ui.navigation.DayWeekWorkoutScreenArgs
import br.com.fitnesspro.workout.ui.navigation.dayWeekWorkoutScreenArguments
import br.com.fitnesspro.workout.ui.screen.dayweek.workout.decorator.WorkoutGroupDecorator
import br.com.fitnesspro.workout.ui.state.DayWeekWorkoutUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class DayWeekWorkoutViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val globalEvents: GlobalEvents,
    private val workoutGroupRepository: WorkoutGroupRepository,
    savedStateHandle: SavedStateHandle
): FitnessProViewModel() {

    private val _uiState: MutableStateFlow<DayWeekWorkoutUIState> = MutableStateFlow(DayWeekWorkoutUIState())
    val uiState get() = _uiState.asStateFlow()

    private val jsonArgs: String? = savedStateHandle[dayWeekWorkoutScreenArguments]

    init {
        initialLoadUIState()
        loadUIStateWithDatabaseInfos()
    }

    private fun initialLoadUIState() {
        _uiState.value = _uiState.value.copy(
            messageDialogState = initializeMessageDialogState()
        )
    }

    private fun loadUIStateWithDatabaseInfos() {
        launch {
            val args = jsonArgs?.fromJsonNavParamToArgs(DayWeekWorkoutScreenArgs::class.java)!!
            val groups = workoutGroupRepository.getListWorkoutGroupDecorator(
                workoutId = args.workoutId,
                dayOfWeek = args.dayWeek
            )

            _uiState.value = _uiState.value.copy(
                dayWeekWorkoutGroups = groups,
                title = getTitle(args.dayWeek),
                subtitle = getSubtitle(groups)
            )
        }
    }

    private fun getTitle(dayWeek: DayOfWeek): String {
        return context.getString(R.string.day_week_workout_title, dayWeek.getFirstPartFullDisplayName())
    }

    private fun getSubtitle(groups: List<WorkoutGroupDecorator>): String {
        return groups.joinToString { it.label }
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

    override fun getGlobalEventsBus(): GlobalEvents = globalEvents

    override fun getErrorMessageFrom(throwable: Throwable): String {
        return context.getString(R.string.unknown_error_message)
    }

    override fun onShowErrorDialog(message: String) {
        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(message = message)
    }
}