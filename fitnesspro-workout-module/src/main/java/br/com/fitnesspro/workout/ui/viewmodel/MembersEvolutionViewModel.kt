package br.com.fitnesspro.workout.ui.viewmodel

import android.content.Context
import br.com.android.ui.compose.components.dialog.message.showErrorDialog
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.base.FitnessProStatefulViewModel
import br.com.fitnesspro.workout.ui.state.MembersEvolutionUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MembersEvolutionViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val globalEvents: GlobalEvents,
    private val personRepository: PersonRepository
): FitnessProStatefulViewModel() {

    private val _uiState: MutableStateFlow<MembersEvolutionUIState> = MutableStateFlow(MembersEvolutionUIState())
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
                            persons = personRepository.getPersonMembersFromPersonalTrainer(
                                simpleFilter = it
                            )
                        )
                    }
                }
            ),
            messageDialogState = createMessageDialogState(
                getCurrentState = { _uiState.value.messageDialogState },
                updateState = { newState -> _uiState.value = _uiState.value.copy(messageDialogState = newState) }
            )
        )
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

    fun loadStateWithDatabaseInfos() {
        launch {
            _uiState.value = _uiState.value.copy(
                persons = personRepository.getPersonMembersFromPersonalTrainer(),
                executeLoad = false
            )
        }
    }

}