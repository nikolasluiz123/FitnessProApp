package br.com.fitnesspro.common.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import br.com.fitnesspor.service.data.access.webclient.general.AuthenticationWebClient
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.core.extensions.PreferencesKey
import br.com.fitnesspro.core.extensions.dataStore
import br.com.fitnesspro.core.extensions.isNetworkAvailable
import br.com.fitnesspro.firebase.api.authentication.FirebaseDefaultAuthenticationService
import br.com.fitnesspro.firebase.api.authentication.FirebaseGoogleAuthenticationService
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.shared.communication.responses.AuthenticationServiceResponse
import br.com.fitnesspro.to.TOUser
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import javax.net.ssl.HttpsURLConnection.HTTP_NOT_FOUND

class UserRepository(
    context: Context,
    private val userDAO: UserDAO,
    private val firebaseDefaultAuthenticationService: FirebaseDefaultAuthenticationService,
    private val firebaseGoogleAuthenticationService: FirebaseGoogleAuthenticationService,
    private val authenticationWebClient: AuthenticationWebClient,
    private val personRepository: PersonRepository
): FitnessProRepository(context) {

    suspend fun hasUserWithEmail(email: String, userId: String?): Boolean = withContext(IO) {
        userDAO.hasUserWithEmail(email, userId)
    }

    suspend fun hasUserWithCredentials(email: String, password: String): Boolean = withContext(IO) {
        userDAO.hasUserWithCredentials(email, password)
    }

    suspend fun authenticate(email: String, password: String): Unit = withContext(IO) {
        saveUserIdDataStore(email)
        authenticateFirebase(email, password)
        authenticateRemote(email, password)
    }

    private suspend fun saveUserIdDataStore(email: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKey.USER] = userDAO.findByEmail(email)!!.id
        }
    }

    private suspend fun authenticateFirebase(email: String, password: String) {
        try {
            firebaseDefaultAuthenticationService.authenticate(email, password)
        } catch (ex: FirebaseAuthInvalidCredentialsException) {
            firebaseDefaultAuthenticationService.register(email, password)
            firebaseDefaultAuthenticationService.authenticate(email, password)
        }
    }

    private suspend fun authenticateRemote(email: String, password: String) {
        val response = authenticationWebClient.authenticate(email, password)

        if (response.success) {
            updateUserServiceToken(email, response)
        } else if (response.code == HTTP_NOT_FOUND && context.isNetworkAvailable()) {
            savePersonRemoteAndAuthenticateAgain(email, password)
        }
    }

    private suspend fun updateUserServiceToken(
        email: String,
        response: AuthenticationServiceResponse
    ) {
        userDAO.findByEmail(email)!!.also { user ->
            user.serviceToken = response.token!!
            userDAO.update(user)
        }
    }

    private suspend fun savePersonRemoteAndAuthenticateAgain(email: String, password: String) {
        val user = findUserByEmail(email)
        val person = personRepository.findPersonByUserId(user?.id!!)

        personRepository.savePersonRemote(person, user)

        authenticate(email, password)
    }

    suspend fun signInWithGoogle(): AuthResult? = withContext(IO) {
        firebaseGoogleAuthenticationService.signIn()
    }

    suspend fun getAuthenticatedTOUser(): TOUser? = withContext(IO) {
        getAuthenticatedUser()?.getTOUser()
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
                active = active,
                serviceToken = serviceToken,
            )
        }
    }

    suspend fun logout() = withContext(IO) {
        context.dataStore.edit {
            it.remove(PreferencesKey.USER)
        }

        firebaseDefaultAuthenticationService.logout()

        getAuthenticatedUser()?.let { user ->
            authenticationWebClient.logout(
                token = user.serviceToken!!,
                email = user.email!!,
                password = user.password!!
            )
        }
    }
}