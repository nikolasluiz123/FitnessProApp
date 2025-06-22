package br.com.fitnesspro.common.ui.state

import br.com.fitnesspro.compose.components.filter.SimpleFilterState
import br.com.fitnesspro.core.state.ILoadingUIState
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.model.enums.EnumReportContext
import br.com.fitnesspro.to.TOReport

data class GeneratedReportsUIState(
    val title: String = "",
    val subtitle: String = "",
    val simpleFilterState: SimpleFilterState = SimpleFilterState(),
    val messageDialogState: MessageDialogState = MessageDialogState(),
    val reportContext: EnumReportContext? = null,
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = {},
    val reports: List<TOReport> = emptyList()
): ILoadingUIState