package br.com.fitnesspro.workout.ui.state

import br.com.fitnesspro.compose.components.fields.state.DropDownTextField
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.compose.components.gallery.video.state.VideoGalleryState
import br.com.fitnesspro.core.state.ILoadingUIState
import br.com.fitnesspro.core.state.IThrowableUIState
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.to.TOExerciseExecution
import java.io.File
import java.time.temporal.ChronoUnit

data class RegisterEvolutionUIState(
    val title: String = "",
    val subtitle: String = "",
    val weight: TextField = TextField(),
    val repetitions: TextField = TextField(),
    val rest: TextField = TextField(),
    val restUnit: DropDownTextField<ChronoUnit?> = DropDownTextField(),
    val duration: TextField = TextField(),
    val durationUnit: DropDownTextField<ChronoUnit?> = DropDownTextField(),
    val toExerciseExecution: TOExerciseExecution = TOExerciseExecution(),
    val videoGalleryState: VideoGalleryState = VideoGalleryState(),
    var notSavedVideoFiles: MutableList<File> = mutableListOf(),
    val onFabVisibilityChange: (Boolean) -> Unit = {},
    val fabVisible: Boolean = true,
    override val messageDialogState: MessageDialogState = MessageDialogState(),
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = { }
): ILoadingUIState, IThrowableUIState