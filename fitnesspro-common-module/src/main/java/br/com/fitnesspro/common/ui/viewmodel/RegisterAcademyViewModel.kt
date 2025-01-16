package br.com.fitnesspro.common.ui.viewmodel

import android.content.Context
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.AcademyRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.ui.navigation.RegisterAcademyScreenArgs
import br.com.fitnesspro.common.ui.state.RegisterAcademyUIState
import br.com.fitnesspro.common.usecase.academy.EnumAcademyValidationTypes
import br.com.fitnesspro.common.usecase.academy.EnumValidatedAcademyFields
import br.com.fitnesspro.common.usecase.academy.SavePersonAcademyTimeUseCase
import br.com.fitnesspro.compose.components.fields.menu.MenuItem
import br.com.fitnesspro.compose.components.fields.state.DropDownTextField
import br.com.fitnesspro.compose.components.fields.state.PagedDialogListTextField
import br.com.fitnesspro.compose.components.fields.state.TimePickerTextField
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.enums.EnumDialogType
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.core.extensions.getFirstPartFullDisplayName
import br.com.fitnesspro.core.extensions.parseToLocalTime
import br.com.fitnesspro.core.validation.ValidationError
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.to.TOAcademy
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOPersonAcademyTime
import br.com.fitnesspro.tuple.AcademyTuple
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import javax.inject.Inject

