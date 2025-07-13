package br.com.fitnesspro.workout.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.FitnessProViewModel
import br.com.fitnesspro.compose.components.fields.menu.MenuItem
import br.com.fitnesspro.compose.components.fields.menu.getLabelOrEmptyIfNullValue
import br.com.fitnesspro.compose.components.fields.state.DropDownTextField
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.compose.components.gallery.video.state.VideoGalleryState
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.extensions.bestChronoUnit
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.core.extensions.millisTo
import br.com.fitnesspro.core.extensions.toIntOrNull
import br.com.fitnesspro.core.extensions.toStringOrEmpty
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.repository.ExerciseRepository
import br.com.fitnesspro.workout.ui.navigation.RegisterEvolutionScreenArgs
import br.com.fitnesspro.workout.ui.navigation.registerEvolutionScreenArguments
import br.com.fitnesspro.workout.ui.state.RegisterEvolutionUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class RegisterEvolutionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val globalEvents: GlobalEvents,
    private val exerciseRepository: ExerciseRepository,
    savedStateHandle: SavedStateHandle
): FitnessProViewModel() {

    private val _uiState: MutableStateFlow<RegisterEvolutionUIState> = MutableStateFlow(RegisterEvolutionUIState())
    val uiState get() = _uiState.asStateFlow()

    val jsonArgs: String? = savedStateHandle[registerEvolutionScreenArguments]

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
            repetitions = initializeTextFieldReps(),
            rest = initializeTextFieldRest(),
            restUnit = initializeDropDownTextFieldrestUnit(),
            duration = initializeTextFieldDuration(),
            durationUnit = initializeDropDownTextFieldUnitDuration(),
            videoGalleryState = initializeVideoGalleryState(),
            onToggleLoading = {
                _uiState.value = _uiState.value.copy(showLoading = _uiState.value.showLoading.not())
            },
            onFabVisibilityChange = {
                _uiState.value = _uiState.value.copy(fabVisible = it)
            }
        )
    }

    private fun initializeTextFieldReps(): TextField {
        return TextField(
            onChange = {
                _uiState.value = _uiState.value.copy(
                    repetitions = _uiState.value.repetitions.copy(
                        value = it,
                        errorMessage = ""
                    ),
                    toExerciseExecution = _uiState.value.toExerciseExecution.copy(repetitions = it.toIntOrNull())
                )
            }
        )
    }

    private fun initializeTextFieldRest(): TextField {
        return TextField(
            onChange = {
                _uiState.value = _uiState.value.copy(
                    rest = _uiState.value.rest.copy(
                        value = it,
                        errorMessage = ""
                    ),
                    toExerciseExecution = _uiState.value.toExerciseExecution.copy(rest = it.toLongOrNull())
                )
            }
        )
    }

    private fun initializeDropDownTextFieldrestUnit(): DropDownTextField<ChronoUnit?> {
        val items = getChronoUnitMenuItems()

        return DropDownTextField(
            dataList = items,
            dataListFiltered = items,
            onDropDownDismissRequest = {
                _uiState.value = _uiState.value.copy(
                    restUnit = _uiState.value.restUnit.copy(expanded = false)
                )
            },
            onDropDownExpandedChange = {
                _uiState.value = _uiState.value.copy(
                    restUnit = _uiState.value.restUnit.copy(expanded = it)
                )
            },
            onDataListItemClick = {
                _uiState.value = _uiState.value.copy(
                    restUnit = _uiState.value.restUnit.copy(
                        value = it.getLabelOrEmptyIfNullValue(),
                        errorMessage = ""
                    ),
                    toExerciseExecution = _uiState.value.toExerciseExecution.copy(
                        restUnit = it.value
                    )
                )

                _uiState.value.restUnit.onDropDownDismissRequest()
            }
        )
    }

    private fun initializeTextFieldDuration(): TextField {
        return TextField(
            onChange = {
                _uiState.value = _uiState.value.copy(
                    duration = _uiState.value.duration.copy(
                        value = it,
                        errorMessage = ""
                    ),
                    toExerciseExecution = _uiState.value.toExerciseExecution.copy(duration = it.toLongOrNull())
                )
            }
        )
    }

    private fun initializeDropDownTextFieldUnitDuration(): DropDownTextField<ChronoUnit?> {
        val items = getChronoUnitMenuItems()

        return DropDownTextField(
            dataList = items,
            dataListFiltered = items,
            onDropDownDismissRequest = {
                _uiState.value = _uiState.value.copy(
                    durationUnit = _uiState.value.durationUnit.copy(expanded = false)
                )
            },
            onDropDownExpandedChange = {
                _uiState.value = _uiState.value.copy(
                    durationUnit = _uiState.value.durationUnit.copy(expanded = it)
                )
            },
            onDataListItemClick = {
                _uiState.value = _uiState.value.copy(
                    durationUnit = _uiState.value.durationUnit.copy(
                        value = it.getLabelOrEmptyIfNullValue(),
                        errorMessage = ""
                    ),
                    toExerciseExecution = _uiState.value.toExerciseExecution.copy(
                        durationUnit = it.value
                    )
                )

                _uiState.value.durationUnit.onDropDownDismissRequest()
            }
        )
    }

    private fun initializeVideoGalleryState(): VideoGalleryState {
        return VideoGalleryState(
            title = context.getString(R.string.register_evolution_screen_video_gallery_title),
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

    private fun getChronoUnitMenuItems(): List<MenuItem<ChronoUnit?>> {
        val units = ChronoUnit.entries.slice(ChronoUnit.SECONDS.ordinal..ChronoUnit.HOURS.ordinal)

        return units.map { unit ->
            MenuItem(
                label = getLabelFromChronoUnit(unit),
                value = unit
            )
        }
    }

    private fun getLabelFromChronoUnit(unit: ChronoUnit): String {
        return when (unit) {
            ChronoUnit.SECONDS -> context.getString(R.string.exercise_screen_chrono_unit_seconds)
            ChronoUnit.MINUTES -> context.getString(R.string.exercise_screen_chrono_unit_minutes)
            ChronoUnit.HOURS -> context.getString(R.string.exercise_screen_chrono_unit_hours)
            else -> throw IllegalArgumentException("Valor invalido para recuperar um label de ChronoUnit")
        }
    }

    private fun loadUIStateWithDatabaseInfos() {
        launch {
            val args = jsonArgs?.fromJsonNavParamToArgs(RegisterEvolutionScreenArgs::class.java)!!

            // TODO - Carregar informacoes de edicao, pode ser que modifique o lugar que o titulo e
            //  carregado
            _uiState.value = _uiState.value.copy(
                title = getTitle(args.exerciseExecutionId)
            )

            loadExerciseInfos(args)
        }
    }

    private fun getTitle(exerciseExecutionId: String?): String {
        // TODO - Isso precisa ser alterado para adicionar a data no titulo quando for edicao

        return if (exerciseExecutionId.isNullOrEmpty()) {
            context.getString(R.string.register_evolution_screen_title_new_exercise)
        } else {
            context.getString(R.string.register_evolution_screen_title_edit_exercise)
        }
    }

    private suspend fun loadExerciseInfos(args: RegisterEvolutionScreenArgs) {
        val toExercise = exerciseRepository.findById(args.exerciseId)

        _uiState.value = _uiState.value.copy(
            subtitle = toExercise.name!!,
            repetitions = _uiState.value.repetitions.copy(
                value = toExercise.repetitions.toStringOrEmpty(),
            ),
            rest = _uiState.value.rest.copy(
                value = getConvertedRestFrom(toExercise)
            ),
            restUnit = _uiState.value.restUnit.copy(
                value = getUnitRestFrom(toExercise)
            ),
            duration = _uiState.value.duration.copy(
                value = getConvertedDurationFrom(toExercise)
            ),
            durationUnit = _uiState.value.durationUnit.copy(
                value = getUnitDurationFrom(toExercise)
            ),
            toExerciseExecution = _uiState.value.toExerciseExecution.copy(
                repetitions = toExercise.repetitions,
                rest = toExercise.rest,
                restUnit = toExercise.unitRest,
                duration = toExercise.duration,
                durationUnit = toExercise.unitDuration,
                exerciseId = toExercise.id
            )
        )
    }

    private fun getUnitDurationFrom(toExercise: TOExercise): String {
        return toExercise.duration?.bestChronoUnit()?.let { getLabelFromChronoUnit(it) } ?: ""
    }

    private fun getUnitRestFrom(toExercise: TOExercise): String {
        return toExercise.rest?.bestChronoUnit()?.let { getLabelFromChronoUnit(it) } ?: ""
    }

    private fun getConvertedDurationFrom(toExercise: TOExercise): String {
        return toExercise.unitDuration?.let { toExercise.duration?.millisTo(it) }.toStringOrEmpty()
    }

    private fun getConvertedRestFrom(toExercise: TOExercise): String {
        return toExercise.unitRest?.let { toExercise.rest?.millisTo(it) }.toStringOrEmpty()
    }

}