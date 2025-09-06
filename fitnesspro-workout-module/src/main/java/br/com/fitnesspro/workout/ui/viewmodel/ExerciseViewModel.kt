package br.com.fitnesspro.workout.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.base.FitnessProStatefulViewModel
import br.com.fitnesspro.compose.components.fields.menu.getChronoUnitMenuItems
import br.com.fitnesspro.compose.components.tabs.Tab
import br.com.fitnesspro.core.callback.showConfirmationDialog
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.core.extensions.getChronoUnitLabel
import br.com.fitnesspro.core.extensions.getFirstPartFullDisplayName
import br.com.fitnesspro.core.extensions.getStringFromConvertedChronoUnitValue
import br.com.fitnesspro.core.extensions.toStringOrEmpty
import br.com.fitnesspro.core.utils.FileUtils
import br.com.fitnesspro.core.utils.VideoUtils
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.to.TOWorkoutGroup
import br.com.fitnesspro.workout.R
import br.com.fitnesspro.workout.repository.ExercisePreDefinitionRepository
import br.com.fitnesspro.workout.repository.ExerciseRepository
import br.com.fitnesspro.workout.repository.VideoRepository
import br.com.fitnesspro.workout.repository.WorkoutGroupRepository
import br.com.fitnesspro.workout.repository.WorkoutRepository
import br.com.fitnesspro.workout.ui.navigation.ExerciseScreenArgs
import br.com.fitnesspro.workout.ui.navigation.exerciseScreenArguments
import br.com.fitnesspro.workout.ui.screen.exercise.enums.EnumTabsExerciseScreen
import br.com.fitnesspro.workout.ui.state.ExerciseUIState
import br.com.fitnesspro.workout.usecase.exercise.InactivateExerciseUseCase
import br.com.fitnesspro.workout.usecase.exercise.SaveExerciseUseCase
import br.com.fitnesspro.workout.usecase.exercise.enums.EnumValidatedExerciseFields
import br.com.fitnesspro.workout.usecase.exercise.video.InactivateVideoExerciseUseCase
import br.com.fitnesspro.workout.usecase.exercise.video.SaveExerciseVideoUseCase
import br.com.fitnesspro.workout.usecase.exercise.video.gallery.SaveExerciseVideoFromGalleryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val globalEvents: GlobalEvents,
    private val exerciseRepository: ExerciseRepository,
    private val personRepository: PersonRepository,
    private val exercisePreDefinitionRepository: ExercisePreDefinitionRepository,
    private val workoutGroupRepository: WorkoutGroupRepository,
    private val workoutRepository: WorkoutRepository,
    private val videoRepository: VideoRepository,
    private val saveExerciseUseCase: SaveExerciseUseCase,
    private val saveExerciseVideoUseCase: SaveExerciseVideoUseCase,
    private val saveExerciseVideoFromGalleryUseCase: SaveExerciseVideoFromGalleryUseCase,
    private val inactivateExerciseUseCase: InactivateExerciseUseCase,
    private val inactivateVideoExerciseUseCase: InactivateVideoExerciseUseCase,
    savedStateHandle: SavedStateHandle
): FitnessProStatefulViewModel() {

    private val _uiState: MutableStateFlow<ExerciseUIState> = MutableStateFlow(ExerciseUIState())
    val uiState get() = _uiState.asStateFlow()

    private val jsonArgs: String? = savedStateHandle[exerciseScreenArguments]

    init {
        initialLoadUIState()
    }

    override fun initialLoadUIState() {
        val args = jsonArgs?.fromJsonNavParamToArgs(ExerciseScreenArgs::class.java)!!

        _uiState.value = _uiState.value.copy(
            group = createDialogListTextField(
                getCurrentState = { _uiState.value.group },
                updateState = { _uiState.value = _uiState.value.copy(group = it) },
                dialogTitle = context.getString(R.string.exercise_screen_group_dialog_list_title),
                getDataList = { getListWorkoutGroups(it) },
                onChange = {
                    _uiState.value = _uiState.value.copy(
                        toExercise = _uiState.value.toExercise.copy(workoutGroupName = it)
                    )
                },
                onDataListItemClick = {
                    _uiState.value = _uiState.value.copy(
                        toExercise = _uiState.value.toExercise.copy(
                            workoutGroupId = it.id,
                            workoutGroupName = it.name
                        )
                    )
                }
            ),
            groupOrder = createIntValueTextFieldState(
                getCurrentState = { _uiState.value.groupOrder },
                onValueChange = { state, value ->
                    _uiState.value = _uiState.value.copy(
                        groupOrder = state,
                        toExercise = _uiState.value.toExercise.copy(groupOrder = value)
                    )
                },
            ),
            exercise = createPagedDialogListTextField(
                getCurrentState = { _uiState.value.exercise },
                updateState = { _uiState.value = _uiState.value.copy(exercise = it) },
                dialogTitle = context.getString(R.string.exercise_screen_exercise_dialog_list_title),
                getDataListFlow = ::getListExercisesAndPreDefinitions,
                onDataListItemClick = {
                    _uiState.value = _uiState.value.copy(
                        toExercise = _uiState.value.toExercise.copy(
                            name = it.name,
                            preDefinition = it.preDefinition,
                            sets = it.sets,
                            repetitions = it.repetitions,
                            rest = it.rest,
                            unitRest = it.unitRest,
                            duration = it.duration,
                            unitDuration = it.unitDuration,
                        )
                    )

                    if (it.preDefinition) {
                        _uiState.value = _uiState.value.copy(
                            sets = _uiState.value.sets.copy(value = it.sets.toStringOrEmpty()),
                            reps = _uiState.value.reps.copy(value = it.repetitions.toStringOrEmpty()),
                            rest = _uiState.value.rest.copy(value = it.unitRest.getStringFromConvertedChronoUnitValue(it.rest)),
                            unitRest = _uiState.value.unitRest.copy(value = it.rest.getChronoUnitLabel(context)),
                            duration = _uiState.value.duration.copy(value = it.unitDuration.getStringFromConvertedChronoUnitValue(it.duration)),
                            unitDuration = _uiState.value.unitDuration.copy(value = it.duration.getChronoUnitLabel(context)),
                        )
                    }
                }
            ),
            exerciseOrder = createIntValueTextFieldState(
                getCurrentState = { _uiState.value.exerciseOrder },
                onValueChange = { state, value ->
                    _uiState.value = _uiState.value.copy(
                        exerciseOrder = state,
                        toExercise = _uiState.value.toExercise.copy(exerciseOrder = value)
                    )
                },
            ),
            sets = createIntValueTextFieldState(
                getCurrentState = { _uiState.value.sets },
                onValueChange = { state, value ->
                    _uiState.value = _uiState.value.copy(
                        sets = state,
                        toExercise = _uiState.value.toExercise.copy(sets = value)
                    )
                },
            ),
            reps = createIntValueTextFieldState(
                getCurrentState = { _uiState.value.reps },
                onValueChange = { state, value ->
                    _uiState.value = _uiState.value.copy(
                        reps = state,
                        toExercise = _uiState.value.toExercise.copy(repetitions = value)
                    )
                },
            ),
            rest = createLongValueTextFieldState(
                getCurrentState = { _uiState.value.rest },
                onValueChange = { state, value ->
                    _uiState.value = _uiState.value.copy(
                        rest = state,
                        toExercise = _uiState.value.toExercise.copy(rest = value)
                    )
                },
            ),
            unitRest = createDropDownTextFieldState(
                items = ChronoUnit.entries.getChronoUnitMenuItems(context),
                getCurrentState = { _uiState.value.unitRest },
                updateState = { _uiState.value = _uiState.value.copy(unitRest = it) },
                onItemClick = {
                    _uiState.value = _uiState.value.copy(
                        toExercise = _uiState.value.toExercise.copy(unitRest = it)
                    )
                }
            ),
            duration = createLongValueTextFieldState(
                getCurrentState = { _uiState.value.duration },
                onValueChange = { state, value ->
                    _uiState.value = _uiState.value.copy(
                        duration = state,
                        toExercise = _uiState.value.toExercise.copy(duration = value)
                    )
                },
            ),
            unitDuration = createDropDownTextFieldState(
                items = ChronoUnit.entries.getChronoUnitMenuItems(context),
                getCurrentState = { _uiState.value.unitDuration },
                updateState = { _uiState.value = _uiState.value.copy(unitDuration = it) },
                onItemClick = {
                    _uiState.value = _uiState.value.copy(
                        toExercise = _uiState.value.toExercise.copy(unitDuration = it)
                    )
                }
            ),
            observation = createTextFieldState(
                getCurrentState = { _uiState.value.observation },
                updateState = {
                    _uiState.value = _uiState.value.copy(
                        observation = it,
                        toExercise = _uiState.value.toExercise.copy(observation = it.value)
                    )
                },
            ),
            videoGalleryState = createVideoGalleryState(
                title = context.getString(R.string.exercise_screen_video_gallery_title),
                isScrollEnabled = false,
                getCurrentState = { _uiState.value.videoGalleryState },
                updateState = { _uiState.value = _uiState.value.copy(videoGalleryState = it) },
            ),
            tabState = createTabState(
                getCurrentState = { _uiState.value.tabState },
                updateState = { _uiState.value = _uiState.value.copy(tabState = it) },
                tabs = getTabsWithDefaultState()
            ),
            messageDialogState = createMessageDialogState(
                getCurrentState = { _uiState.value.messageDialogState },
                updateState = { _uiState.value = _uiState.value.copy(messageDialogState = it) },
            ),
            toExercise = _uiState.value.toExercise.copy(
                workoutId = args.workoutId,
                dayWeek = args.dayWeek
            ),
            onToggleLoading = {
                _uiState.value = _uiState.value.copy(showLoading = _uiState.value.showLoading.not())
            }
        )
    }

    override fun getGlobalEventsBus(): GlobalEvents = globalEvents

    override fun getErrorMessageFrom(throwable: Throwable): String {
        return context.getString(br.com.fitnesspro.common.R.string.unknown_error_message)
    }

    override fun onShowErrorDialog(message: String) {
        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(message = message)
    }

    override fun onError(throwable: Throwable) {
        super.onError(throwable)

        if (_uiState.value.showLoading) {
            _uiState.value.onToggleLoading()
        }
    }

    private fun getTabsWithDefaultState(): MutableList<Tab> {
        return mutableListOf(
            Tab(
                enum = EnumTabsExerciseScreen.GENERAL,
                selected = true,
                enabled = true
            ),
            Tab(
                enum = EnumTabsExerciseScreen.VIDEOS,
                selected = false,
                enabled = false
            )
        )
    }

    private fun getTabListAllEnabled(): MutableList<Tab> {
        return _uiState.value.tabState.tabs.map { tab ->
            tab.copy(enabled = true)
        }.toMutableList()
    }

    private suspend fun getListWorkoutGroups(simpleFilter: String): List<TOWorkoutGroup> {
        val args = jsonArgs?.fromJsonNavParamToArgs(ExerciseScreenArgs::class.java)!!

        return workoutGroupRepository.getWorkoutGroupsFromWorkout(
            workoutId = args.workoutId,
            dayOfWeek = args.dayWeek,
            workoutGroupId = args.workoutGroupId,
            simpleFilter = simpleFilter
        )
    }

    private fun getListExercisesAndPreDefinitions(simpleFilter: String = ""): Flow<PagingData<TOExercise>> {
        val args = jsonArgs?.fromJsonNavParamToArgs(ExerciseScreenArgs::class.java)!!

        return exercisePreDefinitionRepository.getExercisesAndPreDefinitions(
            authenticatedPersonId = _uiState.value.authenticatedPerson.id!!,
            simpleFilter = simpleFilter,
            workoutId = args.workoutId
        ).flow
    }

    fun loadUIStateWithDatabaseInfos() {
        launch {
            val args = jsonArgs?.fromJsonNavParamToArgs(ExerciseScreenArgs::class.java)!!

            loadAuthenticatedPerson()
            loadTopBar(args)
            loadGroups(args)
            loadExercises()
            loadExerciseInfoEdition(args.exerciseId)
            loadExerciseVideos(args.exerciseId)

            _uiState.value.executeLoad = false
        }
    }

    private suspend fun loadTopBar(args: ExerciseScreenArgs) {
        val workout = workoutRepository.findWorkoutById(args.workoutId)

        _uiState.value = _uiState.value.copy(
            title = getTitle(),
            subtitle = context.getString(
                R.string.exercise_screen_subtitle,
                workout?.memberName
            )
        )
    }

    private fun loadExercises() {
        _uiState.value = _uiState.value.copy(
            exercise = _uiState.value.exercise.copy(
                dialogListState = _uiState.value.exercise.dialogListState.copy(
                    dataList = getListExercisesAndPreDefinitions()
                )
            )
        )
    }

    private suspend fun loadExerciseInfoEdition(exerciseId: String?) {
        Log.i("Teste", "loadExerciseInfoEdition: exerciseId = ${exerciseId}")
        exerciseId?.let { id ->
            val args = jsonArgs?.fromJsonNavParamToArgs(ExerciseScreenArgs::class.java)!!
            val toExercise = exerciseRepository.findById(id)
            val convertedRest =  toExercise.unitRest.getStringFromConvertedChronoUnitValue(toExercise.rest)
            val convertedDuration = toExercise.unitDuration.getStringFromConvertedChronoUnitValue(toExercise.duration)

            _uiState.value = _uiState.value.copy(
                toExercise = toExercise,
                group = _uiState.value.group.copy(
                    value = toExercise.workoutGroupName!!
                ),
                groupOrder = _uiState.value.groupOrder.copy(
                    value = toExercise.groupOrder.toStringOrEmpty()
                ),
                exercise = _uiState.value.exercise.copy(
                    value = toExercise.name!!
                ),
                exerciseOrder = _uiState.value.exerciseOrder.copy(
                    value = toExercise.exerciseOrder.toStringOrEmpty()
                ),
                sets = _uiState.value.sets.copy(
                    value = toExercise.sets.toStringOrEmpty()
                ),
                reps = _uiState.value.reps.copy(
                    value = toExercise.repetitions.toStringOrEmpty()
                ),
                rest = _uiState.value.rest.copy(
                    value = convertedRest
                ),
                unitRest = _uiState.value.unitRest.copy(
                    value = toExercise.rest.getChronoUnitLabel(context)
                ),
                duration = _uiState.value.duration.copy(
                    value = convertedDuration
                ),
                unitDuration = _uiState.value.unitDuration.copy(
                    value = toExercise.duration.getChronoUnitLabel(context)
                ),
                observation = _uiState.value.observation.copy(
                    value = toExercise.observation ?: ""
                ),
                tabState = _uiState.value.tabState.copy(tabs = getTabListAllEnabled())
            )

            loadTopBar(args)
        }
    }

    private suspend fun loadExerciseVideos(exerciseId: String?) {
        exerciseId?.let { id ->
            val videoFilePaths = videoRepository.getVideoExercises(id)

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

    private suspend fun loadAuthenticatedPerson() {
        _uiState.value = _uiState.value.copy(
            authenticatedPerson = personRepository.getAuthenticatedTOPerson()!!
        )
    }

    private suspend fun loadGroups(args: ExerciseScreenArgs) {
        val groups = workoutGroupRepository.getWorkoutGroupsFromWorkout(
            workoutId = args.workoutId,
            dayOfWeek = args.dayWeek,
            workoutGroupId = args.workoutGroupId
        )

        _uiState.value = _uiState.value.copy(
            group = _uiState.value.group.copy(
                dialogListState = _uiState.value.group.dialogListState.copy(
                    dataList = groups
                )
            )
        )

        groups.firstOrNull { it.id == args.workoutGroupId }?.let { selectedGroup ->
            _uiState.value = _uiState.value.copy(
                group = _uiState.value.group.copy(
                    value = selectedGroup.name!!
                ),
                groupOrder = _uiState.value.groupOrder.copy(
                    value = selectedGroup.order.toStringOrEmpty()
                ),
                toExercise = _uiState.value.toExercise.copy(
                    workoutGroupId = selectedGroup.id,
                    workoutGroupName = selectedGroup.name,
                    groupOrder = selectedGroup.order
                )
            )
        }
    }

    private suspend fun getTitle(): String {
        val args = jsonArgs?.fromJsonNavParamToArgs(ExerciseScreenArgs::class.java)!!

        return if (_uiState.value.toExercise.id != null) {
            val exercise = exerciseRepository.findById(_uiState.value.toExercise.id!!)

            if (args.dayWeek != null) {
                "${exercise.name} - ${args.dayWeek.getFirstPartFullDisplayName()}"
            } else {
                exercise.name!!
            }

        } else {
            if (args.dayWeek != null) {
                context.getString(
                    R.string.exercise_screen_title_new_from_day_week,
                    args.dayWeek.getFirstPartFullDisplayName()
                )
            } else {
                context.getString(R.string.exercise_screen_title_new)
            }
        }
    }

    fun saveExercise(onSuccess: () -> Unit) {
        launch {
            val validationResults = saveExerciseUseCase(_uiState.value.toExercise)

            if (validationResults.isEmpty()) {
                onSuccess()
                loadExerciseInfoEdition(_uiState.value.toExercise.id)
            } else {
                _uiState.value.onToggleLoading()
                showExerciseFieldsValidationMessages(validationResults.toMutableList())
            }
        }
    }

    private fun showExerciseFieldsValidationMessages(validationResults: MutableList<FieldValidationError<EnumValidatedExerciseFields>>) {
        val dialogValidations = validationResults.filter(::isGlobalFieldsValidation)
        validationResults.removeAll(::isGlobalFieldsValidation)

        if (dialogValidations.isNotEmpty()) {
            _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(dialogValidations.first().message)
            return
        }

        val fieldValidations = validationResults.filter(::isFieldValidation)
        validationResults.removeAll(::isFieldValidation)

        fieldValidations.forEach {
            when (it.field!!) {
                EnumValidatedExerciseFields.EXERCISE_GROUP -> {
                    _uiState.value = _uiState.value.copy(
                        group = _uiState.value.group.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedExerciseFields.EXERCISE_GROUP_ORDER -> {
                    _uiState.value = _uiState.value.copy(
                        groupOrder = _uiState.value.groupOrder.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedExerciseFields.EXERCISE -> {
                    _uiState.value = _uiState.value.copy(
                        exercise = _uiState.value.exercise.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedExerciseFields.EXERCISE_ORDER -> {
                    _uiState.value = _uiState.value.copy(
                        exerciseOrder = _uiState.value.exerciseOrder.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedExerciseFields.SETS -> {
                    _uiState.value = _uiState.value.copy(
                        sets = _uiState.value.sets.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedExerciseFields.REPETITIONS -> {
                    _uiState.value = _uiState.value.copy(
                        reps = _uiState.value.reps.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedExerciseFields.REST -> {
                    _uiState.value = _uiState.value.copy(
                        rest = _uiState.value.rest.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedExerciseFields.UNIT_REST -> {
                    _uiState.value = _uiState.value.copy(
                        unitRest = _uiState.value.unitRest.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedExerciseFields.DURATION -> {
                    _uiState.value = _uiState.value.copy(
                        duration = _uiState.value.duration.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedExerciseFields.UNIT_DURATION -> {
                    _uiState.value = _uiState.value.copy(
                        unitDuration = _uiState.value.unitDuration.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedExerciseFields.OBSERVATION -> {
                    _uiState.value = _uiState.value.copy(
                        observation = _uiState.value.observation.copy(
                            errorMessage = it.message
                        )
                    )
                }
            }
        }
    }

    private fun isFieldValidation(error: FieldValidationError<EnumValidatedExerciseFields>): Boolean {
        return error.field != null
    }

    private fun isGlobalFieldsValidation(error: FieldValidationError<EnumValidatedExerciseFields>): Boolean {
        return error.field == null
    }

    fun onOpenCameraVideo(file: File) {
        _uiState.value.newVideoFileFromCamera = file
    }

    fun onFinishVideoRecording(onSuccess: () -> Unit) {
        launch {
            val validationResult = saveExerciseVideoUseCase(
                exerciseId = _uiState.value.toExercise.id!!,
                videoFile = _uiState.value.newVideoFileFromCamera!!
            )

            if (validationResult != null) {
                onShowErrorDialog(validationResult.message)
            } else {
                loadExerciseVideos(_uiState.value.toExercise.id)
                onSuccess()
            }

        }
    }

    fun onVideoSelectedOnGallery(uri: Uri, onSuccess: () -> Unit) {
        launch {
            val validationResult = saveExerciseVideoFromGalleryUseCase(
                exerciseId = _uiState.value.toExercise.id!!,
                uri = uri
            )

            if (validationResult != null) {
                onShowErrorDialog(validationResult.message)
            } else {
                loadExerciseVideos(_uiState.value.toExercise.id)
                onSuccess()
            }
        }
    }

    fun onVideoClick(path: String, onOpenVideo: (path: String) -> Unit) {
        if (FileUtils.getFileExists(path)) {
            onOpenVideo(path)
        } else {
            _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(
                message = context.getString(R.string.exercise_screen_msg_video_not_found)
            )
        }
    }

    fun onInactivateExercise(onSuccess: () -> Unit) {
        _uiState.value.toExercise.id?.let {
            _uiState.value.messageDialogState.onShowDialog?.showConfirmationDialog(
                message = context.getString(R.string.exercise_screen_msg_confirm_inactivate_exercise),
                onConfirm = {
                    _uiState.value.onToggleLoading()

                    launch {
                        inactivateExerciseUseCase(it)
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
                    inactivateVideoExerciseUseCase(listOf(filePath))
                    loadExerciseVideos(_uiState.value.toExercise.id)
                    onSuccess()
                    _uiState.value.onToggleLoading()
                }
            }
        )
    }
}