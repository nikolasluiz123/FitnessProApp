package br.com.fitnesspro.ui.state

import br.com.fitnesspro.core.state.ILoadingUIState
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.to.TOPerson

data class HomeUIState(
    val title: String = "",
    val subtitle: String = "",
    val toPerson: TOPerson? = null,
    val isEnabledSchedulerButton: Boolean = false,
    val isEnabledWorkoutButton: Boolean = false,
    val isEnabledNutritionButton: Boolean = false,
    val isEnabledMoneyButton: Boolean = false,
    val messageDialogState: MessageDialogState = MessageDialogState(),
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = { },
): ILoadingUIState