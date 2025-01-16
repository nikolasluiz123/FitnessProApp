package br.com.fitnesspro.scheduler.ui.viewmodel

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fitnesspro.common.repository.SchedulerConfigRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.usecase.scheduler.SaveSchedulerConfigUseCase
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumSchedulerConfigValidationTypes
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumValidatedSchedulerConfigFields
import br.com.fitnesspro.compose.components.fields.state.SwitchButtonField
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.compose.components.fields.state.TimePickerTextField
import br.com.fitnesspro.core.enums.EnumDateTimePatterns
import br.com.fitnesspro.core.enums.EnumDialogType
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.core.validation.ValidationError
import br.com.fitnesspro.scheduler.ui.state.SchedulerConfigUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SchedulerConfigViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val schedulerConfigRepository: SchedulerConfigRepository,
    private val schedulerConfigUseCase: SaveSchedulerConfigUseCase
): ViewModel() {

    private val _uiState: MutableStateFlow<SchedulerConfigUIState> = MutableStateFlow(SchedulerConfigUIState())
    val uiState get() = _uiState.asStateFlow()

    init {
        initialUIStateLoad()
        loadUIStateWithDatabaseInfos()
    }

    private fun initialUIStateLoad() {
        _uiState.update { state ->
            state.copy(
                alarm = initializeAlarmSwitchField(state),
                notification = initializeNotificationSwitchField(state),
                minEventDensity = initializeMinEventDensityTextField(state),
                maxEventDensity = initializeMaxEventDensityTextField(state),
                startWorkTime = initializeStartWorkTimeTimePickerField(state),
                endWorkTime = initializeEndWorkTimeTimePickerField(state),
                startBreakTime = initializeStartBreakTimeTimePickerField(state),
                endBreakTime = initializeEndBreakTimeTimePickerField(state),
                onShowDialog = initializeOnShowDialog(),
                onHideDialog = initializeOnHideDialog(),
            )
        }
    }

    private fun loadUIStateWithDatabaseInfos() {
        viewModelScope.launch {
            val toPerson = userRepository.getAuthenticatedTOPerson()!!
            val toConfig = schedulerConfigRepository.getTOSchedulerConfigByPersonId(toPerson.id!!)!!

            _uiState.value = _uiState.value.copy(
                userType = toPerson.toUser?.type!!,
                toConfig = toConfig,
                alarm = _uiState.value.alarm.copy(
                    checked = toConfig.alarm
                ),
                notification = _uiState.value.notification.copy(
                    checked = toConfig.notification
                ),
                minEventDensity = _uiState.value.minEventDensity.copy(
                    value = toConfig.minScheduleDensity.toString()
                ),
                maxEventDensity = _uiState.value.maxEventDensity.copy(
                    value = toConfig.maxScheduleDensity.toString()
                ),
                startWorkTime = _uiState.value.startWorkTime.copy(
                    value = toConfig.startWorkTime!!.format(EnumDateTimePatterns.TIME_ONLY_NUMBERS)
                ),
                endWorkTime = _uiState.value.endWorkTime.copy(
                    value = toConfig.endWorkTime!!.format(EnumDateTimePatterns.TIME_ONLY_NUMBERS)
                ),
                startBreakTime = _uiState.value.startBreakTime.copy(
                    value = toConfig.startBreakTime!!.format(EnumDateTimePatterns.TIME_ONLY_NUMBERS)
                ),
                endBreakTime = _uiState.value.endBreakTime.copy(
                    value = toConfig.endBreakTime!!.format(EnumDateTimePatterns.TIME_ONLY_NUMBERS)
                )
            )
        }
    }

    private fun initializeOnHideDialog(): () -> Unit = {
        _uiState.value = _uiState.value.copy(showDialog = false)
    }

    private fun initializeOnShowDialog(): (EnumDialogType, String, () -> Unit, () -> Unit) -> Unit {
        return { type, message, onConfirm, onCancel ->
            _uiState.value = _uiState.value.copy(
                dialogType = type,
                showDialog = true,
                dialogMessage = message,
                onConfirm = onConfirm,
                onCancel = onCancel
            )
        }
    }

    private fun initializeEndBreakTimeTimePickerField(state: SchedulerConfigUIState): TimePickerTextField {
        return TimePickerTextField(
            onTimePickerOpenChange = {
                _uiState.value = _uiState.value.copy(
                    endBreakTime = state.endBreakTime.copy(timePickerOpen = it)
                )
            },
            onTimeChange = { newTime ->
                _uiState.value = _uiState.value.copy(
                    endBreakTime = _uiState.value.endBreakTime.copy(
                        value = newTime.format(EnumDateTimePatterns.TIME_ONLY_NUMBERS),
                        errorMessage = ""
                    ),
                    toConfig = _uiState.value.toConfig.copy(endBreakTime = newTime)
                )
            },
            onTimeDismiss = {
                _uiState.value = _uiState.value.copy(
                    endBreakTime = _uiState.value.endBreakTime.copy(timePickerOpen = false)
                )
            },
            onChange = { text ->
                if (text.isDigitsOnly()) {
                    _uiState.value = _uiState.value.copy(
                        endBreakTime = _uiState.value.endBreakTime.copy(
                            value = text,
                            errorMessage = ""
                        )
                    )
                }
            }
        )
    }

    private fun initializeStartBreakTimeTimePickerField(state: SchedulerConfigUIState): TimePickerTextField {
        return TimePickerTextField(
            onTimePickerOpenChange = {
                _uiState.value = _uiState.value.copy(
                    startBreakTime = state.startBreakTime.copy(timePickerOpen = it)
                )
            },
            onTimeChange = { newTime ->
                _uiState.value = _uiState.value.copy(
                    startBreakTime = _uiState.value.startBreakTime.copy(
                        value = newTime.format(EnumDateTimePatterns.TIME_ONLY_NUMBERS),
                        errorMessage = ""
                    ),
                    toConfig = _uiState.value.toConfig.copy(startBreakTime = newTime)
                )
            },
            onTimeDismiss = {
                _uiState.value = _uiState.value.copy(
                    startBreakTime = _uiState.value.startBreakTime.copy(timePickerOpen = false)
                )
            },
            onChange = { text ->
                if (text.isDigitsOnly()) {
                    _uiState.value = _uiState.value.copy(
                        startBreakTime = _uiState.value.startBreakTime.copy(
                            value = text,
                            errorMessage = ""
                        )
                    )
                }
            }
        )
    }

    private fun initializeEndWorkTimeTimePickerField(state: SchedulerConfigUIState): TimePickerTextField {
        return TimePickerTextField(
            onTimePickerOpenChange = {
                _uiState.value = _uiState.value.copy(
                    endWorkTime = state.endWorkTime.copy(timePickerOpen = it)
                )
            },
            onTimeChange = { newTime ->
                _uiState.value = _uiState.value.copy(
                    endWorkTime = _uiState.value.endWorkTime.copy(
                        value = newTime.format(EnumDateTimePatterns.TIME_ONLY_NUMBERS),
                        errorMessage = ""
                    ),
                    toConfig = _uiState.value.toConfig.copy(endWorkTime = newTime)
                )
            },
            onTimeDismiss = {
                _uiState.value = _uiState.value.copy(
                    endWorkTime = _uiState.value.endWorkTime.copy(timePickerOpen = false)
                )
            },
            onChange = { text ->
                if (text.isDigitsOnly()) {
                    _uiState.value = _uiState.value.copy(
                        endWorkTime = _uiState.value.endWorkTime.copy(
                            value = text,
                            errorMessage = ""
                        )
                    )
                }
            }
        )
    }

    private fun initializeStartWorkTimeTimePickerField(state: SchedulerConfigUIState): TimePickerTextField {
        return TimePickerTextField(
            onTimePickerOpenChange = {
                _uiState.value = _uiState.value.copy(
                    startWorkTime = state.startWorkTime.copy(timePickerOpen = it)
                )
            },
            onTimeChange = { newTime ->
                _uiState.value = _uiState.value.copy(
                    startWorkTime = _uiState.value.startWorkTime.copy(
                        value = newTime.format(EnumDateTimePatterns.TIME_ONLY_NUMBERS),
                        errorMessage = ""
                    ),
                    toConfig = _uiState.value.toConfig.copy(startWorkTime = newTime)
                )
            },
            onTimeDismiss = {
                _uiState.value = _uiState.value.copy(
                    startWorkTime = _uiState.value.startWorkTime.copy(timePickerOpen = false)
                )
            },
            onChange = { text ->
                if (text.isDigitsOnly()) {
                    _uiState.value = _uiState.value.copy(
                        startWorkTime = _uiState.value.startWorkTime.copy(
                            value = text,
                            errorMessage = ""
                        )
                    )
                }
            }
        )
    }

    private fun initializeMaxEventDensityTextField(state: SchedulerConfigUIState): TextField {
        return TextField(
            onChange = { value ->
                _uiState.value = _uiState.value.copy(
                    maxEventDensity = state.maxEventDensity.copy(value = value),
                    toConfig = _uiState.value.toConfig.copy(maxScheduleDensity = value.ifEmpty { null }?.toInt())
                )
            }
        )
    }

    private fun initializeMinEventDensityTextField(state: SchedulerConfigUIState): TextField {
        return TextField(
            onChange = { value ->
                _uiState.value = _uiState.value.copy(
                    minEventDensity = state.minEventDensity.copy(value = value),
                    toConfig = _uiState.value.toConfig.copy(minScheduleDensity = value.ifEmpty { null }?.toInt())
                )
            }
        )
    }

    private fun initializeNotificationSwitchField(state: SchedulerConfigUIState): SwitchButtonField {
        return SwitchButtonField(
            onCheckedChange = { checked ->
                _uiState.value = _uiState.value.copy(
                    notification = state.notification.copy(checked = checked),
                    toConfig = _uiState.value.toConfig.copy(notification = checked)
                )
            }
        )
    }

    private fun initializeAlarmSwitchField(state: SchedulerConfigUIState): SwitchButtonField {
        return SwitchButtonField(
            onCheckedChange = { checked ->
                _uiState.value = _uiState.value.copy(
                    alarm = state.alarm.copy(checked = checked),
                    toConfig = _uiState.value.toConfig.copy(alarm = checked)
                )
            }
        )
    }

    fun save(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val validationResults = schedulerConfigUseCase.saveConfig(
                personId = _uiState.value.toConfig.personId!!,
                toSchedulerConfig = _uiState.value.toConfig
            )

            if (validationResults.isEmpty()) {
                onSuccess()
            } else {
                showValidationMessage(validationResults)
            }
        }
    }

    private fun showValidationMessage(validationResults: MutableList<ValidationError<EnumValidatedSchedulerConfigFields, EnumSchedulerConfigValidationTypes>>) {
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
                EnumValidatedSchedulerConfigFields.MIN_SCHEDULE_DENSITY -> {
                    _uiState.value = _uiState.value.copy(
                        minEventDensity = _uiState.value.minEventDensity.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedSchedulerConfigFields.MAX_SCHEDULE_DENSITY -> {
                    _uiState.value = _uiState.value.copy(
                        maxEventDensity = _uiState.value.maxEventDensity.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedSchedulerConfigFields.START_WORK_TIME -> {
                    _uiState.value = _uiState.value.copy(
                        startWorkTime = _uiState.value.startWorkTime.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedSchedulerConfigFields.END_WORK_TIME -> {
                    _uiState.value = _uiState.value.copy(
                        endWorkTime = _uiState.value.endWorkTime.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedSchedulerConfigFields.START_BREAK_TIME -> {
                    _uiState.value = _uiState.value.copy(
                        startBreakTime = _uiState.value.startBreakTime.copy(
                            errorMessage = it.message
                        )
                    )
                }

                EnumValidatedSchedulerConfigFields.END_BREAK_TIME -> {
                    _uiState.value = _uiState.value.copy(
                        endBreakTime = _uiState.value.endBreakTime.copy(
                            errorMessage = it.message
                        )
                    )
                }
            }
        }
    }
}