@HiltViewModel
class RegisterAcademyViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository,
    private val academyRepository: AcademyRepository,
    private val savePersonAcademyTimeUseCase: SavePersonAcademyTimeUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState: MutableStateFlow<RegisterAcademyUIState> = MutableStateFlow(RegisterAcademyUIState())
    val uiState get() = _uiState.asStateFlow()

    private val jsonArgs: String? = savedStateHandle[br.com.fitnesspro.common.ui.navigation.registerAcademyArguments]

    init {
        initialLoadUIState()
        loadUIStateWithDatabaseInfos()
    }

    private fun initialLoadUIState() {
        _uiState.update { currentState ->
            currentState.copy(
                onShowDialog = { type, message, onConfirm, onCancel ->
                    _uiState.value = _uiState.value.copy(
                        dialogType = type,
                        showDialog = true,
                        dialogMessage = message,
                        onConfirm = onConfirm,
                        onCancel = onCancel
                    )
                },
                onHideDialog = { _uiState.value = _uiState.value.copy(showDialog = false) },
                academy = initializeAcademyPagedDialogListTextField(),
                dayWeek = initializeDayWeekDropDownTextField(),
                start = initializeStartTimePickerTextField(),
                end = initializeEndTimePickerTextField()
            )
        }
    }

    private fun initializeEndTimePickerTextField(): TimePickerTextField {
        return TimePickerTextField(
            onTimePickerOpenChange = {
                _uiState.value = _uiState.value.copy(
                    end = _uiState.value.end.copy(timePickerOpen = it)
                )
            },
            onTimeDismiss = {
                _uiState.value = _uiState.value.copy(
                    end = _uiState.value.end.copy(timePickerOpen = false)
                )
            },
            onTimeChange = { newEndTime ->
                _uiState.value = _uiState.value.copy(
                    end = _uiState.value.end.copy(
                        value = newEndTime.format(EnumDateTimePatterns.TIME_ONLY_NUMBERS),
                        errorMessage = ""
                    ),
                    toPersonAcademyTime = _uiState.value.toPersonAcademyTime.copy(
                        timeEnd = newEndTime
                    )
                )
            },
            onChange = {
                if (it.isDigitsOnly()) {
                    _uiState.value = _uiState.value.copy(
                        end = _uiState.value.end.copy(
                            value = it,
                            errorMessage = ""
                        ),
                        toPersonAcademyTime = _uiState.value.toPersonAcademyTime.copy(
                            timeEnd = it.parseToLocalTime(EnumDateTimePatterns.TIME_ONLY_NUMBERS)
                        )
                    )
                }
            }
        )
    }

    private fun initializeStartTimePickerTextField(): TimePickerTextField {
        return TimePickerTextField(
            onTimePickerOpenChange = {
                _uiState.value = _uiState.value.copy(
                    start = _uiState.value.start.copy(timePickerOpen = it)
                )
            },
            onTimeDismiss = {
                _uiState.value = _uiState.value.copy(
                    start = _uiState.value.start.copy(timePickerOpen = false)
                )
            },
            onTimeChange = { newStartTime ->
                _uiState.value = _uiState.value.copy(
                    start = _uiState.value.start.copy(
                        value = newStartTime.format(EnumDateTimePatterns.TIME_ONLY_NUMBERS),
                        errorMessage = ""
                    ),
                    toPersonAcademyTime = _uiState.value.toPersonAcademyTime.copy(
                        timeStart = newStartTime
                    )
                )
            },
            onChange = {
                if (it.isDigitsOnly()) {
                    _uiState.value = _uiState.value.copy(
                        start = _uiState.value.start.copy(
                            value = it,
                            errorMessage = ""
                        ),
                        toPersonAcademyTime = _uiState.value.toPersonAcademyTime.copy(
                            timeStart = it.parseToLocalTime(EnumDateTimePatterns.TIME_ONLY_NUMBERS)
                        )
                    )
                }
            }
        )
    }

    private fun initializeDayWeekDropDownTextField(): DropDownTextField<DayOfWeek> {
        val items = DayOfWeek.entries.map { dayOfWeek ->
            MenuItem(
                label = dayOfWeek.getFirstPartFullDisplayName(),
                value = dayOfWeek
            )
        }

        return DropDownTextField(
            dataList = items,
            dataListFiltered = items,
            onDropDownDismissRequest = {
                _uiState.value = _uiState.value.copy(
                    dayWeek = _uiState.value.dayWeek.copy(expanded = false)
                )
            },
            onDropDownExpandedChange = {
                _uiState.value = _uiState.value.copy(
                    dayWeek = _uiState.value.dayWeek.copy(expanded = it)
                )
            },
            onDataListItemClick = {
                _uiState.value = _uiState.value.copy(
                    dayWeek = _uiState.value.dayWeek.copy(
                        value = it.label,
                        errorMessage = ""
                    ),
                    toPersonAcademyTime = _uiState.value.toPersonAcademyTime.copy(
                        dayOfWeek = it.value
                    )
                )

                _uiState.value.dayWeek.onDropDownDismissRequest()
            }
        )
    }

    private fun initializeAcademyPagedDialogListTextField(): PagedDialogListTextField<AcademyTuple> {
        return PagedDialogListTextField(
            dialogTitle = context.getString(R.string.register_academy_screen_title_select_academy),
            onShow = {
                _uiState.value = _uiState.value.copy(
                    academy = _uiState.value.academy.copy(show = true)
                )
            },
            onHide = {
                _uiState.value = _uiState.value.copy(
                    academy = _uiState.value.academy.copy(show = false)
                )
            },
            onChange = { newText ->
                _uiState.value = _uiState.value.copy(
                    academy = _uiState.value.academy.copy(
                        value = newText,
                    )
                )
            },
            onDataListItemClick = { item ->
                _uiState.value = _uiState.value.copy(
                    academy = _uiState.value.academy.copy(
                        value = item.getLabel(),
                        errorMessage = ""
                    ),
                    toPersonAcademyTime = _uiState.value.toPersonAcademyTime.copy(
                        toAcademy = TOAcademy(id = item.id, name = item.name)
                    )
                )

                _uiState.value.academy.onHide()
            },
            onSimpleFilterChange = { filter ->
                _uiState.value = _uiState.value.copy(
                    academy = _uiState.value.academy.copy(
                        dataList = getListAcademies(filter)
                    )
                )
            }
        )
    }

    private fun getListAcademies(filter: String = ""): Flow<PagingData<AcademyTuple>> {
        return academyRepository.getAcademies(simpleFilter = filter).flow
    }

    private fun loadUIStateWithDatabaseInfos() {
        val args = jsonArgs?.fromJsonNavParamToArgs(RegisterAcademyScreenArgs::class.java)!!

        viewModelScope.launch {
            val toPerson = userRepository.getTOPersonById(personId = args.personId)
            val menuItemListAcademy = getListAcademies()
            val toPersonAcademyTime = args.personAcademyTimeId?.let {
                academyRepository.getTOPersonAcademyTimeById(personAcademyTimeId = it)
            }

            _uiState.update { state ->
                state.copy(
                    title = getTitle(toPerson, toPersonAcademyTime),
                    subtitle = getSubtitle(toPersonAcademyTime),
                    academy = state.academy.copy(
                        value = toPersonAcademyTime?.toAcademy?.name ?: "",
                        dataList = menuItemListAcademy,
                    ),
                    dayWeek = state.dayWeek.copy(value = toPersonAcademyTime?.dayOfWeek?.getFirstPartFullDisplayName() ?: ""),
                    start = state.start.copy(value = toPersonAcademyTime?.timeStart?.format(EnumDateTimePatterns.TIME_ONLY_NUMBERS) ?: ""),
                    end = state.end.copy(value = toPersonAcademyTime?.timeEnd?.format(EnumDateTimePatterns.TIME_ONLY_NUMBERS) ?: ""),
                    toPersonAcademyTime = toPersonAcademyTime ?: TOPersonAcademyTime(personId = args.personId)
                )
            }
        }
    }

    private fun getTitle(toPerson: TOPerson, toPersonAcademyTime: TOPersonAcademyTime?): String {
        return if (toPersonAcademyTime != null) {
            context.getString(
                R.string.register_academy_screen_title_edit,
                toPersonAcademyTime.dayOfWeek!!.getFirstPartFullDisplayName(),
                toPersonAcademyTime.timeStart!!.format(EnumDateTimePatterns.TIME),
                toPersonAcademyTime.timeEnd!!.format(EnumDateTimePatterns.TIME)
            )
        } else {
            when (toPerson.toUser?.type!!) {
                EnumUserType.PERSONAL_TRAINER,
                EnumUserType.NUTRITIONIST -> context.getString(R.string.register_academy_screen_title_new_work_hour)

                EnumUserType.ACADEMY_MEMBER -> context.getString(R.string.register_academy_screen_title_new_academy_member)
            }
        }
    }

    private fun getSubtitle(toPersonAcademyTime: TOPersonAcademyTime?): String? {
        return toPersonAcademyTime?.let {
            it.toAcademy?.name!!
        }
    }

    fun saveAcademy(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val validationResults = savePersonAcademyTimeUseCase.execute(_uiState.value.toPersonAcademyTime)

            if (validationResults.isEmpty()) {
                onSuccess()
                loadUIStateWithDatabaseInfos()
            } else {
                showValidationMessages(validationResults)
            }
        }

    }

    private fun showValidationMessages(validationResults: List<ValidationError<EnumValidatedAcademyFields, EnumAcademyValidationTypes>>) {
        val dialogValidations = validationResults.firstOrNull { it.field == null }

        if (dialogValidations != null) {
            _uiState.value.onShowDialog?.onShow(
                type = EnumDialogType.ERROR,
                message = dialogValidations.message,
                onConfirm = { _uiState.value.onHideDialog.invoke() },
                onCancel = { _uiState.value.onHideDialog.invoke() }
            )

            return
        }

        validationResults.forEach {
            when (it.field!!) {
                EnumValidatedAcademyFields.ACADEMY -> {
                    _uiState.value = _uiState.value.copy(
                        academy = _uiState.value.academy.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedAcademyFields.DATE_TIME_START -> {
                    _uiState.value = _uiState.value.copy(
                        start = _uiState.value.start.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedAcademyFields.DATE_TIME_END -> {
                    _uiState.value = _uiState.value.copy(
                        end = _uiState.value.end.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedAcademyFields.DAY_OF_WEEK -> {
                    _uiState.value = _uiState.value.copy(
                        dayWeek = _uiState.value.dayWeek.copy(
                            errorMessage = it.message
                        )
                    )
                }
            }
        }
    }

}