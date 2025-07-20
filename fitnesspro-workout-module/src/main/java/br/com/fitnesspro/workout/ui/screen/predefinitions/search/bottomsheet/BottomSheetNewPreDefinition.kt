package br.com.fitnesspro.workout.ui.screen.predefinitions.search.bottomsheet

import androidx.compose.runtime.Composable
import br.com.fitnesspro.common.R
import br.com.fitnesspro.compose.components.bottomsheet.BottomSheet
import br.com.fitnesspro.firebase.api.analytics.logBottomSheetItemClick
import br.com.fitnesspro.workout.ui.navigation.PreDefinitionScreenArgs
import br.com.fitnesspro.workout.ui.screen.predefinitions.maintenance.callbacks.OnNavigateToPreDefinition
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

@Composable
fun BottomSheetNewPreDefinition(
    onDismissRequest: () -> Unit,
    onItemClickListener: OnNavigateToPreDefinition?
) {
    val items = listOf(
        BottomSheetNewPredefinitionItem(
            option = EnumOptionsBottomSheetNewPredefinition.EXERCISE,
            labelResId = R.string.label_exercise_bottom_sheet_new_pre_definition,
        ),
        BottomSheetNewPredefinitionItem(
            option = EnumOptionsBottomSheetNewPredefinition.GROUP,
            labelResId = R.string.label_group_bottom_sheet_new_pre_definition,
        )
    )

    BottomSheet(
        items = items,
        onDismissRequest = onDismissRequest,
        onItemClickListener = {
            Firebase.analytics.logBottomSheetItemClick(it)
            onItemClickListener?.onNavigate(
                args = PreDefinitionScreenArgs(
                    grouped = it == EnumOptionsBottomSheetNewPredefinition.GROUP
                )
            )
            onDismissRequest()
        }
    )
}