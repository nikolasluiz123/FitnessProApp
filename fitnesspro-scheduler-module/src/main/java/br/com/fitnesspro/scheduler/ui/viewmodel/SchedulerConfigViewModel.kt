package br.com.fitnesspro.scheduler.ui.viewmodel

import android.content.Context
import androidx.core.text.isDigitsOnly
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.SchedulerConfigRepository
import br.com.fitnesspro.common.ui.viewmodel.FitnessProViewModel
import br.com.fitnesspro.common.usecase.scheduler.SaveSchedulerConfigUseCase
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumSchedulerConfigValidationTypes
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumValidatedSchedulerConfigFields
import br.com.fitnesspro.compose.components.fields.state.SwitchButtonField
import br.com.fitnesspro.compose.components.fields.state.TextField
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.extensions.toIntOrNull
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.scheduler.ui.state.SchedulerConfigUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SchedulerConfigViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val personRepository: PersonRepository,
    private val schedulerConfigRepository: SchedulerConfigRepository,
    private val schedulerConfigUseCase: SaveSchedulerConfigUseCase
) : FitnessProViewModel() {

    private val _uiState: MutableStateFlow<SchedulerConfigUIState> = MutableStateFlow(SchedulerConfigUIState())
    val uiState get() = _uiState.asStateFlow()

    init {
        initialUIStateLoad()
        loadUIStateWithDatabaseInfos()
    }

    override fun onShowError(throwable: Throwable) {
        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(
            message = context.getString(R.string.unknown_error_message)
        )
    }

    private fun initialUIStateLoad() {
        _uiState.update { state ->
            state.copy(
                alarm = initializeAlarmSwitchField(),
                notification = initializeNotificationSwitchField(),
                minEventDensity = initializeMinEventDensityTextField(),
                maxEventDensity = initializeMaxEventDensityTextField(),
                messageDialogState = initializeMessageDialogState(),
            )
        }
    }

    private fun loadUIStateWithDatabaseInfos() {
        launch {
            val toPerson = personRepository.getAuthenticatedTOPerson()!!
            val toConfig = schedulerConfigRepository.getTOSchedulerConfigByPersonId(toPerson.id!!)!!

            _uiState.value = _uiState.value.copy(
                toPerson = toPerson,
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
        launch {
            val validationResults = schedulerConfigUseCase.saveConfig(
                toPerson = _uiState.value.toPerson!!,
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
            _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(dialogValidations.message)
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