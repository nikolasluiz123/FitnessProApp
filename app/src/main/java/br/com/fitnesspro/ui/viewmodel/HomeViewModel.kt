package br.com.fitnesspro.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fitnesspro.R
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.repository.UserRepository
import br.com.fitnesspro.ui.screen.registeruser.to.TOPerson
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
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<HomeUIState> = MutableStateFlow(HomeUIState())
    val uiState get() = _uiState.asStateFlow()

    init {
        initialUIStateLoad()
    }

    private fun initialUIStateLoad() {
        viewModelScope.launch {
            val toPerson = userRepository.getAuthenticatedTOPerson()

            _uiState.update { currentState ->
                currentState.copy(
                    title = getTitle(toPerson),
                    subtitle = getSubtitle(toPerson),
                    toPerson = toPerson,
                    isEnabledSchedulerButton = false,
                    isEnabledWorkoutButton = true,
                    isEnabledNutritionButton = false,
                    isEnabledMoneyButton = false,
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
                )
            }
        }
    }

    private fun getTitle(toPerson: TOPerson): String {
        return when (toPerson.toUser?.type!!) {
            EnumUserType.PERSONAL_TRAINER -> context.getString(R.string.home_screen_title_personal_trainer)
            EnumUserType.NUTRITIONIST -> context.getString(R.string.home_screen_title_nutritionist)
            EnumUserType.ACADEMY_MEMBER -> context.getString(R.string.home_screen_title_academy_member)
        }
    }

    private fun getSubtitle(toPerson: TOPerson): String {
        return toPerson.name!!
    }

}