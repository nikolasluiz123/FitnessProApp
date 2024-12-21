package br.com.fitnesspro.ui.bottomsheet.nutrition

import br.com.fitnesspro.compose.components.bottomsheet.interfaces.IBottomSheetItem

class BottomSheetNutritionItem(
    override val option: EnumOptionsBottomSheetNutrition,
    override val iconResId: Int,
    override val labelResId: Int,
    override val iconDescriptionResId: Int
): IBottomSheetItem<EnumOptionsBottomSheetNutrition>