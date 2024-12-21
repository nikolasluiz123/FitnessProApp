package br.com.fitnesspro.ui.bottomsheet.workout

import br.com.fitnesspro.compose.components.bottomsheet.interfaces.IBottomSheetItem

class BottomSheetWorkoutItem(
    override val option: EnumOptionsBottomSheetWorkout,
    override val iconResId: Int,
    override val labelResId: Int,
    override val iconDescriptionResId: Int
): IBottomSheetItem<EnumOptionsBottomSheetWorkout>