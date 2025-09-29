package br.com.fitnesspro.workout.ui.screen.exercise.enums

import br.com.android.ui.compose.components.tabs.state.interfaces.IEnumTab
import br.com.fitnesspro.workout.R

enum class EnumTabsExerciseScreen(
    override val index: Int,
    override val labelResId: Int
): IEnumTab {
    GENERAL(index = 0, labelResId = R.string.exercise_screen_label_tab_general),
    VIDEOS(index = 1, labelResId = R.string.exercise_screen_label_tab_videos)
}