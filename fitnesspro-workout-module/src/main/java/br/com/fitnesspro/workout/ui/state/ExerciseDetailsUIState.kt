package br.com.fitnesspro.workout.ui.state

import androidx.paging.PagingData
import br.com.android.ui.compose.components.dialog.message.MessageDialogState
import br.com.android.ui.compose.components.states.ILoadingUIState
import br.com.android.ui.compose.components.states.ISuspendedLoadUIState
import br.com.android.ui.compose.components.states.IThrowableUIState
import br.com.android.ui.compose.components.tabs.state.TabState
import br.com.android.ui.compose.components.video.state.VideoGalleryState
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.tuple.ExerciseExecutionGroupedTuple
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

data class ExerciseDetailsUIState(
    val subtitle: String = "",
    val tabState: TabState = TabState(),
    val toExercise: TOExercise = TOExercise(),
    val videoGalleryState: VideoGalleryState = VideoGalleryState(),
    val evolutionList: Flow<PagingData<ExerciseExecutionGroupedTuple>> = emptyFlow(),
    override val messageDialogState: MessageDialogState = MessageDialogState(),
    override val showLoading: Boolean = false,
    override val onToggleLoading: () -> Unit = {},
    override var executeLoad: Boolean = true
): ILoadingUIState, IThrowableUIState, ISuspendedLoadUIState