package br.com.fitnesspro.workout.ui.screen.details.enums

import br.com.android.ui.compose.components.tabs.state.interfaces.IEnumTab
import br.com.fitnesspro.workout.R

enum class EnumTabsExerciseDetailsScreen(
    override val index: Int,
    override val labelResId: Int
): IEnumTab {
    ORIENTATION(index = 0, labelResId = R.string.exercise_details_screen_label_tab_orientation),
    EVOLUTION(index = 1, labelResId = R.string.exercise_details_screen_label_tab_evolution)
}