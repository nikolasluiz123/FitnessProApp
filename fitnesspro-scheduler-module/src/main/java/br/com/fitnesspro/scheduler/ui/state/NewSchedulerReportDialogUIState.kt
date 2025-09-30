package br.com.fitnesspro.scheduler.ui.state

import br.com.android.ui.compose.components.fields.text.date.state.DatePickerTextField
import br.com.android.ui.compose.components.fields.text.state.TextField
import br.com.android.ui.compose.components.states.ILoadingUIState
import java.time.LocalDate

data class NewSchedulerReportDialogUIState(
    val name: TextField = TextField(),
    val dateStart: DatePickerTextField = DatePickerTextField(),
    val dateEnd: DatePickerTextField = DatePickerTextField(),
    val result: NewSchedulerReportResult = NewSchedulerReportResult(),
    val onDismissRequest: () -> Unit = { },
    val onShow: () -> Unit = { },
    val showDialog: Boolean = false,
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = {}
): ILoadingUIState

data class NewSchedulerReportResult(
    var reportName: String? = null,
    val dateStart: LocalDate? = null,
    val dateEnd: LocalDate? = null
)