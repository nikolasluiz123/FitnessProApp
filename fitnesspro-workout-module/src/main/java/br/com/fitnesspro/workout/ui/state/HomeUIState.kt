package br.com.fitnesspro.workout.ui.state

import br.com.fitnesspro.core.callback.IShowDialogCallback
import br.com.fitnesspro.core.enums.EnumDialogType
import br.com.fitnesspro.core.state.IDialogUIState
import br.com.fitnesspro.to.TOPerson

data class HomeUIState(
    val title: String = "",
    val subtitle: String = "",
    val toPerson: TOPerson? = null,
    val isEnabledSchedulerButton: Boolean = false,
    val isEnabledWorkoutButton: Boolean = false,
    val isEnabledNutritionButton: Boolean = false,
    val isEnabledMoneyButton: Boolean = false,
    override val dialogType: EnumDialogType = EnumDialogType.ERROR,
    override val dialogMessage: String = "",
    override val showDialog: Boolean = false,
    override val onShowDialog: IShowDialogCallback? = null,
    override val onHideDialog: () -> Unit = {},
    override val onConfirm: () -> Unit = {},
    override val onCancel: () -> Unit = {}
): IDialogUIState