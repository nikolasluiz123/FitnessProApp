package br.com.fitnesspro.ui.state

import br.com.fitnesspro.compose.components.menu.MenuItem
import br.com.fitnesspro.compose.components.state.Field
import br.com.fitnesspro.core.callback.IShowDialogCallback
import br.com.fitnesspro.core.enums.EnumDialogType
import br.com.fitnesspro.core.state.IDialogUIState
import br.com.fitnesspro.ui.screen.registeruser.to.TOPersonAcademyTime
import java.time.DayOfWeek

data class RegisterAcademyUIState(
    var title: String = "",
    var subtitle: String? = null,
    val academy: Field = Field(),
    val dayWeek: Field = Field(),
    val start: Field = Field(),
    val end: Field = Field(),
    val academies: List<MenuItem<String>> = listOf(),
    val dayWeeks: List<MenuItem<DayOfWeek>> = listOf(),
    var toPersonAcademyTime: TOPersonAcademyTime? = null,
    override val dialogMessage: String = "",
    override val showDialog: Boolean = false,
    override val dialogType: EnumDialogType = EnumDialogType.ERROR,
    override val onHideDialog: () -> Unit = { },
    override val onShowDialog: IShowDialogCallback? = null,
    override val onConfirm: () -> Unit = { },
    override val onCancel: () -> Unit = { },
): IDialogUIState