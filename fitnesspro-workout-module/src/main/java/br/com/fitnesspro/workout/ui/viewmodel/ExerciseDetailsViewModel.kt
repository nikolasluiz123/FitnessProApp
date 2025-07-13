package br.com.fitnesspro.workout.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.FitnessProViewModel
import br.com.fitnesspro.compose.components.fields.state.TabState
import br.com.fitnesspro.compose.components.gallery.video.state.VideoGalleryState
import br.com.fitnesspro.compose.components.tabs.Tab
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.repository.ExerciseRepository
import br.com.fitnesspro.workout.ui.navigation.ExerciseDetailsScreenArgs
import br.com.fitnesspro.workout.ui.navigation.exerciseDetailsScreenArguments
import br.com.fitnesspro.workout.ui.screen.details.enums.EnumTabsExerciseDetailsScreen
import br.com.fitnesspro.workout.ui.state.ExerciseDetailsUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ExerciseDetailsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val globalEvents: GlobalEvents,
    private val exerciseRepository: ExerciseRepository,
    savedStateHandle: SavedStateHandle
): FitnessProViewModel() {

    private val _uiState: MutableStateFlow<ExerciseDetailsUIState> = MutableStateFlow(ExerciseDetailsUIState())
    val uiState get() = _uiState.asStateFlow()

    val jsonArgs: String? = savedStateHandle[exerciseDetailsScreenArguments]

    init {
        initialLoadUIState()
        loadUIStateWithDatabaseInfos()
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

    private fun initialLoadUIState() {
        _uiState.value = _uiState.value.copy(
            tabState = initializeTabState(),
            messageDialogState = initializeMessageDialogState(),
            videoGalleryState = initializeVideoGalleryState(),
            onToggleLoading = {
                _uiState.value = _uiState.value.copy(showLoading = _uiState.value.showLoading.not())
            }
        )
    }

    private fun loadUIStateWithDatabaseInfos() {
        launch {
            val args = jsonArgs?.fromJsonNavParamToArgs(ExerciseDetailsScreenArgs::class.java)!!
            val toExercise = exerciseRepository.findById(args.exerciseId)

            _uiState.value = _uiState.value.copy(
                subtitle = toExercise.name!!,
                toExercise = toExercise
            )
        }
    }

    private fun initializeTabState(): TabState {
        return TabState(
            tabs = getTabsWithDefaultState(),
            onSelectTab = { selectedTab ->
                _uiState.value = _uiState.value.copy(
                    tabState = _uiState.value.tabState.copy(
                        tabs = getTabListWithSelectedTab(selectedTab)
                    )
                )
            }
        )
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

    private fun getTabListWithSelectedTab(selectedTab: Tab): MutableList<Tab> {
        return _uiState.value.tabState.tabs.map { tab ->
            tab.copy(selected = tab.enum == selectedTab.enum)
        }.toMutableList()
    }

    private fun initializeMessageDialogState(): MessageDialogState {
        return MessageDialogState(
            onShowDialog = { type, message, onConfirm, onCancel ->
                _uiState.value = _uiState.value.copy(
                    messageDialogState = _uiState.value.messageDialogState.copy(
                        dialogType = type,
                        dialogMessage = message,
                        showDialog = true,
                        onConfirm = onConfirm,
                        onCancel = onCancel
                    )
                )
            },
            onHideDialog = {
                _uiState.value = _uiState.value.copy(
                    messageDialogState = _uiState.value.messageDialogState.copy(
                        showDialog = false
                    )
                )
            }
        )
    }

    private fun initializeVideoGalleryState(): VideoGalleryState {
        return VideoGalleryState(
            title = context.getString(R.string.exercise_screen_video_gallery_title),
            isScrollEnabled = false,
            onViewModeChange = {
                _uiState.value = _uiState.value.copy(
                    videoGalleryState = _uiState.value.videoGalleryState.copy(
                        viewMode = it
                    )
                )
            }
        )
    }
}