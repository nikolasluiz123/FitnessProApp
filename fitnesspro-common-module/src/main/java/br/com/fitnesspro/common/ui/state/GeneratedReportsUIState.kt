package br.com.fitnesspro.common.ui.state

import br.com.android.ui.compose.components.dialog.message.MessageDialogState
import br.com.android.ui.compose.components.simplefilter.SimpleFilterState
import br.com.android.ui.compose.components.states.ILoadingUIState
import br.com.android.ui.compose.components.states.IThrowableUIState
import br.com.fitnesspro.model.enums.EnumReportContext
import br.com.fitnesspro.to.TOReport

data class GeneratedReportsUIState(
    val title: String = "",
    val subtitle: String = "",
    val simpleFilterState: SimpleFilterState = SimpleFilterState(),
    val reportContext: EnumReportContext? = null,
    val storageSize: String = "0",
    override val messageDialogState: MessageDialogState = MessageDialogState(),
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = {},
    val reports: List<TOReport> = emptyList()
): ILoadingUIState, IThrowableUIState