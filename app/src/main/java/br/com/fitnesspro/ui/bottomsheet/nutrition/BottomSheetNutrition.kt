package br.com.fitnesspro.ui.bottomsheet.nutrition

import androidx.compose.runtime.Composable
import br.com.fitnesspro.R
import br.com.fitnesspro.compose.components.bottomsheet.BottomSheet
import br.com.fitnesspro.core.R.drawable
import br.com.fitnesspro.model.enums.EnumUserType

@Composable
fun BottomSheetNutrition(
    userType: EnumUserType,
    onDismissRequest: () -> Unit,
    onItemClickListener: OnBottomSheetNutritionItemClick
) {
    val items = when (userType) {
        EnumUserType.PERSONAL_TRAINER -> {
            emptyList()
        }
        EnumUserType.ACADEMY_MEMBER -> {
            listOf(
                BottomSheetNutritionItem(
                    option = EnumOptionsBottomSheetNutrition.MY_DIET,
                    iconResId = drawable.ic_flatware_32dp,
                    labelResId = R.string.label_my_diet_bottom_sheet_nutrition,
                    iconDescriptionResId = R.string.label_my_diet_bottom_sheet_nutrition
                ),
                BottomSheetNutritionItem(
                    option = EnumOptionsBottomSheetNutrition.MY_PHYSIC_EVALUATION,
                    iconResId = drawable.ic_evaluations_24dp,
                    labelResId = R.string.label_my_physic_evaluation_bottom_sheet_nutrition,
                    iconDescriptionResId = R.string.label_my_physic_evaluation_bottom_sheet_nutrition
                ),
                BottomSheetNutritionItem(
                    option = EnumOptionsBottomSheetNutrition.SHOPPING_LIST,
                    iconResId = drawable.ic_shopping_list_24dp,
                    labelResId = R.string.label_shopping_list_bottom_sheet_nutrition,
                    iconDescriptionResId = R.string.label_shopping_list_bottom_sheet_nutrition
                )
            )
        }

        EnumUserType.NUTRITIONIST -> {
            listOf(
                BottomSheetNutritionItem(
                    option = EnumOptionsBottomSheetNutrition.DIET_SETUP,
                    iconResId = drawable.ic_flatware_32dp,
                    labelResId = R.string.label_diet_setup_bottom_sheet_nutrition,
                    iconDescriptionResId = R.string.label_diet_setup_bottom_sheet_nutrition
                ),
                BottomSheetNutritionItem(
                    option = EnumOptionsBottomSheetNutrition.EXECUTE_PHYSICAL_EVALUATION,
                    iconResId = drawable.ic_evaluations_24dp,
                    labelResId = R.string.label_physic_evaluation_bottom_sheet_nutrition,
                    iconDescriptionResId = R.string.label_physic_evaluation_bottom_sheet_nutrition
                ),
                BottomSheetNutritionItem(
                    option = EnumOptionsBottomSheetNutrition.MY_PREDEFINITIONS,
                    iconResId = drawable.ic_predefinitions_24dp,
                    labelResId = R.string.label_predefinitions_bottom_sheet_nutrition,
                    iconDescriptionResId = R.string.label_predefinitions_bottom_sheet_nutrition
                )
            )
        }
    }

    BottomSheet(
        items = items,
        onDismissRequest = onDismissRequest,
        onItemClickListener = {
            onItemClickListener.onExecute(it)
            onDismissRequest()
        }
    )
}