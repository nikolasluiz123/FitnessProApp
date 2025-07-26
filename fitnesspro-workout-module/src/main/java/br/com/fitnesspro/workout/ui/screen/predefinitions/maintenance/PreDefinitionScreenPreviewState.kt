package br.com.fitnesspro.workout.ui.screen.predefinitions.maintenance

import br.com.fitnesspro.to.TOExercisePreDefinition
import br.com.fitnesspro.to.TOWorkoutGroupPreDefinition
import br.com.fitnesspro.workout.ui.state.PreDefinitionUIState

internal val preDefinitionDefaultState = PreDefinitionUIState(
    title = "Nova Predefinição",
)

internal val preDefinitionGroupedState = PreDefinitionUIState(
    title = "Nova Predefinição",
    showGroupField = true
)

internal val pagedDialogExercisePreDefinitionItem = TOExercisePreDefinition(
    name = "Supino Inclinado com Halteres"
)

internal val pagedDialogWorkoutGroupPreDefinitionItem = TOWorkoutGroupPreDefinition(
    name = "Supino Inclinado com Halteres"
)