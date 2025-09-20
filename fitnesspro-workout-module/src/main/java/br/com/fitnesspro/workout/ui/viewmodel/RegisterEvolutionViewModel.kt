package br.com.fitnesspro.workout.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.base.FitnessProStatefulViewModel
import br.com.fitnesspro.compose.components.fields.menu.getChronoUnitMenuItems
import br.com.fitnesspro.core.callback.showConfirmationDialog
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.bestChronoUnit
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.formatToDecimal
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.core.extensions.getChronoUnitLabel
import br.com.fitnesspro.core.extensions.getStringFromConvertedChronoUnitValue
import br.com.fitnesspro.core.extensions.toStringOrEmpty
import br.com.fitnesspro.core.utils.FileUtils
import br.com.fitnesspro.core.utils.VideoUtils
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.to.TOExerciseExecution
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.repository.ExerciseExecutionRepository
import br.com.fitnesspro.workout.repository.ExerciseRepository
import br.com.fitnesspro.workout.repository.VideoRepository
import br.com.fitnesspro.workout.ui.navigation.RegisterEvolutionScreenArgs
import br.com.fitnesspro.workout.ui.navigation.registerEvolutionScreenArguments
import br.com.fitnesspro.workout.ui.state.RegisterEvolutionUIState
import br.com.fitnesspro.workout.usecase.exercise.InactivateExerciseExecutionUseCase
import br.com.fitnesspro.workout.usecase.exercise.SaveExerciseExecutionUseCase
import br.com.fitnesspro.workout.usecase.exercise.enums.EnumValidatedExerciseExecutionFields
import br.com.fitnesspro.workout.usecase.exercise.exception.VideoException
import br.com.fitnesspro.workout.usecase.exercise.video.InactivateVideoExecutionUseCase
import br.com.fitnesspro.workout.usecase.exercise.video.SaveVideoExecutionUseCase
import br.com.fitnesspro.workout.usecase.exercise.video.gallery.SaveVideoExecutionFromGalleryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.time.delay
import java.io.File
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class RegisterEvolutionViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val globalEvents: GlobalEvents,
    private val exerciseRepository: ExerciseRepository,
    private val videoRepository: VideoRepository,
    private val executionRepository: ExerciseExecutionRepository,
    private val saveVideoExecutionUseCase: SaveVideoExecutionUseCase,
    private val saveVideoExecutionFromGalleryUseCase: SaveVideoExecutionFromGalleryUseCase,
    private val saveExerciseExecutionUseCase: SaveExerciseExecutionUseCase,
    private val inactivateVideoExecutionUseCase: InactivateVideoExecutionUseCase,
    private val inactivateExerciseExecutionUseCase: InactivateExerciseExecutionUseCase,
    savedStateHandle: SavedStateHandle
): FitnessProStatefulViewModel() {

    private val _uiState: MutableStateFlow<RegisterEvolutionUIState> = MutableStateFlow(RegisterEvolutionUIState())
    val uiState get() = _uiState.asStateFlow()

    val jsonArgs: String? = savedStateHandle[registerEvolutionScreenArguments]

    init {
        initialLoadUIState()
    }

    override fun initialLoadUIState() {
        _uiState.value = _uiState.value.copy(
            weight = createDoubleValueTextFieldState(
                getCurrentState = { _uiState.value.weight },
                onValueChange = { state, value ->
                    _uiState.value = _uiState.value.copy(
                        weight = state,
                        toExerciseExecution = _uiState.value.toExerciseExecution.copy(weight = value)
                    )
                }
            ),
            repetitions = createIntValueTextFieldState(
                getCurrentState = { _uiState.value.repetitions },
                onValueChange = { state, value ->
                    _uiState.value = _uiState.value.copy(
                        repetitions = state,
                        toExerciseExecution = _uiState.value.toExerciseExecution.copy(repetitions = value)
                    )
                }
            ),
            rest = createLongValueTextFieldState(
                getCurrentState = { _uiState.value.rest },
                onValueChange = { state, value ->
                    _uiState.value = _uiState.value.copy(
                        rest = state,
                        toExerciseExecution = _uiState.value.toExerciseExecution.copy(rest = value)
                    )
                }
            ),
            restUnit = createDropDownTextFieldState(
                items = ChronoUnit.entries.getChronoUnitMenuItems(context),
                getCurrentState = { _uiState.value.restUnit },
                updateState = { _uiState.value = _uiState.value.copy(restUnit = it) },
                onItemClick = {
                    _uiState.value = _uiState.value.copy(
                        toExerciseExecution = _uiState.value.toExerciseExecution.copy(
                            restUnit = it
                        )
                    )
                }
            ),
            duration = createLongValueTextFieldState(
                getCurrentState = { _uiState.value.duration },
                onValueChange = { state, value ->
                    _uiState.value = _uiState.value.copy(
                        duration = state,
                        toExerciseExecution = _uiState.value.toExerciseExecution.copy(duration = value)
                    )
                }
            ),
            durationUnit = createDropDownTextFieldState(
                items = ChronoUnit.entries.getChronoUnitMenuItems(context),
                getCurrentState = { _uiState.value.durationUnit },
                updateState = { _uiState.value = _uiState.value.copy(durationUnit = it) },
                onItemClick = {
                    _uiState.value = _uiState.value.copy(
                        toExerciseExecution = _uiState.value.toExerciseExecution.copy(
                            durationUnit = it
                        )
                    )
                }
            ),
            videoGalleryState = createVideoGalleryState(
                getCurrentState = { _uiState.value.videoGalleryState },
                updateState = { _uiState.value = _uiState.value.copy(videoGalleryState = it) },
                title = context.getString(R.string.register_evolution_screen_video_gallery_title)
            ),
            messageDialogState = createMessageDialogState(
                getCurrentState = { _uiState.value.messageDialogState },
                updateState = { _uiState.value = _uiState.value.copy(messageDialogState = it) }
            ),
            onToggleLoading = {
                _uiState.value = _uiState.value.copy(showLoading = _uiState.value.showLoading.not())
            },
            onFabVisibilityChange = {
                _uiState.value = _uiState.value.copy(bottomBarVisible = it)
            }
        )
    }

    override fun getErrorMessageFrom(throwable: Throwable): String {
        return context.getString(br.com.fitnesspro.common.R.string.unknown_error_message)
    }

    override fun onShowErrorDialog(message: String) {
        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(message = message)
    }

    override fun onError(throwable: Throwable) {
        super.onError(throwable)

        if (throwable is VideoException) {
            _uiState.value.notSavedVideoFiles.removeAt(_uiState.value.notSavedVideoFiles.lastIndex)
        }

        if (_uiState.value.showLoading) {
            _uiState.value.onToggleLoading()
        }
    }

    override fun getGlobalEventsBus(): GlobalEvents {
        return globalEvents
    }

    fun loadUIStateWithDatabaseInfos() {
        launch {
            val args = jsonArgs?.fromJsonNavParamToArgs(RegisterEvolutionScreenArgs::class.java)!!
            val toExerciseExecution = args.exerciseExecutionId?.let { executionRepository.findById(it) }

            loadChronometerRunning(args)
            loadTitle(toExerciseExecution)
            loadExecutionInfosEdition(toExerciseExecution)
            loadExerciseInfos(args)
            loadVideos(exerciseExecutionId = args.exerciseExecutionId)

            _uiState.value.executeLoad = false
        }
    }

    private fun loadChronometerRunning(args: RegisterEvolutionScreenArgs) {
        _uiState.value = _uiState.value.copy(
            chronometerRunning = args.exerciseExecutionId == null
        )
    }

    private fun loadExecutionInfosEdition(toExerciseExecution: TOExerciseExecution?) {
        toExerciseExecution?.let { to ->
            val convertedRest =  to.restUnit.getStringFromConvertedChronoUnitValue(to.rest)
            val convertedDuration = to.durationUnit.getStringFromConvertedChronoUnitValue(to.duration)

            _uiState.value = _uiState.value.copy(
                weight = _uiState.value.weight.copy(value = to.weight.formatToDecimal()),
                repetitions = _uiState.value.repetitions.copy(value = to.repetitions.toStringOrEmpty()),
                rest = _uiState.value.rest.copy(value = convertedRest),
                restUnit = _uiState.value.restUnit.copy(value = to.rest.getChronoUnitLabel(context)),
                duration = _uiState.value.duration.copy(value = convertedDuration),
                durationUnit = _uiState.value.durationUnit.copy(value = to.duration.getChronoUnitLabel(context)),
                toExerciseExecution = to.copy(
                    duration = convertedDuration.toLongOrNull(),
                    rest = convertedRest.toLongOrNull()
                )
            )
        }
    }

    private fun loadTitle(toExerciseExecution: TOExerciseExecution?) {
        _uiState.value = _uiState.value.copy(
            title = getTitle(toExerciseExecution)
        )
    }

    private fun getTitle(toExecution: TOExerciseExecution?): String {
        return toExecution?.let {
            context.getString(
                R.string.register_evolution_screen_title_edit_exercise,
                it.executionStartTime.format(EnumDateTimePatterns.DATE)
            )
        } ?: context.getString(R.string.register_evolution_screen_title_new_exercise)
    }

    private suspend fun loadExerciseInfos(args: RegisterEvolutionScreenArgs) {
        val toExercise = exerciseRepository.findById(args.exerciseId)

        _uiState.value = _uiState.value.copy(
            subtitle = toExercise.name!!,
        )

        if (args.exerciseExecutionId == null) {
            _uiState.value = _uiState.value.copy(
                repetitions = _uiState.value.repetitions.copy(
                    value = toExercise.repetitions.toStringOrEmpty(),
                ),
                rest = _uiState.value.rest.copy(
                    value = toExercise.rest?.bestChronoUnit().getStringFromConvertedChronoUnitValue(toExercise.rest),
                ),
                restUnit = _uiState.value.restUnit.copy(
                    value = toExercise.rest.getChronoUnitLabel(context)
                ),
                duration = _uiState.value.duration.copy(
                    value = toExercise.duration?.bestChronoUnit().getStringFromConvertedChronoUnitValue(toExercise.duration)
                ),
                durationUnit = _uiState.value.durationUnit.copy(
                    value = toExercise.duration.getChronoUnitLabel(context)
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
    }

    fun onOpenCameraVideo(file: File) {
        _uiState.value.notSavedVideoFiles.add(file)
    }

    fun onFinishVideoRecording(onSuccess: () -> Unit) {
        launch {
            val exerciseExecutionId = _uiState.value.toExerciseExecution.id

            if (exerciseExecutionId != null) {
                onFinishVideoRecordingEdition(exerciseExecutionId, onSuccess)
            } else {
                onFinishVideoRecordingCreation()
            }
        }
    }

    private suspend fun onFinishVideoRecordingEdition(exerciseExecutionId: String, onSuccess: () -> Unit) {
        val validationResult = saveVideoExecutionUseCase(
            exerciseExecutionId = exerciseExecutionId,
            videoFile = _uiState.value.notSavedVideoFiles.first()
        )

        if (validationResult != null) {
            onShowErrorDialog(validationResult.message)
        } else {
            onSuccess()
            loadVideos(exerciseExecutionId)
            _uiState.value.notSavedVideoFiles.clear()
        }
    }

    private suspend fun onFinishVideoRecordingCreation() {
        saveVideoExecutionUseCase.validateVideosLimit(videoCount = _uiState.value.notSavedVideoFiles.size)
        saveVideoExecutionUseCase.validateVideoFile(videoFile = _uiState.value.notSavedVideoFiles.last())
    }

    private suspend fun loadVideos(exerciseExecutionId: String?) {
        exerciseExecutionId?.let { id ->
            val videoFilePaths = videoRepository.getVideoExerciseExecution(id)

            _uiState.value = _uiState.value.copy(
                videoGalleryState = _uiState.value.videoGalleryState.copy(
                    videoPaths = videoFilePaths,
                    thumbCache = videoFilePaths.associateWith { filePath ->
                        VideoUtils.generateVideoThumbnail(filePath)
                    }
                )
            )
        }
    }

    fun onVideoSelectedOnGallery(uri: Uri, onSuccess: () -> Unit) {
        launch {
            val exerciseExecutionId = _uiState.value.toExerciseExecution.id

            if (exerciseExecutionId != null) {
                onVideoSelectedOnGalleryEdition(exerciseExecutionId, uri, onSuccess)
            } else {
                onVideoSelectedOnGalleryCreation(uri)
            }
        }
    }

    private suspend fun onVideoSelectedOnGalleryEdition(
        exerciseExecutionId: String,
        uri: Uri,
        onSuccess: () -> Unit
    ) {
        val validationResult = saveVideoExecutionFromGalleryUseCase(
            exerciseExecutionId = exerciseExecutionId,
            uri = uri
        )

        if (validationResult != null) {
            onShowErrorDialog(validationResult.message)
        } else {
            onSuccess()
            loadVideos(exerciseExecutionId)
        }
    }

    private suspend fun onVideoSelectedOnGalleryCreation(uri: Uri) {
        VideoUtils.createVideoFileFromUri(context, uri)?.let { file ->
            _uiState.value.notSavedVideoFiles.add(file)
            saveVideoExecutionUseCase.validateVideosLimit(videoCount = _uiState.value.notSavedVideoFiles.size)
            saveVideoExecutionUseCase.validateVideoFile(videoFile = file)
        }
    }

    fun onVideoClick(filePath: String, onOpenVideo: (String) -> Unit) {
        if (FileUtils.getFileExists(filePath)) {
            onOpenVideo(filePath)
        } else {
            _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(
                message = context.getString(R.string.exercise_screen_msg_video_not_found)
            )
        }
    }

    fun onSaveRegisterEvolution(onSuccess: () -> Unit) {
        launch {
            val validationResult = saveExerciseExecutionUseCase(
                toExerciseExecution = _uiState.value.toExerciseExecution,
                videoFiles = _uiState.value.notSavedVideoFiles
            )

            if (validationResult.isEmpty()) {
                onSuccess()
            } else {
                _uiState.value.onToggleLoading()
                showValidationMessages(validationResult)
            }
        }
    }

    private fun showValidationMessages(validationResult: List<FieldValidationError<EnumValidatedExerciseExecutionFields>>) {
        validationResult.forEach {
            when (it.field!!) {
                EnumValidatedExerciseExecutionFields.DURATION -> {
                    _uiState.value = _uiState.value.copy(
                        duration = _uiState.value.duration.copy(errorMessage = it.message)
                    )
                }

                EnumValidatedExerciseExecutionFields.UNIT_DURATION -> {
                    _uiState.value = _uiState.value.copy(
                        durationUnit = _uiState.value.durationUnit.copy(errorMessage = it.message)
                    )
                }

                EnumValidatedExerciseExecutionFields.REST -> {
                    _uiState.value = _uiState.value.copy(
                        rest = _uiState.value.rest.copy(errorMessage = it.message)
                    )
                }

                EnumValidatedExerciseExecutionFields.UNIT_REST -> {
                    _uiState.value = _uiState.value.copy(
                        restUnit = _uiState.value.restUnit.copy(errorMessage = it.message)
                    )
                }
            }
        }
    }

    fun onDeleteVideo(filePath: String, onSuccess: () -> Unit) {
        _uiState.value.messageDialogState.onShowDialog?.showConfirmationDialog(
            message = context.getString(R.string.msg_delete_video_confirmation),
            onConfirm = {
                _uiState.value.onToggleLoading()

                launch {
                    inactivateVideoExecutionUseCase(listOf(filePath))
                    loadVideos(_uiState.value.toExerciseExecution.id)
                    onSuccess()
                    _uiState.value.onToggleLoading()
                }
            }
        )
    }

    fun onInactivateExecution(onSuccess: () -> Unit) {
        _uiState.value.toExerciseExecution.id?.let {
            _uiState.value.messageDialogState.onShowDialog?.showConfirmationDialog(
                message = context.getString(R.string.register_evolution_screen_msg_confirm_inactivate),
                onConfirm = {
                    _uiState.value.onToggleLoading()

                    launch {
                        inactivateExerciseExecutionUseCase(it)
                        onSuccess()
                    }
                }
            )
        }
    }

    fun onStopChronometer() {
        _uiState.value = _uiState.value.copy(
            chronometerRunning = false,
            toExerciseExecution = _uiState.value.toExerciseExecution.copy(
                executionEndTime = Instant.now()
            )
        )
    }

    suspend fun onUpdateChronometer() {
        while (true) {
            _uiState.value = _uiState.value.copy(
                chronometerTime = _uiState.value.chronometerTime.plus(1, ChronoUnit.SECONDS)
            )

            delay(Duration.of(1, ChronoUnit.SECONDS))
        }
    }
}