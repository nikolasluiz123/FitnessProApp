package br.com.fitnesspro.workout.ui.viewmodel

import android.content.Context
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.base.FitnessProStatefulViewModel
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.workout.repository.WorkoutRepository
import br.com.fitnesspro.workout.ui.state.MembersWorkoutUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MembersWorkoutViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val globalEvents: GlobalEvents,
    private val workoutRepository: WorkoutRepository
): FitnessProStatefulViewModel() {

    private val _uiState: MutableStateFlow<MembersWorkoutUIState> = MutableStateFlow(MembersWorkoutUIState())
    val uiState get() = _uiState.asStateFlow()

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
                        _uiState.value = _uiState.value.copy(
                            workouts = workoutRepository.getListWorkout(quickFilter = it)
                        )
                    }
                }
            ),
            messageDialogState = createMessageDialogState(
                getCurrentState = { _uiState.value.messageDialogState },
                updateState = { newState -> _uiState.value = _uiState.value.copy(messageDialogState = newState) }
            ),
        )
    }

    override fun getGlobalEventsBus(): GlobalEvents = globalEvents

    override fun getErrorMessageFrom(throwable: Throwable): String {
        return context.getString(br.com.fitnesspro.common.R.string.unknown_error_message)
    }

    override fun onShowErrorDialog(message: String) {
        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(message = message)
    }

    fun onUpdateWorkouts() {
        launch {
            _uiState.value = _uiState.value.copy(
                workouts = workoutRepository.getListWorkout()
            )
        }
    }
}