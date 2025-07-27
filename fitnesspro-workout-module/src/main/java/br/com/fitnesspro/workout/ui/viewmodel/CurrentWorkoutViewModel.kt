package br.com.fitnesspro.workout.ui.viewmodel

import android.content.Context
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.FitnessProViewModel
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.to.TOWorkout
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.repository.WorkoutRepository
import br.com.fitnesspro.workout.ui.state.CurrentWorkoutUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CurrentWorkoutViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val globalEvents: GlobalEvents,
    private val workoutRepository: WorkoutRepository
): FitnessProViewModel() {

    private val _uiState: MutableStateFlow<CurrentWorkoutUIState> = MutableStateFlow(CurrentWorkoutUIState())
    val uiState get() = _uiState.asStateFlow()

    init {
        initialLoadUIState()
        loadStateWithDatabaseInfos()
    }

    private fun initialLoadUIState() {
        _uiState.value = _uiState.value.copy(
            messageDialogState = initializeMessageDialogState()
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

    private fun loadStateWithDatabaseInfos() {
        loadItems()
        loadWorkout()
    }

    private fun loadWorkout() {
        launch {
            val toWorkout = workoutRepository.getCurrentMemberWorkout()

            _uiState.value = _uiState.value.copy(
                toWorkout = toWorkout,
                subtitle = getSubtitle(toWorkout)
            )
        }
    }

    private fun loadItems() {
        launch {
            val list = workoutRepository.getCurrentMemberWorkoutList()
            _uiState.value = _uiState.value.copy(items = list)
        }
    }

    private fun getSubtitle(toWorkout: TOWorkout?): String? {
        if (toWorkout == null) {
            return null
        }

        val start = toWorkout.dateStart?.format(EnumDateTimePatterns.DATE)
        val end = toWorkout.dateEnd?.format(EnumDateTimePatterns.DATE)

        return context.getString(R.string.current_workout_subtitle, start, end)
    }

    override fun getGlobalEventsBus(): GlobalEvents = globalEvents

    override fun onShowErrorDialog(message: String) {
        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(message = message)
    }

    override fun getErrorMessageFrom(throwable: Throwable): String {
        return context.getString(br.com.fitnesspro.common.R.string.unknown_error_message)
    }
}