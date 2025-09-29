package br.com.fitnesspro.workout.ui.state

import br.com.android.ui.compose.components.dialog.message.MessageDialogState
import br.com.android.ui.compose.components.fields.dropdown.state.DropDownTextField
import br.com.android.ui.compose.components.fields.text.state.TextField
import br.com.android.ui.compose.components.states.ILoadingUIState
import br.com.android.ui.compose.components.states.ISuspendedLoadUIState
import br.com.android.ui.compose.components.states.IThrowableUIState
import br.com.android.ui.compose.components.video.state.VideoGalleryState
import br.com.fitnesspro.to.TOExerciseExecution
import java.io.File
import java.time.Duration
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
    val bottomBarVisible: Boolean = true,
    val chronometerTime: Duration = Duration.ZERO,
    val chronometerRunning: Boolean = false,
    override val messageDialogState: MessageDialogState = MessageDialogState(),
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = { },
    override var executeLoad: Boolean = true
): ILoadingUIState, IThrowableUIState, ISuspendedLoadUIState