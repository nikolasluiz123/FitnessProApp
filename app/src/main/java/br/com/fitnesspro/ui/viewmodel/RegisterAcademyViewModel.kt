package br.com.fitnesspro.ui.viewmodel

import android.content.Context
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fitnesspro.R
import br.com.fitnesspro.compose.components.menu.MenuItem
import br.com.fitnesspro.compose.components.menu.selectValue
import br.com.fitnesspro.compose.components.state.Field
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.enums.EnumDialogType
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.core.extensions.getFirstPartFullDisplayName
import br.com.fitnesspro.core.extensions.parseToLocalTime
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.repository.AcademyRepository
import br.com.fitnesspro.repository.UserRepository
import br.com.fitnesspro.to.TOAcademy
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOPersonAcademyTime
import br.com.fitnesspro.ui.navigation.RegisterAcademyScreenArgs
import br.com.fitnesspro.ui.navigation.registerAcademyArguments
import br.com.fitnesspro.ui.state.RegisterAcademyUIState
import br.com.fitnesspro.usecase.academy.EnumValidatedAcademyFields
import br.com.fitnesspro.usecase.academy.SavePersonAcademyTimeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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

    private val jsonArgs: String? = savedStateHandle[registerAcademyArguments]

    init {
        initialLoadUIState()
        loadAcademyList()
        loadDayWeekList()
        loadUIStateWithPersonInformation()
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
                academy = Field(onChange = { text ->
                    _uiState.value = _uiState.value.copy(
                        academy = _uiState.value.academy.copy(
                            value = text,
                            errorMessage = ""
                        )
                    )
                }),
                dayWeek = Field(onChange = {
                    _uiState.value = _uiState.value.copy(
                        dayWeek = _uiState.value.dayWeek.copy(
                            value = it,
                            errorMessage = ""
                        )
                    )
                }),
                start = Field(onChange = {
                    if (it.isDigitsOnly()) {
                        _uiState.value = _uiState.value.copy(
                            start = _uiState.value.start.copy(
                                value = it,
                                errorMessage = ""
                            )
                        )
                    }
                }),
                end = Field(onChange = {
                    if (it.isDigitsOnly()) {
                        _uiState.value = _uiState.value.copy(
                            end = _uiState.value.end.copy(
                                value = it,
                                errorMessage = ""
                            )
                        )
                    }
                })
            )
        }
    }

    private fun loadAcademyList() {
        viewModelScope.launch {
            val items = academyRepository.getAcademies().map {
                MenuItem(
                    label = it.name!!,
                    value = it.id
                )
            }

            _uiState.value = _uiState.value.copy(academies = items)
        }
    }

    private fun loadDayWeekList() {
        val dayWeeks = DayOfWeek.entries.map { dayOfWeek ->
            MenuItem(
                label = dayOfWeek.getFirstPartFullDisplayName(),
                value = dayOfWeek
            )
        }

        _uiState.value = _uiState.value.copy(dayWeeks = dayWeeks)
    }

    private fun loadUIStateWithPersonInformation() {
        val args = jsonArgs?.fromJsonNavParamToArgs(RegisterAcademyScreenArgs::class.java)!!

        viewModelScope.launch {
            val toPerson = userRepository.getTOPersonById(personId = args.personId)
            val toPersonAcademyTime = args.personAcademyTimeId?.let {
                userRepository.getTOPersonAcademyTimeById(personAcademyTimeId = it)
            }

            _uiState.update {
                it.copy(
                    toPersonAcademyTime = toPersonAcademyTime,
                    title = getTitle(toPerson, toPersonAcademyTime),
                    subtitle = getSubtitle(toPersonAcademyTime),
                    academy = it.academy.copy(value = toPersonAcademyTime?.toAcademy?.name ?: ""),
                    dayWeek = it.dayWeek.copy(value = toPersonAcademyTime?.dayOfWeek?.getFirstPartFullDisplayName() ?: ""),
                    start = it.start.copy(value = toPersonAcademyTime?.timeStart?.format(EnumDateTimePatterns.TIME_ONLY_NUMBERS) ?: ""),
                    end = it.end.copy(value = toPersonAcademyTime?.timeEnd?.format(EnumDateTimePatterns.TIME_ONLY_NUMBERS) ?: "")
                )
            }

            toPersonAcademyTime?.let {  to ->
                _uiState.value.academies.selectValue(to.toAcademy?.id!!)
                _uiState.value.dayWeeks.selectValue(to.dayOfWeek!!)
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
            val args = jsonArgs?.fromJsonNavParamToArgs(RegisterAcademyScreenArgs::class.java)!!
            val academy = academyRepository.getAcademyById(id = _uiState.value.academies.first { it.selected }.value)

            val to = TOPersonAcademyTime(
                id = args.personAcademyTimeId,
                personId = args.personId,
                toAcademy = TOAcademy(
                    id = academy.id,
                    name = academy.name,
                    address = academy.address,
                    phone = academy.phone
                ),
                timeStart = _uiState.value.start.value.parseToLocalTime(EnumDateTimePatterns.TIME_ONLY_NUMBERS),
                timeEnd = _uiState.value.end.value.parseToLocalTime(EnumDateTimePatterns.TIME_ONLY_NUMBERS),
                dayOfWeek = _uiState.value.dayWeeks.first { it.selected }.value,
            )

            val validationResults = savePersonAcademyTimeUseCase.execute(to)

            if (validationResults.isEmpty()) {
                onSuccess()
                loadUIStateWithPersonInformation()
            } else {
                showValidationMessages(validationResults)
            }
        }

    }

    private fun showValidationMessages(validationResults: List<Pair<EnumValidatedAcademyFields?, String>>) {
        val dialogValidations = validationResults.firstOrNull { it.first == null }

        if (dialogValidations != null) {
            _uiState.value.onShowDialog?.onShow(
                type = EnumDialogType.ERROR,
                message = dialogValidations.second,
                onConfirm = { _uiState.value.onHideDialog.invoke() },
                onCancel = { _uiState.value.onHideDialog.invoke() }
            )

            return
        }

        validationResults.forEach {
            when (it.first!!) {
                EnumValidatedAcademyFields.ACADEMY -> {
                    _uiState.value = _uiState.value.copy(
                        academy = _uiState.value.academy.copy(
                            errorMessage = it.second
                        )
                    )
                }

                EnumValidatedAcademyFields.DATE_TIME_START -> {
                    _uiState.value = _uiState.value.copy(
                        start = _uiState.value.start.copy(
                            errorMessage = it.second
                        )
                    )
                }

                EnumValidatedAcademyFields.DATE_TIME_END -> {
                    _uiState.value = _uiState.value.copy(
                        end = _uiState.value.end.copy(
                            errorMessage = it.second
                        )
                    )
                }

                EnumValidatedAcademyFields.DAY_OF_WEEK -> {
                    _uiState.value = _uiState.value.copy(
                        dayWeek = _uiState.value.dayWeek.copy(
                            errorMessage = it.second
                        )
                    )
                }
            }
        }
    }

}