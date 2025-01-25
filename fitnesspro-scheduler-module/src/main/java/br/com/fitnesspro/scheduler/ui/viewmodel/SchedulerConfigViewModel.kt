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
import br.com.fitnesspro.core.enums.EnumDialogType
import br.com.fitnesspro.core.extensions.toIntOrNull
import br.com.fitnesspro.core.validation.FieldValidationError
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
                alarm = initializeAlarmSwitchField(),
                notification = initializeNotificationSwitchField(),
                minEventDensity = initializeMinEventDensityTextField(),
                maxEventDensity = initializeMaxEventDensityTextField(),
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

    private fun initializeMaxEventDensityTextField(): TextField {
        return TextField(
            onChange = { value ->
                if (value.isDigitsOnly()) {
                    _uiState.value = _uiState.value.copy(
                        maxEventDensity = _uiState.value.maxEventDensity.copy(value = value),
                        toConfig = _uiState.value.toConfig.copy(maxScheduleDensity = value.toIntOrNull())
                    )
                }
            }
        )
    }

    private fun initializeMinEventDensityTextField(): TextField {
        return TextField(
            onChange = { value ->
                if (value.isDigitsOnly()) {
                    _uiState.value = _uiState.value.copy(
                        minEventDensity = _uiState.value.minEventDensity.copy(value = value),
                        toConfig = _uiState.value.toConfig.copy(minScheduleDensity = value.toIntOrNull())
                    )
                }
            }
        )
    }

    private fun initializeNotificationSwitchField(): SwitchButtonField {
        return SwitchButtonField(
            onCheckedChange = { checked ->
                _uiState.value = _uiState.value.copy(
                    notification = _uiState.value.notification.copy(checked = checked),
                    toConfig = _uiState.value.toConfig.copy(notification = checked)
                )
            }
        )
    }

    private fun initializeAlarmSwitchField(): SwitchButtonField {
        return SwitchButtonField(
            onCheckedChange = { checked ->
                _uiState.value = _uiState.value.copy(
                    alarm = _uiState.value.alarm.copy(checked = checked),
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

    private fun showValidationMessage(validationResults: MutableList<FieldValidationError<EnumValidatedSchedulerConfigFields, EnumSchedulerConfigValidationTypes>>) {
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
            }
        }
    }
}