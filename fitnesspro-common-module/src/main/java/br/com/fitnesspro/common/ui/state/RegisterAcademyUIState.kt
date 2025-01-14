package br.com.fitnesspro.common.ui.state

import br.com.fitnesspro.compose.components.fields.state.DropDownTextField
import br.com.fitnesspro.compose.components.fields.state.PagedDialogListTextField
import br.com.fitnesspro.compose.components.fields.state.TimePickerTextField
import br.com.fitnesspro.core.callback.IShowDialogCallback
import br.com.fitnesspro.core.enums.EnumDialogType
import br.com.fitnesspro.core.state.IDialogUIState
import br.com.fitnesspro.to.TOPersonAcademyTime
import br.com.fitnesspro.tuple.AcademyTuple
import java.time.DayOfWeek

data class RegisterAcademyUIState(
    var title: String = "",
    var subtitle: String? = null,
    val academy: PagedDialogListTextField<AcademyTuple> = PagedDialogListTextField(),
    val dayWeek: DropDownTextField<DayOfWeek> = DropDownTextField(),
    val start: TimePickerTextField = TimePickerTextField(),
    val end: TimePickerTextField = TimePickerTextField(),
    var toPersonAcademyTime: TOPersonAcademyTime = TOPersonAcademyTime(),
    override val dialogMessage: String = "",
    override val showDialog: Boolean = false,
    override val dialogType: EnumDialogType = EnumDialogType.ERROR,
    override val onHideDialog: () -> Unit = { },
    override val onShowDialog: IShowDialogCallback? = null,
    override val onConfirm: () -> Unit = { },
    override val onCancel: () -> Unit = { },
): IDialogUIState