package br.com.fitnesspro.workout.ui.state

import br.com.android.ui.compose.components.dialog.message.MessageDialogState
import br.com.android.ui.compose.components.fields.dropdown.state.DropDownTextField
import br.com.android.ui.compose.components.fields.text.dialog.paged.state.PagedDialogListTextField
import br.com.android.ui.compose.components.fields.text.state.TextField
import br.com.android.ui.compose.components.states.ILoadingUIState
import br.com.android.ui.compose.components.states.ISuspendedLoadUIState
import br.com.android.ui.compose.components.states.IThrowableUIState
import br.com.android.ui.compose.components.video.state.VideoGalleryState
import br.com.fitnesspro.to.TOExercisePreDefinition
import br.com.fitnesspro.to.TOWorkoutGroupPreDefinition
import java.io.File
import java.time.temporal.ChronoUnit

data class PreDefinitionUIState(
    val title: String = "",
    val subtitle: String? = null,
    val group: PagedDialogListTextField<TOWorkoutGroupPreDefinition> = PagedDialogListTextField(),
    val exercise: PagedDialogListTextField<TOExercisePreDefinition> = PagedDialogListTextField(),
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
    var authenticatedPersonId: String? = null,
    override val messageDialogState: MessageDialogState = MessageDialogState(),
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = {},
    override var executeLoad: Boolean = true
): ILoadingUIState, IThrowableUIState, ISuspendedLoadUIState