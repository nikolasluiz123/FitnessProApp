package br.com.fitnesspro.workout.ui.screen.predefinitions.search.bottomsheet

import br.com.android.ui.compose.components.bottomsheet.interfaces.IEnumOptionsBottomSheet

enum class EnumOptionsBottomSheetNewPredefinition(override val index: Int):
    IEnumOptionsBottomSheet {
    EXERCISE(0), GROUP(1);
}