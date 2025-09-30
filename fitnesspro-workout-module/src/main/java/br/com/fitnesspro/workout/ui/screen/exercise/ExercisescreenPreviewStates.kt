package br.com.fitnesspro.workout.ui.screen.exercise

import br.com.android.ui.compose.components.fields.dropdown.state.DropDownTextField
import br.com.android.ui.compose.components.fields.text.dialog.paged.state.PagedDialogListTextField
import br.com.android.ui.compose.components.fields.text.dialog.state.DialogListTextField
import br.com.android.ui.compose.components.fields.text.state.TextField
import br.com.android.ui.compose.components.tabs.state.Tab
import br.com.android.ui.compose.components.tabs.state.TabState
import br.com.fitnesspro.compose.components.gallery.videoGalleryCollapsedManyValuesState
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.to.TOWorkoutGroup
import br.com.fitnesspro.workout.ui.screen.exercise.enums.EnumTabsExerciseScreen
import br.com.fitnesspro.workout.ui.state.ExerciseUIState

internal val exerciseNewUIState = ExerciseUIState(
    title = "Novo Exercício",
    subtitle = "Treino do Nikolas Luiz Schmitt",
    tabState = TabState(
        tabs = mutableListOf(
            Tab(
                enum = EnumTabsExerciseScreen.GENERAL,
                selected = true,
                enabled = true
            ),
            Tab(
                enum = EnumTabsExerciseScreen.VIDEOS,
                selected = false,
                enabled = false
            )
        )
    )
)

internal val exerciseTabGeneralUIState = ExerciseUIState(
    title = "Supino Inclinado com Halteres",
    subtitle = "Treino do Nikolas Luiz Schmitt",
    group = DialogListTextField(value = "Peito"),
    groupOrder = TextField(value = "1"),
    exercise = PagedDialogListTextField(value = "Supino Inclinado com Halteres"),
    exerciseOrder = TextField(value = "1"),
    sets = TextField(value = "3"),
    reps = TextField(value = "12"),
    rest = TextField(value = "30"),
    unitRest = DropDownTextField(value = "Segundos"),
    tabState = TabState(
        tabs = mutableListOf(
            Tab(
                enum = EnumTabsExerciseScreen.GENERAL,
                selected = true,
                enabled = true
            ),
            Tab(
                enum = EnumTabsExerciseScreen.VIDEOS,
                selected = false,
                enabled = true
            )
        )
    )
)

internal val exerciseTabVideosUIState = exerciseTabGeneralUIState.copy(
    tabState = TabState(
        tabs = mutableListOf(
            Tab(
                enum = EnumTabsExerciseScreen.GENERAL,
                selected = false,
                enabled = true
            ),
            Tab(
                enum = EnumTabsExerciseScreen.VIDEOS,
                selected = true,
                enabled = true
            )
        )
    ),
    videoGalleryState = videoGalleryCollapsedManyValuesState
)

internal val pagedDialogExerciseItem = TOExercise(
    name = "Supino Inclinado com Halteres"
)

internal val dialogGroupItem = TOWorkoutGroup(
    name = "Peito"
)