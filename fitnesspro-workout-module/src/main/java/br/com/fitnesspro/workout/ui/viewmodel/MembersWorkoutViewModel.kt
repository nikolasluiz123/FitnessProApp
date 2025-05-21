package br.com.fitnesspro.workout.ui.viewmodel

import android.content.Context
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.FitnessProViewModel
import br.com.fitnesspro.compose.components.filter.SimpleFilterState
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.workout.repository.WorkoutRepository
import br.com.fitnesspro.workout.ui.state.MembersWorkoutUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MembersWorkoutViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val globalEvents: GlobalEvents,
    private val workoutRepository: WorkoutRepository
): FitnessProViewModel() {

    private val _uiState: MutableStateFlow<MembersWorkoutUIState> = MutableStateFlow(MembersWorkoutUIState())
    val uiState get() = _uiState.asStateFlow()

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
        _uiState.update { currentState ->
            currentState.copy(
                simpleFilterState = initializeSimpleFilterState(),
                messageDialogState = initializeMessageDialogState(),
            )
        }
    }

    private fun initializeSimpleFilterState(): SimpleFilterState {
        return SimpleFilterState(
            onSimpleFilterChange = {
                _uiState.value = _uiState.value.copy(
                    simpleFilterState = _uiState.value.simpleFilterState.copy(
                        quickFilter = it
                    )
                )

                launch {
                    _uiState.value = _uiState.value.copy(
                        workouts = workoutRepository.getListWorkout(quickFilter = it)
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
        launch {
            _uiState.value = _uiState.value.copy(
                workouts = workoutRepository.getListWorkout()
            )
        }
    }
}