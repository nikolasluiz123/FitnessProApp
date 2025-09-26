package br.com.fitnesspro.workout.ui.state.reports

import br.com.fitnesspro.compose.components.fields.state.PagedDialogListTextField
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.core.state.ILoadingUIState
import br.com.fitnesspro.tuple.WorkoutTuple

data class NewRegisterEvolutionReportDialogUIState(
    val name: TextField = TextField(),
    val workout: PagedDialogListTextField<WorkoutTuple> = PagedDialogListTextField(),
    val result: NewRegisterEvolutionReportResult = NewRegisterEvolutionReportResult(),
    val onDismissRequest: () -> Unit = { },
    val onShow: () -> Unit = { },
    val showDialog: Boolean = false,
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = {}
): ILoadingUIState

data class NewRegisterEvolutionReportResult(
    var workoutId: String? = null,
    var reportName: String? = null
)