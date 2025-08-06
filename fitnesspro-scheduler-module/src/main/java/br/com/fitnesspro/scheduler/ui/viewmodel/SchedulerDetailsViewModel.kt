package br.com.fitnesspro.scheduler.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.base.FitnessProStatefulViewModel
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.dateNow
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.repository.SchedulerRepository
import br.com.fitnesspro.scheduler.ui.navigation.SchedulerDetailsScreenArgs
import br.com.fitnesspro.scheduler.ui.navigation.schedulerDetailsArguments
import br.com.fitnesspro.scheduler.ui.state.SchedulerDetailsUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class SchedulerDetailsViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val schedulerRepository: SchedulerRepository,
    private val personRepository: PersonRepository,
    private val globalEvents: GlobalEvents,
    savedStateHandle: SavedStateHandle
) : FitnessProStatefulViewModel() {

    private val _uiState: MutableStateFlow<SchedulerDetailsUIState> = MutableStateFlow(SchedulerDetailsUIState())
    val uiState get() = _uiState.asStateFlow()

    private val jsonArgs: String? = savedStateHandle[schedulerDetailsArguments]

    init {
        initialLoadUIState()
    }

    override fun initialLoadUIState() {
        val args = jsonArgs?.fromJsonNavParamToArgs(SchedulerDetailsScreenArgs::class.java)!!

        _uiState.value = _uiState.value.copy(
            title = context.getString(R.string.scheduler_details_screen_title),
            subtitle = args.scheduledDate.format(EnumDateTimePatterns.DATE),
            isVisibleFabAdd = args.scheduledDate >= dateNow(ZoneId.systemDefault()),
            messageDialogState = createMessageDialogState(
                getCurrentState = { _uiState.value.messageDialogState },
                updateState = { _uiState.value = _uiState.value.copy(messageDialogState = it) }
            )
        )
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
            _uiState.value = _uiState.value.copy(
                schedules = schedulerRepository.getSchedulerList(
                    scheduledDate = args.scheduledDate
                )
            )
        }
    }

    fun loadUIStateWithDatabaseInfos() {
        launch {
            val toPerson = personRepository.getAuthenticatedTOPerson()!!
            _uiState.value = _uiState.value.copy(userType = toPerson.user?.type, executeLoad = false)
        }
    }
}