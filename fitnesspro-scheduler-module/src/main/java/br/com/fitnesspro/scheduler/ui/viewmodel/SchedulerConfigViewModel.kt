package br.com.fitnesspro.scheduler.ui.viewmodel

import android.content.Context
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.SchedulerConfigRepository
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.base.FitnessProStatefulViewModel
import br.com.fitnesspro.common.usecase.scheduler.SaveSchedulerConfigUseCase
import br.com.fitnesspro.common.usecase.scheduler.enums.EnumValidatedSchedulerConfigFields
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.scheduler.ui.state.SchedulerConfigUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SchedulerConfigViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val personRepository: PersonRepository,
    private val schedulerConfigRepository: SchedulerConfigRepository,
    private val schedulerConfigUseCase: SaveSchedulerConfigUseCase,
    private val globalEvents: GlobalEvents
) : FitnessProStatefulViewModel() {

    private val _uiState: MutableStateFlow<SchedulerConfigUIState> = MutableStateFlow(SchedulerConfigUIState())
    val uiState get() = _uiState.asStateFlow()

    init {
        initialLoadUIState()
        loadUIStateWithDatabaseInfos()
    }

    override fun initialLoadUIState() {
        _uiState.value = _uiState.value.copy(
            notification = createSwitchButtonFieldState(
                getCurrentState = { _uiState.value.notification },
                updateState = {
                    _uiState.value = _uiState.value.copy(
                        notification = it,
                        toConfig = _uiState.value.toConfig.copy(notification = it.checked)
                    )
                }
            ),
            notificationAntecedenceTime = createIntValueTextFieldState(
                getCurrentState = { _uiState.value.notificationAntecedenceTime },
                onValueChange = { state, value ->
                    _uiState.value = _uiState.value.copy(
                        toConfig = _uiState.value.toConfig.copy(notificationAntecedenceTime = value)
                    )
                }
            ),
            minEventDensity = createIntValueTextFieldState(
                getCurrentState = { _uiState.value.minEventDensity },
                onValueChange = { state, value ->
                    _uiState.value = _uiState.value.copy(
                        toConfig = _uiState.value.toConfig.copy(minScheduleDensity = value)
                    )
                }
            ),
            maxEventDensity = createIntValueTextFieldState(
                getCurrentState = { _uiState.value.maxEventDensity },
                onValueChange = { state, value ->
                    _uiState.value = _uiState.value.copy(
                        toConfig = _uiState.value.toConfig.copy(maxScheduleDensity = value)
                    )
                }
            ),
            messageDialogState = createMessageDialogState(
                getCurrentState = { _uiState.value.messageDialogState },
                updateState = { _uiState.value = _uiState.value.copy(messageDialogState = it) }
            ),
            onToggleLoading = {
                _uiState.value = _uiState.value.copy(
                    showLoading = _uiState.value.showLoading.not()
                )
            }
        )
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