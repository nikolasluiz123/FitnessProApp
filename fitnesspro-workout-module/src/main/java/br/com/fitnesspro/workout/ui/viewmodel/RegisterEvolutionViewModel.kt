package br.com.fitnesspro.workout.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.FitnessProViewModel
import br.com.fitnesspro.compose.components.fields.menu.MenuItem
import br.com.fitnesspro.compose.components.fields.menu.getLabelOrEmptyIfNullValue
import br.com.fitnesspro.compose.components.fields.state.DropDownTextField
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.compose.components.gallery.video.state.VideoGalleryState
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.extensions.bestChronoUnit
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.formatToDecimal
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.core.extensions.millisTo
import br.com.fitnesspro.core.extensions.openVideoPlayer
import br.com.fitnesspro.core.extensions.toDoubleValue
import br.com.fitnesspro.core.extensions.toIntOrNull
import br.com.fitnesspro.core.extensions.toStringOrEmpty
import br.com.fitnesspro.core.state.MessageDialogState
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
import br.com.fitnesspro.workout.usecase.exercise.SaveExerciseExecutionUseCase
import br.com.fitnesspro.workout.usecase.exercise.enums.EnumValidatedExerciseExecutionFields
import br.com.fitnesspro.workout.usecase.exercise.exception.VideoException
import br.com.fitnesspro.workout.usecase.exercise.video.SaveVideoExecutionUseCase
import br.com.fitnesspro.workout.usecase.exercise.video.gallery.SaveVideoExecutionFromGalleryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class RegisterEvolutionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val globalEvents: GlobalEvents,
    private val exerciseRepository: ExerciseRepository,
    private val videoRepository: VideoRepository,
    private val executionRepository: ExerciseExecutionRepository,
    private val saveVideoExecutionUseCase: SaveVideoExecutionUseCase,
    private val saveVideoExecutionFromGalleryUseCase: SaveVideoExecutionFromGalleryUseCase,
    private val saveExerciseExecutionUseCase: SaveExerciseExecutionUseCase,
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

    private fun initialLoadUIState() {
        _uiState.value = _uiState.value.copy(
            weight = initializeTextFieldWeight(),
            repetitions = initializeTextFieldReps(),
            rest = initializeTextFieldRest(),
            restUnit = initializeDropDownTextFieldrestUnit(),
            duration = initializeTextFieldDuration(),
            durationUnit = initializeDropDownTextFieldUnitDuration(),
            videoGalleryState = initializeVideoGalleryState(),
            messageDialogState = initializeMessageDialogState(),
            onToggleLoading = {
                _uiState.value = _uiState.value.copy(showLoading = _uiState.value.showLoading.not())
            },
            onFabVisibilityChange = {
                _uiState.value = _uiState.value.copy(fabVisible = it)
            }
        )
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

    private fun initializeTextFieldWeight(): TextField {
        return TextField(
            onChange = {
                _uiState.value = _uiState.value.copy(
                    weight = _uiState.value.weight.copy(
                        value = it,
                        errorMessage = ""
                    ),
                    toExerciseExecution = _uiState.value.toExerciseExecution.copy(weight = it.toDoubleValue())
                )
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
            val toExerciseExecution = args.exerciseExecutionId?.let { executionRepository.findById(it) }

            loadTitle(toExerciseExecution)
            loadExecutionInfosEdition(toExerciseExecution)
            loadExerciseInfos(args)
            loadVideos(exerciseExecutionId = args.exerciseExecutionId)
        }
    }

    private fun loadExecutionInfosEdition(toExerciseExecution: TOExerciseExecution?) {
        toExerciseExecution?.let { to ->
            val convertedRest = getConvertedRestFrom(to.restUnit, to.rest)
            val convertedDuration = getConvertedDurationFrom(to.durationUnit, to.duration)

            _uiState.value = _uiState.value.copy(
                weight = _uiState.value.weight.copy(value = to.weight.formatToDecimal()),
                repetitions = _uiState.value.repetitions.copy(value = to.repetitions.toStringOrEmpty()),
                rest = _uiState.value.rest.copy(value = convertedRest),
                restUnit = _uiState.value.restUnit.copy(value = getUnitRestFrom(to.rest)),
                duration = _uiState.value.duration.copy(value = convertedDuration),
                durationUnit = _uiState.value.durationUnit.copy(value = getUnitDurationFrom(to.duration)),
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
                it.date!!.format(EnumDateTimePatterns.DATE)
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
                    value = getConvertedRestFrom(toExercise.unitRest, toExercise.rest)
                ),
                restUnit = _uiState.value.restUnit.copy(
                    value = getUnitRestFrom(toExercise.rest)
                ),
                duration = _uiState.value.duration.copy(
                    value = getConvertedDurationFrom(toExercise.unitDuration, toExercise.duration)
                ),
                durationUnit = _uiState.value.durationUnit.copy(
                    value = getUnitDurationFrom(toExercise.duration)
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

    private fun getUnitDurationFrom(duration: Long?): String {
        return duration?.bestChronoUnit()?.let { getLabelFromChronoUnit(it) } ?: ""
    }

    private fun getUnitRestFrom(rest: Long?): String {
        return rest?.bestChronoUnit()?.let { getLabelFromChronoUnit(it) } ?: ""
    }

    private fun getConvertedDurationFrom(unitDuration: ChronoUnit?, duration: Long?): String {
        return unitDuration?.let { duration?.millisTo(it) }.toStringOrEmpty()
    }

    private fun getConvertedRestFrom(unitRest: ChronoUnit?, rest: Long?): String {
        return unitRest?.let { rest?.millisTo(it) }.toStringOrEmpty()
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

    fun onVideoClick(filePath: String) {
        if (FileUtils.getFileExists(filePath)) {
            context.openVideoPlayer(filePath = filePath)
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
                // TODO - Chamar um reload?
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
}