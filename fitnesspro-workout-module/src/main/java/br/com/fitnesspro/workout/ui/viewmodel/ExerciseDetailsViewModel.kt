package br.com.fitnesspro.workout.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.base.FitnessProStatefulViewModel
import br.com.fitnesspro.compose.components.tabs.Tab
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.tuple.ExerciseExecutionGroupedTuple
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.repository.ExerciseExecutionRepository
import br.com.fitnesspro.workout.repository.ExerciseRepository
import br.com.fitnesspro.workout.ui.navigation.ExerciseDetailsScreenArgs
import br.com.fitnesspro.workout.ui.navigation.exerciseDetailsScreenArguments
import br.com.fitnesspro.workout.ui.screen.details.enums.EnumTabsExerciseDetailsScreen
import br.com.fitnesspro.workout.ui.state.ExerciseDetailsUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ExerciseDetailsViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val globalEvents: GlobalEvents,
    private val exerciseRepository: ExerciseRepository,
    private val exerciseExecutionRepository: ExerciseExecutionRepository,
    savedStateHandle: SavedStateHandle
): FitnessProStatefulViewModel() {

    private val _uiState: MutableStateFlow<ExerciseDetailsUIState> = MutableStateFlow(ExerciseDetailsUIState())
    val uiState get() = _uiState.asStateFlow()

    val jsonArgs: String? = savedStateHandle[exerciseDetailsScreenArguments]

    init {
        initialLoadUIState()
        loadEvolutionList()
    }

    override fun initialLoadUIState() {
        _uiState.value = _uiState.value.copy(
            tabState = createTabState(
                getCurrentState = { _uiState.value.tabState },
                updateState = { newState -> _uiState.value = _uiState.value.copy(tabState = newState) },
                tabs = getTabsWithDefaultState()
            ),
            messageDialogState = createMessageDialogState(
                getCurrentState = { _uiState.value.messageDialogState },
                updateState = { newState -> _uiState.value = _uiState.value.copy(messageDialogState = newState) }
            ),
            videoGalleryState = createVideoGalleryState(
                title = context.getString(R.string.exercise_screen_video_gallery_title),
                isScrollEnabled = false,
                showDeleteButton = false,
                getCurrentState = { _uiState.value.videoGalleryState },
                updateState = { newState -> _uiState.value = _uiState.value.copy(videoGalleryState = newState) }
            ),
            onToggleLoading = {
                _uiState.value = _uiState.value.copy(showLoading = _uiState.value.showLoading.not())
            }
        )
    }

    private fun loadEvolutionList() {
        _uiState.value = _uiState.value.copy(
            evolutionList = getListExerciseExecutionGrouped()
        )
    }

    override fun getErrorMessageFrom(throwable: Throwable): String {
        return context.getString(br.com.fitnesspro.common.R.string.unknown_error_message)
    }

    override fun onShowErrorDialog(message: String) {
        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(message = message)
    }

    override fun getGlobalEventsBus(): GlobalEvents {
        return globalEvents
    }

    fun loadUIStateWithDatabaseInfos() {
        launch {
            val args = jsonArgs?.fromJsonNavParamToArgs(ExerciseDetailsScreenArgs::class.java)!!
            val toExercise = exerciseRepository.findById(args.exerciseId)

            _uiState.value = _uiState.value.copy(
                subtitle = toExercise.name!!,
                toExercise = toExercise,
                executeLoad = false
            )
        }
    }

    private fun getTabsWithDefaultState(): MutableList<Tab> {
        return mutableListOf(
            Tab(
                enum = EnumTabsExerciseDetailsScreen.ORIENTATION,
                selected = true,
                enabled = true
            ),
            Tab(
                enum = EnumTabsExerciseDetailsScreen.EVOLUTION,
                selected = false,
                enabled = true
            )
        )
    }

    private fun getListExerciseExecutionGrouped(): Flow<PagingData<ExerciseExecutionGroupedTuple>> {
        val args = jsonArgs?.fromJsonNavParamToArgs(ExerciseDetailsScreenArgs::class.java)!!
        return exerciseExecutionRepository.getListExerciseExecutionGrouped(args.exerciseId).flow
    }
}