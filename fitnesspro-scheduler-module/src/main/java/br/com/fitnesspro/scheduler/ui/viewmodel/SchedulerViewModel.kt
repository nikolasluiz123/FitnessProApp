package br.com.fitnesspro.scheduler.ui.viewmodel

import android.content.Context
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.SchedulerConfigRepository
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.FitnessProViewModel
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.pdf.generator.enums.EnumPageSize
import br.com.fitnesspro.pdf.generator.report.PDFReportGenerator
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.scheduler.reports.SchedulerFilter
import br.com.fitnesspro.scheduler.reports.SchedulerPDFReport
import br.com.fitnesspro.scheduler.repository.SchedulerRepository
import br.com.fitnesspro.scheduler.ui.screen.scheduler.decorator.SchedulerDecorator
import br.com.fitnesspro.scheduler.ui.state.SchedulerUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SchedulerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val schedulerRepository: SchedulerRepository,
    private val schedulerConfigRepository: SchedulerConfigRepository,
    private val personRepository: PersonRepository,
    private val globalEvents: GlobalEvents
) : FitnessProViewModel() {

    private val _uiState: MutableStateFlow<SchedulerUIState> = MutableStateFlow(SchedulerUIState())
    val uiState get() = _uiState.asStateFlow()

    init {
        initialLoadUIState()
        loadUIStateWithDatabaseInfos()
        updateSchedules()
    }

    override fun getGlobalEventsBus(): GlobalEvents = globalEvents

    override fun onShowErrorDialog(message: String) {
        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(message = message)
    }

    override fun getErrorMessageFrom(throwable: Throwable): String {
        return context.getString(br.com.fitnesspro.common.R.string.unknown_error_message)
    }

    private fun initialLoadUIState() {
        _uiState.update {
            it.copy(
                title = context.getString(R.string.schedule_screen_title),
                onSelectYearMonth = { newYearMonth ->
                    _uiState.value = _uiState.value.copy(selectedYearMonth = newYearMonth)
                    updateSchedules()
                },
                messageDialogState = initializeMessageDialogState(),
            )
        }
    }

    private fun loadUIStateWithDatabaseInfos() {
        launch {
            val toPerson = personRepository.getAuthenticatedTOPerson()!!
            val userType = toPerson.user?.type!!

            _uiState.update {
                it.copy(
                    userType = userType,
                    toSchedulerConfig = schedulerConfigRepository.getTOSchedulerConfigByPersonId(toPerson.id!!),
                    isVisibleFabRecurrentScheduler = userType == EnumUserType.PERSONAL_TRAINER
                )
            }
        }
    }

    fun updateSchedules() {
        launch {
            val groupedTOSchedulers = schedulerRepository.getSchedulerList(
                yearMonth = _uiState.value.selectedYearMonth,
                canceledSchedules = false
            ).groupBy { it.dateTimeStart!!.toLocalDate() }

            val decorators = groupedTOSchedulers.map { (date, schedules) ->
                SchedulerDecorator(
                    date = date,
                    count = schedules.size
                )
            }

            _uiState.value = _uiState.value.copy(schedules = decorators)
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

    fun generateFakeReport(onSuccess: (filePath: String) -> Unit) {
        launch {
            val report = SchedulerPDFReport(context, SchedulerFilter(""))

            val generator = PDFReportGenerator(context, report)
            val file = generator.generatePdfFile(pageSize = EnumPageSize.A4)

            onSuccess(file.absolutePath)
        }
    }
}