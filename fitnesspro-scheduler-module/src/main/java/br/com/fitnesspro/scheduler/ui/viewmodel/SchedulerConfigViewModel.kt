package br.com.fitnesspro.scheduler.ui.viewmodel

import android.content.Context
import androidx.core.text.isDigitsOnly
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.SchedulerConfigRepository
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.FitnessProViewModel
import br.com.fitnesspro.common.usecase.scheduler.SaveSchedulerConfigUseCase
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
    private val schedulerConfigUseCase: SaveSchedulerConfigUseCase,
    private val globalEvents: GlobalEvents
) : FitnessProViewModel() {

    private val _uiState: MutableStateFlow<SchedulerConfigUIState> = MutableStateFlow(SchedulerConfigUIState())
    val uiState get() = _uiState.asStateFlow()

    init {
        initialUIStateLoad()
        loadUIStateWithDatabaseInfos()
    }

    override fun getGlobalEventsBus(): GlobalEvents = globalEvents

    override fun onShowErrorDialog(message: String) {
        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(message = message)
    }

    override fun getErrorMessageFrom(throwable: Throwable): String {
        return context.getString(R.string.unknown_error_message)
    }

    override fun onError(throwable: Throwable) {
        super.onError(throwable)

        if (_uiState.value.showLoading) {
            _uiState.value.onToggleLoading()
        }
    }

    private fun initialUIStateLoad() {
        _uiState.update { state ->
            state.copy(
                notification = initializeNotificationSwitchField(),
                notificationAntecedenceTime = initializeNotificationAntecedenceTextField(),
                minEventDensity = initializeMinEventDensityTextField(),
                maxEventDensity = initializeMaxEventDensityTextField(),
                messageDialogState = initializeMessageDialogState(),
                onToggleLoading = {
                    _uiState.value = _uiState.value.copy(
                        showLoading = _uiState.value.showLoading.not()
                    )
                }
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
                notification = _uiState.value.notification.copy(
                    checked = toConfig.notification
                ),
                notificationAntecedenceTime = _uiState.value.notificationAntecedenceTime.copy(
                    value = toConfig.notificationAntecedenceTime.toString()
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

    private fun initializeNotificationAntecedenceTextField(): TextField {
        return TextField(
            onChange = { value ->
                if (value.isDigitsOnly()) {
                    _uiState.value = _uiState.value.copy(
                        notificationAntecedenceTime = _uiState.value.notificationAntecedenceTime.copy(value = value),
                        toConfig = _uiState.value.toConfig.copy(notificationAntecedenceTime = value.toIntOrNull())
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

    fun save(onSuccess: () -> Unit) {
        launch {
            val validationResults = schedulerConfigUseCase.saveConfig(
                toPerson = _uiState.value.toPerson!!,
                toSchedulerConfig = _uiState.value.toConfig
            )

            if (validationResults.isEmpty()) {
                onSuccess()
            } else {
                uiState.value.onToggleLoading()
                showValidationMessage(validationResults)
            }
        }
    }

    private fun showValidationMessage(validationResults: MutableList<FieldValidationError<EnumValidatedSchedulerConfigFields>>) {
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

                EnumValidatedSchedulerConfigFields.NOTIFICATION_ANTECEDENCE_TIME -> {
                    _uiState.value = _uiState.value.copy(
                        notificationAntecedenceTime = _uiState.value.notificationAntecedenceTime.copy(
                            errorMessage = it.message
                        )
                    )
                }
            }
        }
    }
}