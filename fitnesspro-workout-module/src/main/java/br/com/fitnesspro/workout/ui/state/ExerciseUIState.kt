package br.com.fitnesspro.workout.ui.state

import br.com.fitnesspro.compose.components.fields.state.DialogListTextField
import br.com.fitnesspro.compose.components.fields.state.DropDownTextField
import br.com.fitnesspro.compose.components.fields.state.PagedDialogListTextField
import br.com.fitnesspro.compose.components.fields.state.TabState
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.compose.components.gallery.video.state.VideoGalleryState
import br.com.fitnesspro.core.state.ILoadingUIState
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOVideoExercise
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
    val messageDialogState: MessageDialogState = MessageDialogState(),
    val toExercise: TOExercise = TOExercise(),
    val authenticatedPerson: TOPerson = TOPerson(),
    val videoGalleryState: VideoGalleryState = VideoGalleryState(),
    var newVideoFileFromCamera: File? = null,
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = { },
): ILoadingUIState
