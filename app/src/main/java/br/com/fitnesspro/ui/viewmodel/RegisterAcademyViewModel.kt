package br.com.fitnesspro.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fitnesspro.R
import br.com.fitnesspro.compose.components.menu.MenuItem
import br.com.fitnesspro.compose.components.state.Field
import br.com.fitnesspro.core.enums.EnumDialogType
import br.com.fitnesspro.repository.UserRepository
import br.com.fitnesspro.ui.state.RegisterAcademyUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterAcademyViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<RegisterAcademyUIState> = MutableStateFlow(RegisterAcademyUIState())
    val uiState get() = _uiState.asStateFlow()

    init {
        initialLoadUIState()
        loadAcademies()
    }

    private fun loadAcademies() {
        viewModelScope.launch {
            val result = userRepository.getAcademies()

            if (result.data.isNotEmpty()) {
                val items = result.data.map { MenuItem(label = it.name, value = it.id) }
                _uiState.value = _uiState.value.copy(academies = items, filteredAcademies = items)
            } else {
                _uiState.value.onShowDialog?.onShow(
                    type = EnumDialogType.ERROR,
                    message = result.error?.message!!,
                    onConfirm = { },
                    onCancel = { }
                )

                Log.e(TAG, result.error?.details ?: "")
            }
        }
    }

    private fun initialLoadUIState() {
        val dayWeeks = getDayWeeks()

        _uiState.update { currentState ->
            currentState.copy(
                dayWeeks = dayWeeks,
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
                        ),
                        filteredAcademies = _uiState.value.academies.filter { menuItem ->
                            menuItem.label.contains(text, ignoreCase = true)
                        }
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
                    _uiState.value = _uiState.value.copy(
                        start = _uiState.value.start.copy(
                            value = it,
                            errorMessage = ""
                        )
                    )
                }),
                end = Field(onChange = {
                    _uiState.value = _uiState.value.copy(
                        end = _uiState.value.end.copy(
                            value = it,
                            errorMessage = ""
                        )
                    )
                })
            )
        }
    }

    private fun getDayWeeks(): MutableList<MenuItem<String>> {
        return mutableListOf(
            MenuItem(
                label = context.getString(R.string.register_user_screen_day_week_monday),
                value = "SEG"
            ),
            MenuItem(
                label = context.getString(R.string.register_user_screen_day_week_tuesday),
                value = "TER"
            ),
            MenuItem(
                label = context.getString(R.string.register_user_screen_day_week_wednesday),
                value = "QUA"
            ),
            MenuItem(
                label = context.getString(R.string.register_user_screen_day_week_thursday),
                value = "QUI"
            ),
            MenuItem(
                label = context.getString(R.string.register_user_screen_day_week_friday),
                value = "SEX"
            ),
            MenuItem(
                label = context.getString(R.string.register_user_screen_day_week_saturday),
                value = "SAB"
            ),
            MenuItem(
                label = context.getString(R.string.register_user_screen_day_week_sunday),
                value = "DOM"
            )
        )
    }

    companion object {
        private const val TAG = "RegisterAcademyViewModel"
    }
}