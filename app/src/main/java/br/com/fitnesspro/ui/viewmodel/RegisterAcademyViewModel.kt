package br.com.fitnesspro.ui.viewmodel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import br.com.fitnesspro.core.extensions.fromJsonNavParamToArgs
import br.com.fitnesspro.repository.UserRepository
import br.com.fitnesspro.ui.navigation.RegisterAcademyScreenArgs
import br.com.fitnesspro.ui.navigation.registerAcademyArguments
import br.com.fitnesspro.ui.state.RegisterAcademyUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RegisterAcademyViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState: MutableStateFlow<RegisterAcademyUIState> = MutableStateFlow(RegisterAcademyUIState())
    val uiState get() = _uiState.asStateFlow()

    private val jsonArgs: String? = savedStateHandle[registerAcademyArguments]

    init {
        jsonArgs?.fromJsonNavParamToArgs(RegisterAcademyScreenArgs::class.java)?.let { args ->
        }
    }

}