package br.com.fitnesspro.common.ui.bottomsheet.registeruser

import br.com.fitnesspro.compose.components.bottomsheet.interfaces.IEnumOptionsBottomSheet

/**
 * Enum com as opções de seleção no [BottomSheetRegisterUser]
 */
enum class EnumOptionsBottomSheetRegisterUser(override val index: Int): IEnumOptionsBottomSheet {
    ACADEMY_MEMBER(0), PERSONAL_TRAINER(1), NUTRITIONIST(2);
}