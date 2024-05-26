package br.com.fitnesspro.ui.bottomsheet

import br.com.fitnesspro.compose.components.bottomsheet.interfaces.IEnumOptionsBottomSheet

/**
 * Enum com as opções de seleção no [BottomSheetRegisterUser]
 */
enum class EnumOptionsBottomSheetRegisterUser(override val index: Int): IEnumOptionsBottomSheet {
    STUDENT(0), TRAINER(1), NUTRITIONIST(2);
}