package br.com.fitnesspro.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import br.com.fitnesspro.R
import br.com.fitnesspro.ui.state.ScheduleUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState: MutableStateFlow<ScheduleUIState> = MutableStateFlow(ScheduleUIState())
    val uiState get() = _uiState.asStateFlow()

    init {
        initialLoadUIState()
    }

    private fun initialLoadUIState() {
        _uiState.update {
            it.copy(
                title = context.getString(R.string.schedule_screen_title)
            )
        }
    }
}