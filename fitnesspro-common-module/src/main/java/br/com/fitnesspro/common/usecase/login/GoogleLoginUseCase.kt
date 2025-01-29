package br.com.fitnesspro.common.usecase.login

import android.content.Context
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOUser

class GoogleLoginUseCase(
    private val context: Context,
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(): GoogleAuthResult {
        val authResult = userRepository.signInWithGoogle() ?: return GoogleAuthResult(errorMessage = context.getString(R.string.login_google_unknown_error))

        val toPerson = TOPerson(
            name = authResult.user?.displayName,
            phone = authResult.user?.phoneNumber,
            toUser = TOUser(
                email = authResult.user?.email,
            )
        )

        val userExistsLocal = userRepository.hasUserWithEmail(authResult.user?.email!!, null)

        if (userExistsLocal) {
            val user = userRepository.findUserByEmail(authResult.user?.email!!)
            userRepository.authenticate(user?.email!!, user.password!!)
        }

        return GoogleAuthResult(
            success = true,
            userExists = userExistsLocal,
            toPerson = toPerson,
            errorMessage = null
        )
    }
}

data class GoogleAuthResult(
    val success: Boolean = false,
    val userExists: Boolean = false,
    val toPerson: TOPerson? = null,
    val errorMessage: String? = null
)