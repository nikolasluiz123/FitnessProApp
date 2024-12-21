package br.com.fitnesspro.ui.bottomsheet.registeruser

import br.com.fitnesspro.compose.components.bottomsheet.interfaces.IBottomSheetItem

/**
 * Implementação do item do [BottomSheetRegisterUser]
 */
class BottomSheetRegisterUserItem(
    override val option: EnumOptionsBottomSheetRegisterUser,
    override val iconResId: Int,
    override val labelResId: Int,
    override val iconDescriptionResId: Int
): IBottomSheetItem<EnumOptionsBottomSheetRegisterUser>