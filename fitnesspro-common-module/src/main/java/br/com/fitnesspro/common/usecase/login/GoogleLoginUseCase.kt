package br.com.fitnesspro.common.usecase.login

import android.content.Context
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.core.extensions.isNetworkAvailable
import br.com.fitnesspro.mappers.getTOPerson
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOUser
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class GoogleLoginUseCase(
    private val context: Context,
    private val userRepository: UserRepository,
    private val personRepository: PersonRepository
) {

    suspend operator fun invoke(authAgain: Boolean = false): GoogleAuthResult? = withContext(IO) {
        return@withContext userRepository.signInWithGoogle()?.let { authResult ->
            val toPerson = TOPerson(
                name = authResult.user?.displayName,
                phone = authResult.user?.phoneNumber,
                user = TOUser(
                    email = authResult.user?.email,
                )
            )

            val networkAvailable = context.isNetworkAvailable()
            val userExistsLocally = userRepository.hasUserWithEmail(authResult.user?.email!!, null)

            if (!networkAvailable && !userExistsLocally) {
                return@let GoogleAuthResult(errorMessage = context.getString(R.string.validation_msg_network_required_login))
            }

            val findPersonRemoteResponse = personRepository.findPersonByEmailRemote(
                email = authResult.user?.email!!,
                password = null
            )

            if (!userExistsLocally && !findPersonRemoteResponse.success) {
                return@let GoogleAuthResult(errorMessage = findPersonRemoteResponse.error!!)
            }

            val toPersonRemote = findPersonRemoteResponse.value?.getTOPerson()
            val authUser = userRepository.getAuthenticatedUser()
            val isSameUser = authUser?.email == authResult.user?.email

            when {
                userExistsLocally -> {
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
                    return@let GoogleAuthResult(errorMessage = context.getString(R.string.validation_msg_not_same_user_auth_again))
                }
            }

            return@let GoogleAuthResult(
                success = true,
                userExists = userExistsLocally || toPersonRemote != null,
                toPerson = toPerson,
                errorMessage = null
            )
        }
    }
}

data class GoogleAuthResult(
    val success: Boolean = false,
    val userExists: Boolean = false,
    val toPerson: TOPerson? = null,
    val errorMessage: String? = null
)