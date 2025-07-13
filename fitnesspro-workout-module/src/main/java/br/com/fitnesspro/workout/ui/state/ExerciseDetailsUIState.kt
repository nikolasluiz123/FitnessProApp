package br.com.fitnesspro.workout.ui.state

import br.com.fitnesspro.compose.components.fields.state.TabState
import br.com.fitnesspro.compose.components.gallery.video.state.VideoGalleryState
import br.com.fitnesspro.core.state.ILoadingUIState
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.to.TOExercise

data class ExerciseDetailsUIState(
    val subtitle: String = "",
    val messageDialogState: MessageDialogState = MessageDialogState(),
    val tabState: TabState = TabState(),
    val toExercise: TOExercise = TOExercise(),
    val videoGalleryState: VideoGalleryState = VideoGalleryState(),
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = {}
): ILoadingUIState