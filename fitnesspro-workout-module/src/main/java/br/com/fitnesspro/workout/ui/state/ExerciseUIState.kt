package br.com.fitnesspro.workout.ui.state

import br.com.android.ui.compose.components.dialog.message.MessageDialogState
import br.com.android.ui.compose.components.fields.dropdown.state.DropDownTextField
import br.com.android.ui.compose.components.fields.text.dialog.paged.state.PagedDialogListTextField
import br.com.android.ui.compose.components.fields.text.dialog.state.DialogListTextField
import br.com.android.ui.compose.components.fields.text.state.TextField
import br.com.android.ui.compose.components.states.ILoadingUIState
import br.com.android.ui.compose.components.states.ISuspendedLoadUIState
import br.com.android.ui.compose.components.states.IThrowableUIState
import br.com.android.ui.compose.components.tabs.state.TabState
import br.com.android.ui.compose.components.video.state.VideoGalleryState
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOWorkoutGroup
import java.io.File
import java.time.temporal.ChronoUnit

data class ExerciseUIState(
    var title: String = "",
    var subtitle: String = "",
    val tabState: TabState = TabState(),
    val group: DialogListTextField<TOWorkoutGroup> = DialogListTextField(),
    val exercise: PagedDialogListTextField<TOExercise> = PagedDialogListTextField(),
    val groupOrder: TextField = TextField(),
    val exerciseOrder: TextField = TextField(),
    val sets: TextField = TextField(),
    val reps: TextField = TextField(),
    val rest: TextField = TextField(),
    val unitRest: DropDownTextField<ChronoUnit?> = DropDownTextField(),
    val duration: TextField = TextField(),
    val unitDuration: DropDownTextField<ChronoUnit?> = DropDownTextField(),
    val observation: TextField = TextField(),
    val toExercise: TOExercise = TOExercise(),
    val authenticatedPerson: TOPerson = TOPerson(),
    val videoGalleryState: VideoGalleryState = VideoGalleryState(),
    var newVideoFileFromCamera: File? = null,
    override val messageDialogState: MessageDialogState = MessageDialogState(),
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = { },
    override var executeLoad: Boolean = true,
): ILoadingUIState, IThrowableUIState, ISuspendedLoadUIState
