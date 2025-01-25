package br.com.fitnesspro.ui.bottomsheet.workout

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import br.com.fitnesspro.R
import br.com.fitnesspro.common.ui.enums.EnumBottomSheetsTestTags.BOTTOM_SHEET_WORKOUT
import br.com.fitnesspro.compose.components.bottomsheet.BottomSheet
import br.com.fitnesspro.core.R.drawable
import br.com.fitnesspro.model.enums.EnumUserType

@Composable
fun BottomSheetWorkout(
    userType: EnumUserType,
    onDismissRequest: () -> Unit,
    onItemClickListener: OnBottomSheetWorkoutItemClick
) {
    val items = when (userType) {
        EnumUserType.PERSONAL_TRAINER -> {
            listOf(
                BottomSheetWorkoutItem(
                    option = EnumOptionsBottomSheetWorkout.FOLLOW_UP_EVOLUTION,
                    iconResId = drawable.ic_evolution_24dp,
                    labelResId = R.string.label_follow_up_evolution_bottom_sheet_workout,
                    iconDescriptionResId = R.string.label_follow_up_evolution_bottom_sheet_workout
                ),
                BottomSheetWorkoutItem(
                    option = EnumOptionsBottomSheetWorkout.WORKOUT_SETUP,
                    iconResId = drawable.ic_workout_24dp,
                    labelResId = R.string.label_workout_setup_bottom_sheet_workout,
                    iconDescriptionResId = R.string.label_workout_setup_bottom_sheet_workout
                ),
                BottomSheetWorkoutItem(
                    option = EnumOptionsBottomSheetWorkout.MY_PREDEFINITIONS,
                    iconResId = drawable.ic_predefinitions_24dp,
                    labelResId = R.string.label_predefinitions_bottom_sheet_workout,
                    iconDescriptionResId = R.string.label_predefinitions_bottom_sheet_workout
                )
            )
        }
        EnumUserType.ACADEMY_MEMBER -> {
            listOf(
                BottomSheetWorkoutItem(
                    option = EnumOptionsBottomSheetWorkout.MY_EVOLUTION,
                    iconResId = drawable.ic_evolution_24dp,
                    labelResId = R.string.label_evolution_bottom_sheet_workout,
                    iconDescriptionResId = R.string.label_evolution_bottom_sheet_workout
                ),
                BottomSheetWorkoutItem(
                    option = EnumOptionsBottomSheetWorkout.MY_WORKOUT,
                    iconResId = drawable.ic_workout_24dp,
                    labelResId = R.string.label_workout_bottom_sheet_workout,
                    iconDescriptionResId = R.string.label_workout_bottom_sheet_workout
                )
            )
        }

        EnumUserType.NUTRITIONIST -> emptyList()
    }

    BottomSheet(
        modifier = Modifier.testTag(BOTTOM_SHEET_WORKOUT.name),
        items = items,
        onDismissRequest = onDismissRequest,
        onItemClickListener = {
            onItemClickListener.onExecute(it)
            onDismissRequest()
        }
    )
}