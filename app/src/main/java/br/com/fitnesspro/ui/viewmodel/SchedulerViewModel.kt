package br.com.fitnesspro.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fitnesspro.R
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.repository.SchedulerRepository
import br.com.fitnesspro.repository.UserRepository
import br.com.fitnesspro.ui.screen.scheduler.decorator.SchedulerDecorator
import br.com.fitnesspro.ui.state.SchedulerUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SchedulerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val schedulerRepository: SchedulerRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<SchedulerUIState> = MutableStateFlow(SchedulerUIState())
    val uiState get() = _uiState.asStateFlow()

    init {
        initialLoadUIState()
        loadUIStateWithDatabaseInfos()
        updateSchedules()
    }

    private fun initialLoadUIState() {
        _uiState.update {
            it.copy(
                title = context.getString(R.string.schedule_screen_title),
                onSelectYearMonth = { newYearMonth ->
                    _uiState.value = _uiState.value.copy(selectedYearMonth = newYearMonth)
                    updateSchedules()
                }
            )
        }
    }

    private fun loadUIStateWithDatabaseInfos() {
        viewModelScope.launch {
            val toPerson = userRepository.getAuthenticatedTOPerson()!!
            val userType = toPerson.toUser?.type!!

            _uiState.update {
                it.copy(
                    userType = userType,
                    toSchedulerConfig = schedulerRepository.getTOSchedulerConfigByPersonId(toPerson.id!!),
                    isVisibleFabRecurrentScheduler = userType == EnumUserType.PERSONAL_TRAINER
                )
            }
        }
    }

    fun updateSchedules() {
        viewModelScope.launch {
            val groupedTOSchedulers = schedulerRepository.getSchedulerList(
                yearMonth = _uiState.value.selectedYearMonth
            ).groupBy { it.scheduledDate!! }

            val decorators = groupedTOSchedulers.map { (date, schedules) ->
                SchedulerDecorator(
                    date = date,
                    count = schedules.size
                )
            }

            _uiState.value = _uiState.value.copy(schedules = decorators)
        }
    }
}