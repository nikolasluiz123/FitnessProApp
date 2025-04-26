package br.com.fitnesspro.common.usecase.login

import android.content.Context
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOUser

class GoogleLoginUseCase(
    private val context: Context,
    private val userRepository: UserRepository,
    private val personRepository: PersonRepository
) {

    suspend operator fun invoke(authAgain: Boolean = false): GoogleAuthResult {
        val authResult = userRepository.signInWithGoogle() ?: return GoogleAuthResult(errorMessage = context.getString(R.string.login_google_unknown_error))

        val toPerson = TOPerson(
            name = authResult.user?.displayName,
            phone = authResult.user?.phoneNumber,
            user = TOUser(
                email = authResult.user?.email,
            )
        )

        val userExistsLocal = userRepository.hasUserWithEmail(authResult.user?.email!!, null)
        val toPersonRemote = personRepository.findPersonByEmailRemote(authResult.user?.email!!)

        val authUser = userRepository.getAuthenticatedUser()
        val isSameUser = authUser?.email == authResult.user?.email

        when {
            userExistsLocal -> {
                val user = userRepository.findUserByEmail(authResult.user?.email!!)
                userRepository.authenticate(user?.email!!, user.password!!)
            }

            toPersonRemote != null -> {
                personRepository.savePerson(
                    toPerson = toPersonRemote,
                    isRegisterServiceAuth = true,
                    forceInsertLocally = true
                )

                userRepository.authenticate(
                    email = toPersonRemote.user?.email!!,
                    toPersonRemote.user?.password!!
                )
            }

            authAgain && !isSameUser -> {
                GoogleAuthResult(errorMessage = context.getString(R.string.validation_msg_not_same_user_auth_again))
            }
        }

        return GoogleAuthResult(
            success = true,
            userExists = userExistsLocal || toPersonRemote != null,
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