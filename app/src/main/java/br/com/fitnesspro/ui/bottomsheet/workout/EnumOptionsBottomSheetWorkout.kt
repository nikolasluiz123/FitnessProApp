package br.com.fitnesspro.ui.bottomsheet.workout

import br.com.fitnesspro.compose.components.bottomsheet.interfaces.IEnumOptionsBottomSheet

enum class EnumOptionsBottomSheetWorkout(override val index: Int): IEnumOptionsBottomSheet {
    MY_EVOLUTION(0),
    MY_WORKOUT(1),

    FOLLOW_UP_EVOLUTION(0),
    WORKOUT_SETUP(1),
    MY_PREDEFINITIONS(2)
}