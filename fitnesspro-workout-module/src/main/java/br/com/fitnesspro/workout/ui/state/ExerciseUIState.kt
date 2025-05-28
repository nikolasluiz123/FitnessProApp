package br.com.fitnesspro.workout.ui.state

import br.com.fitnesspro.compose.components.fields.state.DropDownTextField
import br.com.fitnesspro.compose.components.fields.state.PagedDialogListTextField
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.core.state.ILoadingUIState
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOWorkoutGroup
import java.time.temporal.ChronoUnit

data class ExerciseUIState(
    var title: String = "",
    var subtitle: String = "",
    val group: DropDownTextField<TOWorkoutGroup> = DropDownTextField(),
    val exercise: PagedDialogListTextField<TOExercise> = PagedDialogListTextField(),
    val sets: TextField = TextField(),
    val reps: TextField = TextField(),
    val rest: TextField = TextField(),
    val unitRest: DropDownTextField<ChronoUnit?> = DropDownTextField(),
    val duration: TextField = TextField(),
    val unitDuration: DropDownTextField<ChronoUnit?> = DropDownTextField(),
    val observation: TextField = TextField(),
    val messageDialogState: MessageDialogState = MessageDialogState(),
    val toExercise: TOExercise = TOExercise(),
    val authenticatedPerson: TOPerson = TOPerson(),
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = { },
): ILoadingUIState
