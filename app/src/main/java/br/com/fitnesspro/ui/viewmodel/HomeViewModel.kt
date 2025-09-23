package br.com.fitnesspro.ui.viewmodel

import android.content.Context
import androidx.lifecycle.viewModelScope
import br.com.fitnesspro.R
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.base.FitnessProViewModel
import br.com.fitnesspro.core.callback.showConfirmationDialog
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.callback.showInformationDialog
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.local.data.access.backup.DatabaseBackupExporter
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.ui.state.HomeUIState
import br.com.fitnesspro.usecase.FullManualImportationUseCase
import br.com.fitnesspro.workout.usecase.exercise.HealthConnectManualIntegrationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val userRepository: UserRepository,
    private val personRepository: PersonRepository,
    private val globalEvents: GlobalEvents,
    private val healthConnectManualIntegrationUseCase: HealthConnectManualIntegrationUseCase,
    private val fullManualImportationUseCase: FullManualImportationUseCase
) : FitnessProViewModel() {

    private val _uiState: MutableStateFlow<HomeUIState> = MutableStateFlow(HomeUIState())
    val uiState get() = _uiState.asStateFlow()

    init {
        initialUIStateLoad()
    }

    override fun getGlobalEventsBus(): GlobalEvents = globalEvents

    override fun onShowErrorDialog(message: String) {
        _uiState.value.messageDialogState.onShowDialog?.showErrorDialog(message = message)
    }

    override fun getErrorMessageFrom(throwable: Throwable): String {
        return context.getString(br.com.fitnesspro.common.R.string.unknown_error_message)
    }

    override fun onError(throwable: Throwable) {
        super.onError(throwable)

        if (_uiState.value.showLoading) {
            _uiState.value.onToggleLoading()
        }
    }

    private fun initialUIStateLoad() {
        viewModelScope.launch {
            val toPerson = personRepository.getAuthenticatedTOPerson()!!

            _uiState.update { currentState ->
                currentState.copy(
                    title = getTitle(toPerson),
                    subtitle = getSubtitle(toPerson),
                    toPerson = toPerson,
                    isEnabledSchedulerButton = true,
                    isEnabledWorkoutButton = true,
                    isEnabledNutritionButton = false,
                    isEnabledMoneyButton = false,
                    messageDialogState = initializeMessageDialogState(),
                    onToggleLoading = {
                        _uiState.value = _uiState.value.copy(showLoading = !_uiState.value.showLoading)
                    }
                )
            }
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

    fun logout(onSuccess: () -> Unit) {
        _uiState.value.messageDialogState.onShowDialog?.showConfirmationDialog(
            message = context.getString(R.string.home_screen_dialog_logout_message)
        ) {
            _uiState.value.onToggleLoading()

            launch {
                userRepository.logout()
                onSuccess()
            }
        }
    }

    private fun getTitle(toPerson: TOPerson): String {
        return when (toPerson.user?.type!!) {
            EnumUserType.PERSONAL_TRAINER -> context.getString(R.string.home_screen_title_personal_trainer)
            EnumUserType.NUTRITIONIST -> context.getString(R.string.home_screen_title_nutritionist)
            EnumUserType.ACADEMY_MEMBER -> context.getString(R.string.home_screen_title_academy_member)
        }
    }

    private fun getSubtitle(toPerson: TOPerson): String {
        return toPerson.name!!
    }

    fun onExecuteBackup(onSuccess: () -> Unit) {
        launch {
            val backupPath = DatabaseBackupExporter(context).export()
            onSuccess()

            _uiState.value.messageDialogState.onShowDialog?.showInformationDialog(
                message = context.getString(R.string.home_screen_dialog_backup_message, backupPath)
            )
        }
    }

    fun onExecuteHealthConnectIntegration() {
        _uiState.value.messageDialogState.onShowDialog?.showConfirmationDialog(
            message = context.getString(R.string.home_screen_dialog_health_connect_integration_message),
            onConfirm = {
                _uiState.value.onToggleLoading()

                launch {
                    healthConnectManualIntegrationUseCase()
                    _uiState.value.onToggleLoading()
                }
            }
        )
    }

    fun onExecuteFullManualImport() {
        _uiState.value.messageDialogState.onShowDialog?.showConfirmationDialog(
            message = context.getString(R.string.home_screen_dialog_full_manual_import_message),
            onConfirm = {
                _uiState.value.onToggleLoading()

                launch {
                    fullManualImportationUseCase()
                    _uiState.value.onToggleLoading()
                }
            }
        )
    }
}