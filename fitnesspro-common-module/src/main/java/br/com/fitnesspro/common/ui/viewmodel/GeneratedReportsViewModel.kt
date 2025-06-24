package br.com.fitnesspro.common.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.ReportRepository
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.navigation.GeneratedReportsScreenArgs
import br.com.fitnesspro.common.ui.navigation.generatedReportsArguments
import br.com.fitnesspro.common.ui.state.GeneratedReportsUIState
import br.com.fitnesspro.compose.components.filter.SimpleFilterState
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.core.extensions.toReadableFileSize
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.model.enums.EnumReportContext
import br.com.fitnesspro.to.TOReport
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class GeneratedReportsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val globalEvents: GlobalEvents,
    private val reportRepository: ReportRepository,
    savedStateHandle: SavedStateHandle
): FitnessProViewModel() {

    private val _uiState: MutableStateFlow<GeneratedReportsUIState> = MutableStateFlow(GeneratedReportsUIState())
    val uiState get() = _uiState.asStateFlow()

    private val jsonArgs: String? = savedStateHandle[generatedReportsArguments]

    init {
        initialLoadUIState()
    }

    private fun initialLoadUIState() {
        val args = jsonArgs?.fromJsonNavParamToArgs(GeneratedReportsScreenArgs::class.java)!!

        _uiState.value = _uiState.value.copy(
            title = context.getString(R.string.generated_reports_title),
            subtitle = getSubtitle(args),
            reportContext = args.reportContext,
            messageDialogState = initializeMessageDialogState(),
            simpleFilterState = initializeSimpleFilterState(),
            onToggleLoading = {
                _uiState.value = _uiState.value.copy(
                    showLoading = _uiState.value.showLoading.not()
                )
            }
        )
    }

    private fun getSubtitle(args: GeneratedReportsScreenArgs): String {
        return when (args.reportContext) {
            EnumReportContext.SCHEDULERS_REPORT -> {
                context.getString(R.string.generated_reports_subtitle_schedulers_report)
            }
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

    private fun initializeSimpleFilterState(): SimpleFilterState {
        return SimpleFilterState(
            onSimpleFilterChange = { filterText ->
                _uiState.value = _uiState.value.copy(
                    simpleFilterState = _uiState.value.simpleFilterState.copy(
                        quickFilter = filterText
                    )
                )

                onUpdateReports(filterText)
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

    override fun getGlobalEventsBus(): GlobalEvents = globalEvents

    override fun getErrorMessageFrom(throwable: Throwable): String {
        return context.getString(R.string.unknown_error_message)
    }

    override fun onShowErrorDialog(message: String) {
        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(message = message)
    }

    override fun onError(throwable: Throwable) {
        super.onError(throwable)

        if (_uiState.value.showLoading) {
            _uiState.value.onToggleLoading()
        }
    }

    fun onUpdateReports(filterText: String? = null) {
        launch {
            val args = jsonArgs?.fromJsonNavParamToArgs(GeneratedReportsScreenArgs::class.java)!!
            val reports = reportRepository.getListReports(args.reportContext, filterText)

            _uiState.value = _uiState.value.copy(
                reports = reports,
                storageSize = reports.sumOf { it.kbSize!! }.toReadableFileSize(context)
            )
        }
    }

    fun onDeleteAllReportsClick(onSuccess: () -> Unit) {
        launch {

        }
    }

    fun onDeleteReportClick(report: TOReport, onSuccess: () -> Unit) {
        launch {

        }
    }
}