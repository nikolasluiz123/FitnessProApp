package br.com.fitnesspro.scheduler.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.FitnessProViewModel
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.dateNow
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.repository.SchedulerRepository
import br.com.fitnesspro.scheduler.ui.navigation.SchedulerDetailsScreenArgs
import br.com.fitnesspro.scheduler.ui.navigation.schedulerDetailsArguments
import br.com.fitnesspro.scheduler.ui.state.SchedulerDetailsUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class SchedulerDetailsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val schedulerRepository: SchedulerRepository,
    private val personRepository: PersonRepository,
    private val globalEvents: GlobalEvents,
    savedStateHandle: SavedStateHandle
) : FitnessProViewModel() {

    private val _uiState: MutableStateFlow<SchedulerDetailsUIState> = MutableStateFlow(SchedulerDetailsUIState())
    val uiState get() = _uiState.asStateFlow()

    private val jsonArgs: String? = savedStateHandle[schedulerDetailsArguments]

    init {
        initialLoadUIState()
        loadUIStateWithDatabaseInfos()
    }

    override fun getGlobalEventsBus(): GlobalEvents = globalEvents

    override fun onShowErrorDialog(message: String) {
        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(message = message)
    }

    override fun getErrorMessageFrom(throwable: Throwable): String {
        return context.getString(br.com.fitnesspro.common.R.string.unknown_error_message)
    }

    fun updateSchedules() {
        val args = jsonArgs?.fromJsonNavParamToArgs(SchedulerDetailsScreenArgs::class.java)!!

        launch {
            _uiState.update {
                it.copy(
                    schedules = schedulerRepository.getSchedulerList(
                        scheduledDate = args.scheduledDate
                    )
                )
            }
        }
    }

    private fun loadUIStateWithDatabaseInfos() {
        launch {
            val toPerson = personRepository.getAuthenticatedTOPerson()!!

            _uiState.update {
                it.copy(
                    userType = toPerson.user?.type,
                )
            }
        }
    }

    private fun initialLoadUIState() {
        val args = jsonArgs?.fromJsonNavParamToArgs(SchedulerDetailsScreenArgs::class.java)!!

        _uiState.update {
            it.copy(
                title = context.getString(R.string.scheduler_details_screen_title),
                subtitle = args.scheduledDate.format(EnumDateTimePatterns.DATE),
                isVisibleFabAdd = args.scheduledDate >= dateNow(ZoneId.systemDefault()),
                messageDialogState = initializeMessageDialogState(),
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
}