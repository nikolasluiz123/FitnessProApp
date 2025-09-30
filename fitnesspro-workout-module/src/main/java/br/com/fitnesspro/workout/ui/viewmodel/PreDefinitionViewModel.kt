package br.com.fitnesspro.workout.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import br.com.android.ui.compose.components.dialog.message.showConfirmationDialog
import br.com.android.ui.compose.components.dialog.message.showErrorDialog
import br.com.android.ui.compose.components.fields.dropdown.getChronoUnitMenuItems
import br.com.android.ui.compose.components.fields.validation.FieldValidationError
import br.com.core.android.utils.extensions.getChronoUnitLabel
import br.com.core.android.utils.media.FileUtils
import br.com.core.android.utils.media.VideoUtils
import br.com.core.utils.extensions.bestChronoUnit
import br.com.core.utils.extensions.fromJsonNavParamToArgs
import br.com.core.utils.extensions.getStringFromConvertedChronoUnitValue
import br.com.core.utils.extensions.toStringOrEmpty
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.base.FitnessProStatefulViewModel
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
import br.com.fitnesspro.workout.usecase.exercise.video.InactivateVideoPreDefinitionUseCase
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
    private val inactivateVideoExercisePreDefinitionUseCase: InactivateVideoPreDefinitionUseCase,
    savedStateHandle: SavedStateHandle
): FitnessProStatefulViewModel() {

    private val _uiState: MutableStateFlow<PreDefinitionUIState> = MutableStateFlow(PreDefinitionUIState())
    val uiState get() = _uiState.asStateFlow()

    val jsonArgs: String? = savedStateHandle[preDefinitionScreenArguments]

    init {
        initialLoadUIState()
    }

    override fun initialLoadUIState() {
        val args = jsonArgs?.fromJsonNavParamToArgs(PreDefinitionScreenArgs::class.java)!!

        _uiState.value = _uiState.value.copy(
            title = getTitle(args.exercisePreDefinitionId),
            group = createPagedDialogListTextField(
                getCurrentState = { _uiState.value.group },
                updateState = { _uiState.value = _uiState.value.copy(group = it) },
                dialogTitle = context.getString(br.com.fitnesspro.common.R.string.register_academy_screen_title_select_academy),
                getDataListFlow = ::getListWorkoutGroupPreDefinitions,
                onDataListItemClick = { toGroup ->
                    _uiState.value = _uiState.value.copy(toWorkoutGroupPreDefinition = toGroup)
                },
            ),
            exercise = createPagedDialogListTextField(
                getCurrentState = { _uiState.value.exercise },
                updateState = { _uiState.value = _uiState.value.copy(exercise = it) },
                dialogTitle = context.getString(R.string.exercise_screen_exercise_dialog_list_title),
                getDataListFlow = ::getListExercisePreDefinitions,
                onDataListItemClick = { item ->
                    _uiState.value = _uiState.value.copy(
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
                            value = item.rest?.bestChronoUnit().getStringFromConvertedChronoUnitValue(item.rest),
                            errorMessage = ""
                        ),
                        unitRest = _uiState.value.unitRest.copy(
                            value = item.rest.getChronoUnitLabel(context),
                            errorMessage = ""
                        ),
                        duration = _uiState.value.duration.copy(
                            value = item.duration?.bestChronoUnit().getStringFromConvertedChronoUnitValue(item.duration),
                            errorMessage = ""
                        ),
                        unitDuration = _uiState.value.unitDuration.copy(
                            value = item.duration.getChronoUnitLabel(context),
                            errorMessage = ""
                        )
                    )
                },
                showTrailingIcon = args.grouped
            ),
            exerciseOrder = createIntValueTextFieldState(
                getCurrentState = { _uiState.value.exerciseOrder },
                onValueChange = { state, value ->
                    _uiState.value = _uiState.value.copy(
                        exerciseOrder = state,
                        toExercisePredefinition = _uiState.value.toExercisePredefinition.copy(
                            exerciseOrder = value
                        )
                    )
                }
            ),
            sets = createIntValueTextFieldState(
                getCurrentState = { _uiState.value.sets },
                onValueChange = { state, value ->
                    _uiState.value = _uiState.value.copy(
                        sets = state,
                        toExercisePredefinition = _uiState.value.toExercisePredefinition.copy(
                            sets = value
                        )
                    )
                }
            ),
            reps = createIntValueTextFieldState(
                getCurrentState = { _uiState.value.reps },
                onValueChange = { state, value ->
                    _uiState.value = _uiState.value.copy(
                        reps = state,
                        toExercisePredefinition = _uiState.value.toExercisePredefinition.copy(
                            repetitions = value
                        )
                    )
                }
            ),
            rest = createLongValueTextFieldState(
                getCurrentState = { _uiState.value.rest },
                onValueChange = { state, value ->
                    _uiState.value = _uiState.value.copy(
                        rest = state,
                        toExercisePredefinition = _uiState.value.toExercisePredefinition.copy(
                            rest = value
                        )
                    )
                }
            ),
            unitRest = createDropDownTextFieldState(
                items = ChronoUnit.entries.getChronoUnitMenuItems(context),
                getCurrentState = { _uiState.value.unitRest },
                updateState = { _uiState.value = _uiState.value.copy(unitRest = it) },
                onItemClick = {
                    _uiState.value = _uiState.value.copy(
                        toExercisePredefinition = _uiState.value.toExercisePredefinition.copy(
                            unitRest = it
                        )
                    )
                }
            ),
            duration = createLongValueTextFieldState(
                getCurrentState = { _uiState.value.duration },
                onValueChange = { state, value ->
                    _uiState.value = _uiState.value.copy(
                        duration = state,
                        toExercisePredefinition = _uiState.value.toExercisePredefinition.copy(
                            duration = value
                        )
                    )
                }
            ),
            unitDuration = createDropDownTextFieldState(
                items = ChronoUnit.entries.getChronoUnitMenuItems(context),
                getCurrentState = { _uiState.value.unitDuration },
                updateState = { _uiState.value = _uiState.value.copy(unitDuration = it) },
                onItemClick = {
                    _uiState.value = _uiState.value.copy(
                        toExercisePredefinition = _uiState.value.toExercisePredefinition.copy(
                            unitDuration = it
                        )
                    )
                }
            ),
            videoGalleryState = createVideoGalleryState(
                getCurrentState = { _uiState.value.videoGalleryState },
                updateState = { _uiState.value = _uiState.value.copy(videoGalleryState = it) },
                title = context.getString(R.string.pre_definition_screen_video_gallery_title)
            ),
            messageDialogState = createMessageDialogState(
                getCurrentState = { _uiState.value.messageDialogState },
                updateState = { _uiState.value = _uiState.value.copy(messageDialogState = it) }
            ),
            showGroupField = args.grouped,
            onToggleLoading = {
                _uiState.value = _uiState.value.copy(showLoading = _uiState.value.showLoading.not())
            }
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

    override fun onError(throwable: Throwable) {
        super.onError(throwable)

        if (_uiState.value.showLoading) {
            _uiState.value.onToggleLoading()
        }
    }

    fun loadStateWithDatabaseInfos() {
        launch {
            val args = jsonArgs?.fromJsonNavParamToArgs(PreDefinitionScreenArgs::class.java)!!

            loadAuthenticatedPerson()
            loadEditionData(args.exercisePreDefinitionId)
            loadPreDefinitionList()

            _uiState.value.executeLoad = false
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
            val toExercisePreDef = exercisePreDefinitionRepository.findTOExercisePreDefinitionById(preDefId)!!
            val toWorkoutPreDef = toExercisePreDef.workoutGroupPreDefinitionId?.let {
                exercisePreDefinitionRepository.findTOWorkoutGroupPreDefinitionById(it)
            }

            val convertedRest =  toExercisePreDef.unitRest.getStringFromConvertedChronoUnitValue(toExercisePreDef.rest)
            val convertedDuration = toExercisePreDef.unitDuration.getStringFromConvertedChronoUnitValue(toExercisePreDef.duration)

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
                    value = toExercisePreDef.rest.getChronoUnitLabel(context)
                ),
                duration = _uiState.value.duration.copy(
                    value = convertedDuration
                ),
                unitDuration = _uiState.value.unitDuration.copy(
                    value = toExercisePreDef.duration.getChronoUnitLabel(context)
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

    private fun getTitle(exercisePreDefinitionId: String?): String {
        return if (exercisePreDefinitionId == null) {
            context.getString(br.com.fitnesspro.common.R.string.label_new_pre_definition)
        } else {
            context.getString(br.com.fitnesspro.common.R.string.label_edit_pre_definition)
        }
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

    fun onVideoClick(filePath: String, onOpenVideo: (String) -> Unit) {
        if (FileUtils.getFileExists(filePath)) {
            onOpenVideo(filePath)
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


    fun onDeleteVideo(filePath: String, onSuccess: () -> Unit) {
        _uiState.value.messageDialogState.onShowDialog?.showConfirmationDialog(
            message = context.getString(R.string.msg_delete_video_confirmation),
            onConfirm = {
                _uiState.value.onToggleLoading()

                launch {
                    inactivateVideoExercisePreDefinitionUseCase(listOf(filePath))
                    loadVideos(_uiState.value.toExercisePredefinition.id)
                    onSuccess()
                    _uiState.value.onToggleLoading()
                }
            }
        )
    }
}