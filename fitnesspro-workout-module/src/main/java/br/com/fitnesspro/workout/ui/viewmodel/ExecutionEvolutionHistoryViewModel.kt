package br.com.fitnesspro.workout.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.base.FitnessProStatefulViewModel
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.workout.repository.ExerciseExecutionRepository
import br.com.fitnesspro.workout.ui.navigation.ExecutionEvolutionHistoryScreenArgs
import br.com.fitnesspro.workout.ui.navigation.executionEvolutionHistoryScreenRouteArguments
import br.com.fitnesspro.workout.ui.state.ExecutionEvolutionHistoryUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ExecutionEvolutionHistoryViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val globalEvents: GlobalEvents,
    private val exerciseExecutionRepository: ExerciseExecutionRepository,
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
                    loadHistory(it)
                }
            ),
            messageDialogState = createMessageDialogState(
                getCurrentState = { _uiState.value.messageDialogState },
                updateState = { newState -> _uiState.value = _uiState.value.copy(messageDialogState = newState) }
            ),
        )
    }

    fun loadHistory(simpleFilter: String = _uiState.value.simpleFilterState.quickFilter) {
        launch {
            val args = jsonArgs?.fromJsonNavParamToArgs(ExecutionEvolutionHistoryScreenArgs::class.java)!!

            _uiState.value = _uiState.value.copy(
                history = exerciseExecutionRepository.getListExecutionHistoryGrouped(
                    simpleFilter = simpleFilter,
                    personMemberId = args.personMemberId
                ).flow,
                executeLoad = false
            )
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