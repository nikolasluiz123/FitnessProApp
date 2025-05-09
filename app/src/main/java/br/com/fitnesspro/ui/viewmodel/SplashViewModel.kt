package br.com.fitnesspro.ui.viewmodel

import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.ui.event.GlobalEvents
import br.com.fitnesspro.common.ui.viewmodel.FitnessProViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val globalEvents: GlobalEvents
): FitnessProViewModel() {

    fun verifyNavigationDestination(onNavigateToLogin: () -> Unit, onNavigateToRoomList: () -> Unit) {
        launch {
            val authUser = userRepository.getAuthenticatedUser()

            if (authUser != null) {
                onNavigateToRoomList()
            } else {
                onNavigateToLogin()
            }
        }
    }

    override fun getGlobalEventsBus(): GlobalEvents = globalEvents

    override fun getErrorMessageFrom(throwable: Throwable): String = ""

    override fun onShowErrorDialog(message: String) = Unit
}