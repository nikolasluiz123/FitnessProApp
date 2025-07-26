package br.com.fitnesspro.workout.ui.viewmodel

import android.content.Context
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.FitnessProViewModel
import br.com.fitnesspro.compose.components.filter.SimpleFilterState
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.workout.repository.ExercisePreDefinitionRepository
import br.com.fitnesspro.workout.ui.state.PreDefinitionsUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PreDefinitionsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val globalEvents: GlobalEvents,
    private val exercisePreDefinitionRepository: ExercisePreDefinitionRepository,
    private val personRepository: PersonRepository
): FitnessProViewModel() {

    private val _uiState: MutableStateFlow<PreDefinitionsUIState> = MutableStateFlow(PreDefinitionsUIState())
    val uiState get() = _uiState.asStateFlow()

    init {
        initialLoadUIState()
        loadStateWithDatabaseInfos()
    }

    private fun loadStateWithDatabaseInfos() {
        launch {
            val personId = personRepository.getAuthenticatedTOPerson()?.id!!

            _uiState.value = _uiState.value.copy(
                authenticatedPersonId = personId,
                predefinitions = exercisePreDefinitionRepository.getListGroupedPredefinitions(personId).flow
            )
        }
    }

    private fun initialLoadUIState() {
        _uiState.value = _uiState.value.copy(
            simpleFilterState = initializeSimpleFilterState(),
            messageDialogState = initializeMessageDialogState(),
            onToggleBottomSheetNewPredefinition = {
                _uiState.value = _uiState.value.copy(
                    showBottomSheetNewPredefinition = !_uiState.value.showBottomSheetNewPredefinition
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

    private fun initializeSimpleFilterState(): SimpleFilterState {
        return SimpleFilterState(
            onSimpleFilterChange = { filterText ->
                _uiState.value = _uiState.value.copy(
                    simpleFilterState = _uiState.value.simpleFilterState.copy(
                        quickFilter = filterText
                    )
                )

                _uiState.value.authenticatedPersonId?.let { personId ->
                    _uiState.value = _uiState.value.copy(
                        predefinitions = exercisePreDefinitionRepository.getListGroupedPredefinitions(
                            authenticatedPersonId = personId,
                            simpleFilter = filterText
                        ).flow
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

    override fun getErrorMessageFrom(throwable: Throwable): String {
        return context.getString(br.com.fitnesspro.common.R.string.unknown_error_message)
    }

    override fun onShowErrorDialog(message: String) {
        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(message = message)
    }

    override fun getGlobalEventsBus(): GlobalEvents {
        return globalEvents
    }
}