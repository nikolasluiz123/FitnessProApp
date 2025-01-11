package br.com.fitnesspro.ui.state

import br.com.fitnesspro.compose.components.fields.menu.MenuItem
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.core.callback.IShowDialogCallback
import br.com.fitnesspro.core.enums.EnumDialogType
import br.com.fitnesspro.core.state.IDialogUIState
import br.com.fitnesspro.to.TOPersonAcademyTime
import java.time.DayOfWeek

data class RegisterAcademyUIState(
    var title: String = "",
    var subtitle: String? = null,
    val academy: TextField = TextField(),
    val dayWeek: TextField = TextField(),
    val start: TextField = TextField(),
    val end: TextField = TextField(),
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