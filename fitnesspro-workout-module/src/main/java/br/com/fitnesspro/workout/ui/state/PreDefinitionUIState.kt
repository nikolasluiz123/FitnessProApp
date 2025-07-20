package br.com.fitnesspro.workout.ui.state

import br.com.fitnesspro.compose.components.fields.state.DropDownTextField
import br.com.fitnesspro.compose.components.fields.state.PagedDialogListTextField
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.compose.components.gallery.video.state.VideoGalleryState
import br.com.fitnesspro.core.state.ILoadingUIState
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.to.TOExercisePreDefinition
import br.com.fitnesspro.to.TOWorkoutGroupPreDefinition
import java.io.File
import java.time.temporal.ChronoUnit

data class PreDefinitionUIState(
    val title: String = "",
    val subtitle: String? = null,
    val messageDialogState: MessageDialogState = MessageDialogState(),
    val group: TextField = TextField(),
    val exercise: PagedDialogListTextField<TOExercise> = PagedDialogListTextField(),
    val exerciseOrder: TextField = TextField(),
    val sets: TextField = TextField(),
    val reps: TextField = TextField(),
    val rest: TextField = TextField(),
    val unitRest: DropDownTextField<ChronoUnit?> = DropDownTextField(),
    val duration: TextField = TextField(),
    val unitDuration: DropDownTextField<ChronoUnit?> = DropDownTextField(),
    val videoGalleryState: VideoGalleryState = VideoGalleryState(),
    var inactivateEnabled: Boolean = false,
    var showGroupField: Boolean = false,
    val toWorkoutGroupPreDefinition: TOWorkoutGroupPreDefinition = TOWorkoutGroupPreDefinition(),
    val toExercisePredefinition: TOExercisePreDefinition = TOExercisePreDefinition(),
    var notSavedVideoFiles: MutableList<File> = mutableListOf(),
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = {}
): ILoadingUIState