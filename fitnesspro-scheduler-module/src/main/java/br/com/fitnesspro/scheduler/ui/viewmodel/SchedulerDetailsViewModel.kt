package br.com.fitnesspro.scheduler.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fitnesspro.common.repository.UserRepository
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SchedulerDetailsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val schedulerRepository: SchedulerRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState: MutableStateFlow<SchedulerDetailsUIState> = MutableStateFlow(SchedulerDetailsUIState())
    val uiState get() = _uiState.asStateFlow()

    private val jsonArgs: String? = savedStateHandle[schedulerDetailsArguments]

    init {
        initialLoadUIState()
        loadUIStateWithDatabaseInfos()
    }

    fun updateSchedules() {
        val args = jsonArgs?.fromJsonNavParamToArgs(SchedulerDetailsScreenArgs::class.java)!!

        viewModelScope.launch {
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
        viewModelScope.launch {
            val toPerson = userRepository.getAuthenticatedTOPerson()!!

            _uiState.update {
                it.copy(
                    userType = toPerson.toUser?.type,
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
                isVisibleFabAdd = args.scheduledDate >= dateNow()
            )
        }
    }
}