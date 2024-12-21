package br.com.fitnesspro.ui.bottomsheet.nutrition

import br.com.fitnesspro.compose.components.bottomsheet.interfaces.IEnumOptionsBottomSheet

enum class EnumOptionsBottomSheetNutrition(override val index: Int): IEnumOptionsBottomSheet {
    MY_DIET(0),
    MY_PHYSIC_EVALUATION(1),
    SHOPPING_LIST(2),

    DIET_SETUP(0),
    EXECUTE_PHYSICAL_EVALUATION(1),
    MY_PREDEFINITIONS(2)
}