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
import br.com.fitnesspro.compose.components.fields.state.DialogListState
import br.com.fitnesspro.compose.components.fields.state.DialogListTextField
import br.com.fitnesspro.compose.components.fields.state.DropDownTextField
import br.com.fitnesspro.compose.components.fields.state.PagedDialogListState
import br.com.fitnesspro.compose.components.fields.state.PagedDialogListTextField
import br.com.fitnesspro.compose.components.fields.state.TabState
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.compose.components.gallery.video.state.VideoGalleryState
import br.com.fitnesspro.compose.components.tabs.Tab
import br.com.fitnesspro.core.callback.showConfirmationDialog
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.extensions.bestChronoUnit
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.core.extensions.getFirstPartFullDisplayName
import br.com.fitnesspro.core.extensions.millisTo
import br.com.fitnesspro.core.extensions.openVideoPlayer
import br.com.fitnesspro.core.extensions.toIntOrNull
import br.com.fitnesspro.core.extensions.toStringOrEmpty
import br.com.fitnesspro.core.state.MessageDialogState
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
import br.com.fitnesspro.workout.usecase.exercise.video.SaveExerciseVideoUseCase
import br.com.fitnesspro.workout.usecase.exercise.video.gallery.SaveExerciseVideoFromGalleryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    savedStateHandle: SavedStateHandle
): FitnessProViewModel() {

    private val _uiState: MutableStateFlow<ExerciseUIState> = MutableStateFlow(ExerciseUIState())
    val uiState get() = _uiState.asStateFlow()

    private val jsonArgs: String? = savedStateHandle[exerciseScreenArguments]

    init {
        initialLoadUIState()
        loadUIStateWithDatabaseInfos()
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

    private fun initialLoadUIState() {
        val args = jsonArgs?.fromJsonNavParamToArgs(ExerciseScreenArgs::class.java)!!

        _uiState.update {
            it.copy(
                group = initializeDialogListTextFieldGroup(),
                groupOrder = initializeTextFieldGroupOrder(),
                exercise = initializePagedDialogListTextFieldExercise(),
                exerciseOrder = initializeTextFieldExerciseOrder(),
                sets = initializeTextFieldSets(),
                reps = initializeTextFieldReps(),
                rest = initializeTextFieldRest(),
                unitRest = initializeDropDownTextFieldUnitRest(),
                duration = initializeTextFieldDuration(),
                unitDuration = initializeDropDownTextFieldUnitDuration(),
                observation = initializeTextFieldObservation(),
                videoGalleryState = initializeVideoGalleryState(),
                tabState = initializeTabState(),
                messageDialogState = initializeMessageDialogState(),
                toExercise = _uiState.value.toExercise.copy(
                    workoutId = args.workoutId,
                    dayWeek = args.dayWeek
                ),
                onToggleLoading = {
                    _uiState.value = _uiState.value.copy(showLoading = _uiState.value.showLoading.not())
                }
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

    private fun getTabListWithSelectedTab(selectedTab: Tab): MutableList<Tab> {
        return _uiState.value.tabState.tabs.map { tab ->
            tab.copy(selected = tab.enum == selectedTab.enum)
        }.toMutableList()
    }

    private fun getTabListAllEnabled(): MutableList<Tab> {
        return _uiState.value.tabState.tabs.map { tab ->
            tab.copy(enabled = true)
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

    private fun initializeDialogListTextFieldGroup(): DialogListTextField<TOWorkoutGroup> {
        return DialogListTextField(
            dialogListState = DialogListState(
                dialogTitle = context.getString(R.string.exercise_screen_group_dialog_list_title),
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
                        toExercise = _uiState.value.toExercise.copy(
                            workoutGroupId = item.id,
                            workoutGroupName = item.name
                        )
                    )

                    _uiState.value.group.dialogListState.onHide()
                },
                onSimpleFilterChange = { filter ->
                    launch {
                        _uiState.value = _uiState.value.copy(
                            group = _uiState.value.group.copy(
                                dialogListState = _uiState.value.group.dialogListState.copy(
                                    dataList = getListWorkoutGroups(simpleFilter = filter)
                                )
                            )
                        )
                    }
                },
            ),
            onChange = {
                _uiState.value = _uiState.value.copy(
                    group = _uiState.value.group.copy(
                        value = it,
                    ),
                    toExercise = _uiState.value.toExercise.copy(
                        workoutGroupName = it
                    )
                )
            }
        )
    }

    private fun initializeTextFieldGroupOrder(): TextField {
        return TextField(
            onChange = {
                if (it.isDigitsOnly()) {
                    _uiState.value = _uiState.value.copy(
                        groupOrder = _uiState.value.groupOrder.copy(
                            value = it,
                            errorMessage = ""
                        ),
                        toExercise = _uiState.value.toExercise.copy(groupOrder = it.toIntOrNull())
                    )
                }
            }
        )
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
                        toExercise = _uiState.value.toExercise.copy(exerciseOrder = it.toIntOrNull())
                    )
                }
            }
        )
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

    private fun initializePagedDialogListTextFieldExercise(): PagedDialogListTextField<TOExercise> {
        return PagedDialogListTextField(
            dialogListState = PagedDialogListState(
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
                        toExercise = item
                    )

                    _uiState.value.exercise.dialogListState.onHide()
                },
                onSimpleFilterChange = { filter ->
                    launch {
                        _uiState.value = _uiState.value.copy(
                            exercise = _uiState.value.exercise.copy(
                                dialogListState = _uiState.value.exercise.dialogListState.copy(
                                    dataList = getListExercisesAndPreDefinitions(simpleFilter = filter)
                                )
                            )
                        )
                    }
                },
            ),
            onChange = { newText ->
                _uiState.value = _uiState.value.copy(
                    exercise = _uiState.value.exercise.copy(
                        value = newText,
                        errorMessage = ""
                    ),
                    toExercise = _uiState.value.toExercise.copy(
                        name = newText
                    )
                )
            }
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

    private fun initializeTextFieldSets(): TextField {
        return TextField(
           onChange = {
               _uiState.value = _uiState.value.copy(
                   sets = _uiState.value.sets.copy(
                       value = it,
                       errorMessage = ""
                   ),
                   toExercise = _uiState.value.toExercise.copy(sets = it.toIntOrNull())
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
                    toExercise = _uiState.value.toExercise.copy(repetitions = it.toIntOrNull())
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
                    toExercise = _uiState.value.toExercise.copy(rest = it.toLongOrNull())
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
                    toExercise = _uiState.value.toExercise.copy(
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
                    toExercise = _uiState.value.toExercise.copy(duration = it.toLongOrNull())
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
                    toExercise = _uiState.value.toExercise.copy(
                        unitDuration = it.value
                    )
                )

                _uiState.value.unitDuration.onDropDownDismissRequest()
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

    private fun initializeTextFieldObservation(): TextField {
        return TextField(
            onChange = {
                _uiState.value = _uiState.value.copy(
                    observation = _uiState.value.observation.copy(
                        value = it,
                        errorMessage = ""
                    ),
                    toExercise = _uiState.value.toExercise.copy(observation = it)
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

    private fun loadUIStateWithDatabaseInfos() {
        launch {
            val args = jsonArgs?.fromJsonNavParamToArgs(ExerciseScreenArgs::class.java)!!

            loadAuthenticatedPerson()
            loadTopBar(args)
            loadGroups(args)
            loadExercises()
            loadExerciseInfoEdition(args.exerciseId)
            loadExerciseVideos(args.exerciseId)
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
        exerciseId?.let { id ->
            val args = jsonArgs?.fromJsonNavParamToArgs(ExerciseScreenArgs::class.java)!!
            val toExercise = exerciseRepository.findById(id)
            val convertedRest = getConvertedRestFrom(toExercise)
            val convertedDuration = getConvertedDurationFrom(toExercise)

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
                    value = getUnitRestFrom(toExercise)
                ),
                duration = _uiState.value.duration.copy(
                    value = convertedDuration
                ),
                unitDuration = _uiState.value.unitDuration.copy(
                    value = getUnitDurationFrom(toExercise)
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
                    thumbCache = videoFilePaths.associate { filePath ->
                        filePath to VideoUtils.generateVideoThumbnail(filePath)
                    }
                )
            )
        }
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

    private fun getLabelFromChronoUnit(unit: ChronoUnit): String {
        return when (unit) {
            ChronoUnit.SECONDS -> context.getString(R.string.exercise_screen_chrono_unit_seconds)
            ChronoUnit.MINUTES -> context.getString(R.string.exercise_screen_chrono_unit_minutes)
            ChronoUnit.HOURS -> context.getString(R.string.exercise_screen_chrono_unit_hours)
            else -> throw IllegalArgumentException("Valor invalido para recuperar um label de ChronoUnit")
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

    fun onVideoClick(path: String) {
        if (FileUtils.getFileExists(path)) {
            context.openVideoPlayer(filePath = path)
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
}