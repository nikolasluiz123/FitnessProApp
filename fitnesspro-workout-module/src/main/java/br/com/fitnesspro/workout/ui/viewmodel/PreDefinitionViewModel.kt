package br.com.fitnesspro.workout.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.FitnessProViewModel
import br.com.fitnesspro.compose.components.fields.menu.MenuItem
import br.com.fitnesspro.compose.components.fields.menu.getLabelOrEmptyIfNullValue
import br.com.fitnesspro.compose.components.fields.state.DropDownTextField
import br.com.fitnesspro.compose.components.fields.state.PagedDialogListState
import br.com.fitnesspro.compose.components.fields.state.PagedDialogListTextField
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.compose.components.gallery.video.state.VideoGalleryState
import br.com.fitnesspro.core.callback.showConfirmationDialog
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.extensions.bestChronoUnit
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.core.extensions.millisTo
import br.com.fitnesspro.core.extensions.openVideoPlayer
import br.com.fitnesspro.core.extensions.toIntOrNull
import br.com.fitnesspro.core.extensions.toStringOrEmpty
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.core.utils.FileUtils
import br.com.fitnesspro.core.utils.VideoUtils
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.to.TOExercisePreDefinition
import br.com.fitnesspro.to.TOWorkoutGroupPreDefinition
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.repository.ExercisePreDefinitionRepository
import br.com.fitnesspro.workout.repository.VideoRepository
import br.com.fitnesspro.workout.ui.navigation.PreDefinitionScreenArgs
import br.com.fitnesspro.workout.ui.navigation.preDefinitionScreenArguments
import br.com.fitnesspro.workout.ui.state.PreDefinitionUIState
import br.com.fitnesspro.workout.usecase.exercise.InactivateExercisePreDefinitionUseCase
import br.com.fitnesspro.workout.usecase.exercise.SaveExercisePreDefinitionUseCase
import br.com.fitnesspro.workout.usecase.exercise.SaveGroupPreDefinitionUseCase
import br.com.fitnesspro.workout.usecase.exercise.enums.EnumValidatedExercisePreDefinitionFields
import br.com.fitnesspro.workout.usecase.exercise.video.SaveVideoPreDefinitionUseCase
import br.com.fitnesspro.workout.usecase.exercise.video.gallery.SaveVideoExercisePreDefinitionFromGalleryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import java.io.File
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class PreDefinitionViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val globalEvents: GlobalEvents,
    private val videoRepository: VideoRepository,
    private val exercisePreDefinitionRepository: ExercisePreDefinitionRepository,
    private val personRepository: PersonRepository,
    private val saveGroupPreDefinitionUseCase: SaveGroupPreDefinitionUseCase,
    private val saveExercisePreDefinitionUseCase: SaveExercisePreDefinitionUseCase,
    private val saveVideoExercisePreDefinitionFromGalleryUseCase: SaveVideoExercisePreDefinitionFromGalleryUseCase,
    private val saveVideoPreDefinitionUseCase: SaveVideoPreDefinitionUseCase,
    private val inactivateExercisePreDefinitionUseCase: InactivateExercisePreDefinitionUseCase,
    savedStateHandle: SavedStateHandle
): FitnessProViewModel() {

    private val _uiState: MutableStateFlow<PreDefinitionUIState> =
        MutableStateFlow(PreDefinitionUIState())
    val uiState get() = _uiState.asStateFlow()

    val jsonArgs: String? = savedStateHandle[preDefinitionScreenArguments]

    init {
        val args = jsonArgs?.fromJsonNavParamToArgs(PreDefinitionScreenArgs::class.java)!!

        initialLoadUIState(args)
        loadStateWithDatabaseInfos(args)
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

    override fun onError(throwable: Throwable) {
        super.onError(throwable)

        if (_uiState.value.showLoading) {
            _uiState.value.onToggleLoading()
        }
    }

    private fun initialLoadUIState(args: PreDefinitionScreenArgs) {
        _uiState.value = _uiState.value.copy(
            title = getTitle(args.exercisePreDefinitionId),
            group = initializePagedDialogListTextFieldGroup(),
            exercise = initializePagedDialogListTextFieldExercise(args.grouped),
            exerciseOrder = initializeTextFieldExerciseOrder(),
            sets = initializeTextFieldSets(),
            reps = initializeTextFieldReps(),
            rest = initializeTextFieldRest(),
            unitRest = initializeDropDownTextFieldUnitRest(),
            duration = initializeTextFieldDuration(),
            unitDuration = initializeDropDownTextFieldUnitDuration(),
            videoGalleryState = initializeVideoGalleryState(),
            messageDialogState = initializeMessageDialogState(),
            showGroupField = args.grouped,
            onToggleLoading = {
                _uiState.value = _uiState.value.copy(showLoading = _uiState.value.showLoading.not())
            }

        )
    }

    private fun loadStateWithDatabaseInfos(args: PreDefinitionScreenArgs) {
        launch {
            loadAuthenticatedPerson()
            loadEditionData(args.exercisePreDefinitionId)
            loadPreDefinitionList()
        }
    }

    private fun loadPreDefinitionList() {
        if (_uiState.value.showGroupField) {
            _uiState.value = _uiState.value.copy(
                exercise = _uiState.value.exercise.copy(
                    dialogListState = _uiState.value.exercise.dialogListState.copy(
                        dataList = getListExercisePreDefinitions()
                    )
                ),
                group = _uiState.value.group.copy(
                    dialogListState = _uiState.value.group.dialogListState.copy(
                        dataList = getListWorkoutGroupPreDefinitions()
                    )
                )
            )
        }
    }

    private suspend fun loadAuthenticatedPerson() {
        val personId = personRepository.getAuthenticatedTOPerson()?.id

        _uiState.value = _uiState.value.copy(
            authenticatedPersonId = personId
        )
    }

    private suspend fun loadEditionData(exercisePreDefinitionId: String?) {
        exercisePreDefinitionId?.let { preDefId ->
            val toExercisePreDef =
                exercisePreDefinitionRepository.findTOExercisePreDefinitionById(preDefId)!!
            val toWorkoutPreDef = toExercisePreDef.workoutGroupPreDefinitionId?.let {
                exercisePreDefinitionRepository.findTOWorkoutGroupPreDefinitionById(it)
            }

            val convertedRest =
                getConvertedRestFrom(toExercisePreDef.unitRest, toExercisePreDef.rest)
            val convertedDuration =
                getConvertedDurationFrom(toExercisePreDef.unitDuration, toExercisePreDef.duration)

            _uiState.value = _uiState.value.copy(
                title = getTitle(toExercisePreDef.id),
                subtitle = toExercisePreDef.name,
                exercise = _uiState.value.exercise.copy(
                    value = toExercisePreDef.name!!
                ),
                sets = _uiState.value.sets.copy(
                    value = toExercisePreDef.sets.toStringOrEmpty()
                ),
                reps = _uiState.value.reps.copy(
                    value = toExercisePreDef.repetitions.toStringOrEmpty()
                ),
                rest = _uiState.value.rest.copy(
                    value = convertedRest
                ),
                unitRest = _uiState.value.unitRest.copy(
                    value = getUnitRestFrom(toExercisePreDef.rest)
                ),
                duration = _uiState.value.duration.copy(
                    value = convertedDuration
                ),
                unitDuration = _uiState.value.unitDuration.copy(
                    value = getUnitDurationFrom(toExercisePreDef.duration)
                ),
                toExercisePredefinition = toExercisePreDef,
                inactivateEnabled = true,
            )

            if (toWorkoutPreDef != null) {
                _uiState.value = _uiState.value.copy(
                    toWorkoutGroupPreDefinition = toWorkoutPreDef
                )
            }

            loadVideos(toExercisePreDef.id)
        }
    }

    private fun getConvertedDurationFrom(unitDuration: ChronoUnit?, duration: Long?): String {
        return unitDuration?.let { duration?.millisTo(it) }.toStringOrEmpty()
    }

    private fun getConvertedRestFrom(unitRest: ChronoUnit?, rest: Long?): String {
        return unitRest?.let { rest?.millisTo(it) }.toStringOrEmpty()
    }

    private fun getTitle(exercisePreDefinitionId: String?): String {
        return if (exercisePreDefinitionId == null) {
            context.getString(br.com.fitnesspro.common.R.string.label_new_pre_definition)
        } else {
            context.getString(br.com.fitnesspro.common.R.string.label_edit_pre_definition)
        }
    }

    private fun initializePagedDialogListTextFieldGroup(): PagedDialogListTextField<TOWorkoutGroupPreDefinition> {
        return PagedDialogListTextField(
            dialogListState = PagedDialogListState(
                dialogTitle = context.getString(R.string.exercise_pre_definition_screen_group_dialog_list_title),
                onShow = {
                    _uiState.value = _uiState.value.copy(
                        group = _uiState.value.group.copy(
                            dialogListState = _uiState.value.group.dialogListState.copy(show = true)
                        )
                    )
                },
                onHide = {
                    _uiState.value = _uiState.value.copy(
                        group = _uiState.value.group.copy(
                            dialogListState = _uiState.value.group.dialogListState.copy(show = false)
                        )
                    )
                },
                onDataListItemClick = { item ->
                    _uiState.value = _uiState.value.copy(
                        group = _uiState.value.group.copy(
                            value = item.getLabel(),
                            errorMessage = ""
                        ),
                        toWorkoutGroupPreDefinition = item
                    )

                    _uiState.value.group.dialogListState.onHide()
                },
                onSimpleFilterChange = { filter ->
                    _uiState.value = _uiState.value.copy(
                        group = _uiState.value.group.copy(
                            dialogListState = _uiState.value.group.dialogListState.copy(
                                dataList = getListWorkoutGroupPreDefinitions(simpleFilter = filter)
                            )
                        )
                    )
                },
            ),
            onChange = { newText ->
                _uiState.value = _uiState.value.copy(
                    group = _uiState.value.group.copy(
                        value = newText,
                        errorMessage = ""
                    ),
                    toWorkoutGroupPreDefinition = _uiState.value.toWorkoutGroupPreDefinition.copy(
                        name = newText
                    )
                )
            },
        )
    }

    private fun initializePagedDialogListTextFieldExercise(grouped: Boolean): PagedDialogListTextField<TOExercisePreDefinition> {
        return PagedDialogListTextField(
            dialogListState = PagedDialogListState(
                showTrailingIcon = grouped,
                dialogTitle = context.getString(R.string.exercise_screen_exercise_dialog_list_title),
                onShow = {
                    _uiState.value = _uiState.value.copy(
                        exercise = _uiState.value.exercise.copy(
                            dialogListState = _uiState.value.exercise.dialogListState.copy(show = true)
                        )
                    )
                },
                onHide = {
                    _uiState.value = _uiState.value.copy(
                        exercise = _uiState.value.exercise.copy(
                            dialogListState = _uiState.value.exercise.dialogListState.copy(show = false)
                        )
                    )
                },
                onDataListItemClick = { item ->
                    _uiState.value = _uiState.value.copy(
                        exercise = _uiState.value.exercise.copy(
                            value = item.getLabel(),
                            errorMessage = ""
                        ),
                        toExercisePredefinition = _uiState.value.toExercisePredefinition.copy(
                            name = item.name,
                            sets = item.sets,
                            repetitions = item.repetitions,
                            rest = item.rest,
                            unitRest = item.rest?.bestChronoUnit(),
                            duration = item.duration,
                            unitDuration = item.duration?.bestChronoUnit()
                        ),
                        sets = _uiState.value.sets.copy(
                            value = item.sets.toStringOrEmpty(),
                            errorMessage = ""
                        ),
                        reps = _uiState.value.reps.copy(
                            value = item.repetitions.toStringOrEmpty(),
                            errorMessage = ""
                        ),
                        rest = _uiState.value.rest.copy(
                            value = getConvertedRestFrom(item.rest?.bestChronoUnit(), item.rest),
                            errorMessage = ""
                        ),
                        unitRest = _uiState.value.unitRest.copy(
                            value = getUnitRestFrom(item.rest),
                            errorMessage = ""
                        ),
                        duration = _uiState.value.duration.copy(
                            value = getConvertedDurationFrom(
                                item.duration?.bestChronoUnit(),
                                item.duration
                            ),
                            errorMessage = ""
                        ),
                        unitDuration = _uiState.value.unitDuration.copy(
                            value = getUnitDurationFrom(item.duration),
                            errorMessage = ""
                        )
                    )

                    _uiState.value.exercise.dialogListState.onHide()
                },
                onSimpleFilterChange = { filter ->
                    _uiState.value = _uiState.value.copy(
                        exercise = _uiState.value.exercise.copy(
                            dialogListState = _uiState.value.exercise.dialogListState.copy(
                                dataList = getListExercisePreDefinitions(simpleFilter = filter)
                            )
                        )
                    )
                },
            ),
            onChange = { newText ->
                _uiState.value = _uiState.value.copy(
                    exercise = _uiState.value.exercise.copy(
                        value = newText,
                        errorMessage = ""
                    ),
                    toExercisePredefinition = _uiState.value.toExercisePredefinition.copy(
                        name = newText
                    )
                )
            },
        )
    }

    private fun getListExercisePreDefinitions(simpleFilter: String = ""): Flow<PagingData<TOExercisePreDefinition>> {
        return _uiState.value.authenticatedPersonId?.let { authenticatedPersonId ->
            exercisePreDefinitionRepository.getListExercisePreDefinitions(
                authenticatedPersonId,
                simpleFilter
            ).flow
        } ?: emptyFlow()
    }

    private fun getListWorkoutGroupPreDefinitions(simpleFilter: String = ""): Flow<PagingData<TOWorkoutGroupPreDefinition>> {
        return _uiState.value.authenticatedPersonId?.let { authenticatedPersonId ->
            exercisePreDefinitionRepository.getListWorkoutGroupPreDefinitions(
                authenticatedPersonId,
                simpleFilter
            ).flow
        } ?: emptyFlow()
    }

    private fun initializeTextFieldExerciseOrder(): TextField {
        return TextField(
            onChange = {
                if (it.isDigitsOnly()) {
                    _uiState.value = _uiState.value.copy(
                        exerciseOrder = _uiState.value.exerciseOrder.copy(
                            value = it,
                            errorMessage = ""
                        ),
                        toExercisePredefinition = _uiState.value.toExercisePredefinition.copy(
                            exerciseOrder = it.toIntOrNull()
                        )
                    )
                }
            }
        )
    }

    private fun initializeTextFieldSets(): TextField {
        return TextField(
            onChange = {
                _uiState.value = _uiState.value.copy(
                    sets = _uiState.value.sets.copy(
                        value = it,
                        errorMessage = ""
                    ),
                    toExercisePredefinition = _uiState.value.toExercisePredefinition.copy(sets = it.toIntOrNull())
                )
            }
        )
    }

    private fun initializeTextFieldReps(): TextField {
        return TextField(
            onChange = {
                _uiState.value = _uiState.value.copy(
                    reps = _uiState.value.reps.copy(
                        value = it,
                        errorMessage = ""
                    ),
                    toExercisePredefinition = _uiState.value.toExercisePredefinition.copy(
                        repetitions = it.toIntOrNull()
                    )
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
                    toExercisePredefinition = _uiState.value.toExercisePredefinition.copy(rest = it.toLongOrNull())
                )
            }
        )
    }

    private fun initializeDropDownTextFieldUnitRest(): DropDownTextField<ChronoUnit?> {
        val items = getChronoUnitMenuItems()

        return DropDownTextField(
            dataList = items,
            dataListFiltered = items,
            onDropDownDismissRequest = {
                _uiState.value = _uiState.value.copy(
                    unitRest = _uiState.value.unitRest.copy(expanded = false)
                )
            },
            onDropDownExpandedChange = {
                _uiState.value = _uiState.value.copy(
                    unitRest = _uiState.value.unitRest.copy(expanded = it)
                )
            },
            onDataListItemClick = {
                _uiState.value = _uiState.value.copy(
                    unitRest = _uiState.value.unitRest.copy(
                        value = it.getLabelOrEmptyIfNullValue(),
                        errorMessage = ""
                    ),
                    toExercisePredefinition = _uiState.value.toExercisePredefinition.copy(
                        unitRest = it.value
                    )
                )

                _uiState.value.unitRest.onDropDownDismissRequest()
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
                    toExercisePredefinition = _uiState.value.toExercisePredefinition.copy(duration = it.toLongOrNull())
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
                    unitDuration = _uiState.value.unitDuration.copy(expanded = false)
                )
            },
            onDropDownExpandedChange = {
                _uiState.value = _uiState.value.copy(
                    unitDuration = _uiState.value.unitDuration.copy(expanded = it)
                )
            },
            onDataListItemClick = {
                _uiState.value = _uiState.value.copy(
                    unitDuration = _uiState.value.unitDuration.copy(
                        value = it.getLabelOrEmptyIfNullValue(),
                        errorMessage = ""
                    ),
                    toExercisePredefinition = _uiState.value.toExercisePredefinition.copy(
                        unitDuration = it.value
                    )
                )

                _uiState.value.unitDuration.onDropDownDismissRequest()
            }
        )
    }

    private fun initializeVideoGalleryState(): VideoGalleryState {
        return VideoGalleryState(
            title = context.getString(R.string.pre_definition_screen_video_gallery_title),
            isScrollEnabled = true,
            onViewModeChange = {
                _uiState.value = _uiState.value.copy(
                    videoGalleryState = _uiState.value.videoGalleryState.copy(
                        viewMode = it
                    )
                )
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

    private fun getChronoUnitMenuItems(): List<MenuItem<ChronoUnit?>> {
        val units = ChronoUnit.entries.slice(ChronoUnit.SECONDS.ordinal..ChronoUnit.HOURS.ordinal)

        return units.map { unit ->
            MenuItem(
                label = getLabelFromChronoUnit(unit),
                value = unit
            )
        }
    }

    private fun getUnitDurationFrom(duration: Long?): String {
        return duration?.bestChronoUnit()?.let { getLabelFromChronoUnit(it) } ?: ""
    }

    private fun getUnitRestFrom(rest: Long?): String {
        return rest?.bestChronoUnit()?.let { getLabelFromChronoUnit(it) } ?: ""
    }

    private fun getLabelFromChronoUnit(unit: ChronoUnit): String {
        return when (unit) {
            ChronoUnit.SECONDS -> context.getString(R.string.exercise_screen_chrono_unit_seconds)
            ChronoUnit.MINUTES -> context.getString(R.string.exercise_screen_chrono_unit_minutes)
            ChronoUnit.HOURS -> context.getString(R.string.exercise_screen_chrono_unit_hours)
            else -> throw IllegalArgumentException("Valor invalido para recuperar um label de ChronoUnit")
        }
    }

    fun onOpenCameraVideo(file: File) {
        _uiState.value.notSavedVideoFiles.add(file)
    }

    fun onFinishVideoRecording(onSuccess: () -> Unit) {
        launch {
            val id = _uiState.value.toExercisePredefinition.id

            if (id != null) {
                onFinishVideoRecordingEdition(id, onSuccess)
            } else {
                onFinishVideoRecordingCreation()
            }
        }
    }

    private suspend fun onFinishVideoRecordingEdition(
        exercisePreDefinitionId: String,
        onSuccess: () -> Unit
    ) {
        val validationResult = saveVideoPreDefinitionUseCase(
            exercisePreDefinitionId = exercisePreDefinitionId,
            videoFile = _uiState.value.notSavedVideoFiles.first()
        )

        if (validationResult != null) {
            onShowErrorDialog(validationResult.message)
        } else {
            onSuccess()
            loadVideos(exercisePreDefinitionId)
            _uiState.value.notSavedVideoFiles.clear()
        }
    }

    private suspend fun loadVideos(exercisePreDefinitionId: String?) {
        exercisePreDefinitionId?.let { id ->
            val videoFilePaths = videoRepository.getVideoExercisePreDefinition(id)

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

    private suspend fun onFinishVideoRecordingCreation() {
        saveVideoPreDefinitionUseCase.validateVideosLimit(videoCount = _uiState.value.notSavedVideoFiles.size)
        saveVideoPreDefinitionUseCase.validateVideoFile(videoFile = _uiState.value.notSavedVideoFiles.last())
    }

    fun onVideoSelectedOnGallery(uri: Uri, onSuccess: () -> Unit) {
        launch {
            val id = _uiState.value.toExercisePredefinition.id

            if (id != null) {
                onVideoSelectedOnGalleryEdition(id, uri, onSuccess)
            } else {
                onVideoSelectedOnGalleryCreation(uri)
            }
        }
    }

    private suspend fun onVideoSelectedOnGalleryEdition(
        exercisePreDefinitionId: String,
        uri: Uri,
        onSuccess: () -> Unit
    ) {
        val validationResult = saveVideoExercisePreDefinitionFromGalleryUseCase(
            exercisePreDefinitionId = exercisePreDefinitionId,
            uri = uri
        )

        if (validationResult != null) {
            onShowErrorDialog(validationResult.message)
        } else {
            onSuccess()
            loadVideos(exercisePreDefinitionId)
        }
    }

    private suspend fun onVideoSelectedOnGalleryCreation(uri: Uri) {
        VideoUtils.createVideoFileFromUri(context, uri)?.let { file ->
            _uiState.value.notSavedVideoFiles.add(file)
            saveVideoPreDefinitionUseCase.validateVideosLimit(videoCount = _uiState.value.notSavedVideoFiles.size)
            saveVideoPreDefinitionUseCase.validateVideoFile(videoFile = file)
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

    fun onSavePreDefinition(onSuccess: () -> Unit) {
        launch {
            val validationResult = if (_uiState.value.showGroupField) {
                saveGroupPreDefinitionUseCase(
                    toWorkoutGroupPreDefinition = _uiState.value.toWorkoutGroupPreDefinition,
                    toExercisePreDefinition = _uiState.value.toExercisePredefinition,
                    videoFiles = _uiState.value.notSavedVideoFiles
                )
            } else {
                saveExercisePreDefinitionUseCase(
                    toExercisePreDefinition = _uiState.value.toExercisePredefinition,
                    videoFiles = _uiState.value.notSavedVideoFiles
                )
            }

            if (validationResult.isEmpty()) {
                onSuccess()
                loadEditionData(_uiState.value.toExercisePredefinition.id!!)
            } else {
                _uiState.value.onToggleLoading()
                showValidationMessages(validationResult)
            }
        }
    }

    private fun showValidationMessages(validationResult: List<FieldValidationError<EnumValidatedExercisePreDefinitionFields>>) {
        validationResult.forEach {
            when (it.field!!) {
                EnumValidatedExercisePreDefinitionFields.GROUP -> {
                    _uiState.value = _uiState.value.copy(
                        group = _uiState.value.group.copy(errorMessage = it.message)
                    )
                }

                EnumValidatedExercisePreDefinitionFields.EXERCISE -> {
                    _uiState.value = _uiState.value.copy(
                        exercise = _uiState.value.exercise.copy(errorMessage = it.message)
                    )
                }

                EnumValidatedExercisePreDefinitionFields.DURATION -> {
                    _uiState.value = _uiState.value.copy(
                        duration = _uiState.value.duration.copy(errorMessage = it.message)
                    )
                }

                EnumValidatedExercisePreDefinitionFields.UNIT_DURATION -> {
                    _uiState.value = _uiState.value.copy(
                        unitDuration = _uiState.value.unitDuration.copy(errorMessage = it.message)
                    )
                }

                EnumValidatedExercisePreDefinitionFields.REST -> {
                    _uiState.value = _uiState.value.copy(
                        rest = _uiState.value.rest.copy(errorMessage = it.message)
                    )
                }

                EnumValidatedExercisePreDefinitionFields.UNIT_REST -> {
                    _uiState.value = _uiState.value.copy(
                        unitRest = _uiState.value.unitRest.copy(errorMessage = it.message)
                    )
                }
            }
        }
    }

    fun onInactivate(onSuccess: () -> Unit) {
        _uiState.value.toExercisePredefinition.id?.let { exercisePreDefinitionId ->
            _uiState.value.messageDialogState.onShowDialog?.showConfirmationDialog(
                message = context.getString(R.string.exercise_pre_definition_screen_msg_confirm_inactivate_exercise_pre_definition),
                onConfirm = {
                    _uiState.value.onToggleLoading()

                    launch {
                        inactivateExercisePreDefinitionUseCase(exercisePreDefinitionId)
                        onSuccess()
                    }
                }
            )
        }
    }
}