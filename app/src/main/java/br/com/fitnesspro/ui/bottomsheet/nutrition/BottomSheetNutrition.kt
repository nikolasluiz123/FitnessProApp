package br.com.fitnesspro.ui.bottomsheet.nutrition

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import br.com.android.firebase.toolkit.analytics.logBottomSheetItemClick
import br.com.android.ui.compose.components.bottomsheet.BottomSheet
import br.com.fitnesspro.R
import br.com.fitnesspro.common.ui.enums.EnumBottomSheetsTestTags.BOTTOM_SHEET_NUTRITION
import br.com.fitnesspro.core.R.drawable
import br.com.fitnesspro.model.enums.EnumUserType
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics

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
        modifier = Modifier.testTag(BOTTOM_SHEET_NUTRITION.name),
        items = items,
        onDismissRequest = onDismissRequest,
        onItemClickListener = {
            Firebase.analytics.logBottomSheetItemClick(it)
            onItemClickListener.onExecute(it)
            onDismissRequest()
        }
    )
}