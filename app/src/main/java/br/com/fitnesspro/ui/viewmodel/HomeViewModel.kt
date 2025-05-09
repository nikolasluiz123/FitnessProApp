package br.com.fitnesspro.ui.viewmodel

import android.content.Context
import androidx.lifecycle.viewModelScope
import br.com.fitnesspro.R
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.FitnessProViewModel
import br.com.fitnesspro.core.callback.showConfirmationDialog
import br.com.fitnesspro.core.callback.showErrorDialog
import br.com.fitnesspro.core.state.MessageDialogState
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.ui.state.HomeUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository,
    private val personRepository: PersonRepository,
    private val globalEvents: GlobalEvents
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
                    messageDialogState = initializeMessageDialogState()
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

}