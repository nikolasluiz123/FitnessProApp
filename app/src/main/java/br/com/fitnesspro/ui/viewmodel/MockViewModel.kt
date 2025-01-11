package br.com.fitnesspro.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fitnesspro.common.mock.PersonMockHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MockViewModel @Inject constructor(
    private val personMockHelper: PersonMockHelper
): ViewModel() {

    fun executePersonMock() {
        viewModelScope.launch {
            personMockHelper.executeInsertsPersonMock()
        }
    }
}