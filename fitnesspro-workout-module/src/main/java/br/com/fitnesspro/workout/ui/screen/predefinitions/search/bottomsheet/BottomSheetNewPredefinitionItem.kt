package br.com.fitnesspro.workout.ui.screen.predefinitions.search.bottomsheet

import br.com.fitnesspro.compose.components.bottomsheet.interfaces.IBottomSheetItem

class BottomSheetNewPredefinitionItem(
    override val option: EnumOptionsBottomSheetNewPredefinition,
    override val labelResId: Int,
    override val iconDescriptionResId: Int? = null,
    override val iconResId: Int? = null
): IBottomSheetItem<EnumOptionsBottomSheetNewPredefinition>