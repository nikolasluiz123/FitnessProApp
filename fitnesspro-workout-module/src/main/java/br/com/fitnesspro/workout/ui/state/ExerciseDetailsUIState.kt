package br.com.fitnesspro.workout.ui.state

import androidx.paging.PagingData
import br.com.fitnesspro.compose.components.fields.state.TabState
import br.com.fitnesspro.compose.components.gallery.video.state.VideoGalleryState
import br.com.fitnesspro.core.state.ILoadingUIState
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.tuple.ExerciseExecutionGroupedTuple
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class ExerciseDetailsUIState(
    val subtitle: String = "",
    val messageDialogState: MessageDialogState = MessageDialogState(),
    val tabState: TabState = TabState(),
    val toExercise: TOExercise = TOExercise(),
    val videoGalleryState: VideoGalleryState = VideoGalleryState(),
    val evolutionList: Flow<PagingData<ExerciseExecutionGroupedTuple>> = emptyFlow(),
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = {}
): ILoadingUIState