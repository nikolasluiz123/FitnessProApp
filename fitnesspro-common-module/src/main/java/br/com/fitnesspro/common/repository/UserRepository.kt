package br.com.fitnesspro.common.repository

import br.com.fitnesspro.firebase.api.authentication.DefaultAuthenticationService
import br.com.fitnesspro.firebase.api.authentication.GoogleAuthenticationService
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.to.TOUser
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class UserRepository(
    private val userDAO: UserDAO,
    private val defaultAuthenticationService: DefaultAuthenticationService,
    private val googleAuthenticationService: GoogleAuthenticationService
) {

    suspend fun hasUserWithEmail(email: String, userId: String?): Boolean = withContext(IO) {
        userDAO.hasUserWithEmail(email, userId)
    }

    suspend fun hasUserWithCredentials(email: String, password: String): Boolean = withContext(IO) {
        userDAO.hasUserWithCredentials(email, password)
    }

    suspend fun authenticate(email: String, password: String): Unit = withContext(IO) {
        userDAO.authenticate(email, password)

        try {
            defaultAuthenticationService.authenticate(email, password)
        } catch (ex: FirebaseAuthInvalidCredentialsException) {
            defaultAuthenticationService.register(email, password)
            defaultAuthenticationService.authenticate(email, password)
        }
    }

    suspend fun signInWithGoogle(): AuthResult? = withContext(IO) {
        googleAuthenticationService.signIn()
    }

    suspend fun getAuthenticatedTOUser(): TOUser? = withContext(IO) {
        userDAO.getAuthenticatedUser()?.getTOUser()
    }

    suspend fun findUserById(userId: String): User? = withContext(IO) {
        userDAO.findById(userId)
    }

    suspend fun findUserByEmail(email: String): User? = withContext(IO) {
        userDAO.findByEmail(email)
    }

    private fun User?.getTOUser(): TOUser? {
        return this?.run {
            TOUser(
                id = id,
                email = email,
                password = password,
                type = type,
                active = active
            )
        }
    }

    suspend fun logout() = withContext(IO) {
        userDAO.logoutAll()
        defaultAuthenticationService.logout()
    }
}