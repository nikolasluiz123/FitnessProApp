package br.com.fitnesspro.common.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import br.com.android.ui.compose.components.dialog.message.showConfirmationDialog
import br.com.android.ui.compose.components.dialog.message.showErrorDialog
import br.com.core.android.utils.extensions.toReadableFileSize
import br.com.core.utils.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.ReportRepository
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.navigation.GeneratedReportsScreenArgs
import br.com.fitnesspro.common.ui.navigation.generatedReportsArguments
import br.com.fitnesspro.common.ui.state.GeneratedReportsUIState
import br.com.fitnesspro.common.ui.viewmodel.base.FitnessProStatefulViewModel
import br.com.fitnesspro.common.usecase.report.InactivateAllReportsUseCase
import br.com.fitnesspro.common.usecase.report.InactivateReportUseCase
import br.com.fitnesspro.model.enums.EnumReportContext
import br.com.fitnesspro.to.TOReport
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.FileNotFoundException
import javax.inject.Inject

@HiltViewModel
class GeneratedReportsViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val globalEvents: GlobalEvents,
    private val reportRepository: ReportRepository,
    private val inactivateReportUseCase: InactivateReportUseCase,
    private val inactivateAllReportsUseCase: InactivateAllReportsUseCase,
    savedStateHandle: SavedStateHandle
): FitnessProStatefulViewModel() {

    private val _uiState: MutableStateFlow<GeneratedReportsUIState> = MutableStateFlow(GeneratedReportsUIState())
    val uiState get() = _uiState.asStateFlow()

    private val jsonArgs: String? = savedStateHandle[generatedReportsArguments]

    init {
        initialLoadUIState()
    }

    override fun initialLoadUIState() {
        val args = jsonArgs?.fromJsonNavParamToArgs(GeneratedReportsScreenArgs::class.java)!!

        _uiState.value = _uiState.value.copy(
            title = context.getString(R.string.generated_reports_title),
            subtitle = getSubtitle(args),
            reportContext = args.reportContext,
            messageDialogState = createMessageDialogState(
                getCurrentState = { _uiState.value.messageDialogState },
                updateState = { _uiState.value = _uiState.value.copy(messageDialogState = it) }
            ),
            simpleFilterState = createSimpleFilterState(
                getCurrentState = { _uiState.value.simpleFilterState },
                updateState = { _uiState.value = _uiState.value.copy(simpleFilterState = it) },
                onSimpleFilterChange = ::onUpdateReports
            ),
            onToggleLoading = {
                _uiState.value = _uiState.value.copy(showLoading = _uiState.value.showLoading.not())
            }
        )
    }

    override fun getGlobalEventsBus(): GlobalEvents = globalEvents

    override fun getErrorMessageFrom(throwable: Throwable): String {
        return when (throwable) {
            is FileNotFoundException -> throwable.message!!
            else -> context.getString(R.string.unknown_error_message)
        }
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

    private fun getSubtitle(args: GeneratedReportsScreenArgs): String {
        return when (args.reportContext) {
            EnumReportContext.SCHEDULERS_REPORT -> {
                context.getString(R.string.generated_reports_subtitle_schedulers_report)
            }

            EnumReportContext.WORKOUT_REGISTER_EVOLUTION -> {
                context.getString(R.string.generated_reports_subtitle_workout_register_evolution)
            }
        }
    }

    fun onUpdateReports(filterText: String? = null) {
        launch {
            updateReports(filterText)
        }
    }

    private suspend fun updateReports(filterText: String? = null) {
        val args = jsonArgs?.fromJsonNavParamToArgs(GeneratedReportsScreenArgs::class.java)!!
        val reports = reportRepository.getListReports(args.reportContext, filterText)

        _uiState.value = _uiState.value.copy(
            reports = reports,
            storageSize = reports.sumOf { it.kbSize!! }.toReadableFileSize(context)
        )
    }

    fun onInactivateAllReportsClick(onSuccess: () -> Unit) {
        _uiState.value.messageDialogState.onShowDialog?.showConfirmationDialog(
            message = context.getString(R.string.generated_reports_delete_all_reports_confirmation_message),
            onConfirm = {
                _uiState.value.onToggleLoading()

                launch {
                    inactivateAllReportsUseCase(_uiState.value.reportContext!!)
                    updateReports()
                    onSuccess()
                }
            }
        )
    }

    fun onInactivateReportClick(report: TOReport, onSuccess: () -> Unit) {
        _uiState.value.messageDialogState.onShowDialog?.showConfirmationDialog(
            message = context.getString(R.string.generated_reports_delete_report_confirmation_message),
            onConfirm = {
                _uiState.value.onToggleLoading()

                launch {
                    inactivateReportUseCase(_uiState.value.reportContext!!, report)
                    updateReports()
                    onSuccess()
                }
            }
        )
    }
}