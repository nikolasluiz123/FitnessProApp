package br.com.fitnesspro.workout.ui.screen.exercise

import br.com.fitnesspro.compose.components.fields.state.DialogListTextField
import br.com.fitnesspro.compose.components.fields.state.DropDownTextField
import br.com.fitnesspro.compose.components.fields.state.PagedDialogListTextField
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.to.TOWorkoutGroup
import br.com.fitnesspro.workout.ui.state.ExerciseUIState

internal val exerciseNewUIState = ExerciseUIState(
    title = "Novo Exercício",
    subtitle = "Treino do Nikolas Luiz Schmitt",
)

internal val exerciseUIState = ExerciseUIState(
    title = "Supino Inclinado com Halteres",
    subtitle = "Treino do Nikolas Luiz Schmitt",
    group = DialogListTextField(value = "Peito"),
    exercise = PagedDialogListTextField(value = "Supino Inclinado com Halteres"),
    sets = TextField(value = "3"),
    reps = TextField(value = "12"),
    rest = TextField(value = "30"),
    unitRest = DropDownTextField(value = "Segundos"),
)

internal val pagedDialogExerciseItem = TOExercise(
    name = "Supino Inclinado com Halteres"
)

internal val dialogGroupItem = TOWorkoutGroup(
    name = "Peito"
)