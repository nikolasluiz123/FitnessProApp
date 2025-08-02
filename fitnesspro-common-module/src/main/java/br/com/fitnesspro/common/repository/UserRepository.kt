package br.com.fitnesspro.common.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import br.com.fitnesspor.service.data.access.webclient.general.AuthenticationWebClient
import br.com.fitnesspro.common.BuildConfig
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.core.exceptions.ServiceException
import br.com.fitnesspro.core.extensions.PreferencesKey
import br.com.fitnesspro.core.extensions.dataStore
import br.com.fitnesspro.core.extensions.isNetworkAvailable
import br.com.fitnesspro.firebase.api.authentication.FirebaseDefaultAuthenticationService
import br.com.fitnesspro.firebase.api.authentication.FirebaseGoogleAuthenticationService
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.mappers.getTOUser
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.shared.communication.dtos.general.AuthenticationDTO
import br.com.fitnesspro.to.TOUser
import com.google.firebase.FirebaseNetworkException
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
    private val personRepository: PersonRepository,
    private val deviceRepository: DeviceRepository,
    private val serviceTokenRepository: ServiceTokenRepository
): FitnessProRepository(context) {

    suspend fun hasUserWithEmail(email: String, userId: String?): Boolean {
        return userDAO.hasUserWithEmail(email, userId)
    }

    suspend fun hasUserWithCredentials(email: String, password: String): Boolean {
        return userDAO.hasUserWithCredentials(email, password)
    }

    suspend fun authenticate(email: String, password: String) {
        runInTransaction {
            saveUserIdDataStore(email)
            authenticateRemote(email, password)
            authenticateFirebase(email, password)
        }
    }

    private suspend fun saveUserIdDataStore(email: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKey.USER] = userDAO.findByEmail(email)!!.id
        }
    }

    private suspend fun authenticateFirebase(email: String, password: String) {
        try {
            firebaseDefaultAuthenticationService.authenticate(email, password)
        } catch (_: FirebaseAuthInvalidCredentialsException) {
            firebaseDefaultAuthenticationService.register(email, password)
            firebaseDefaultAuthenticationService.authenticate(email, password)
        } catch (_: FirebaseNetworkException) {
            // Nao realizamos nenhuma acao pois o app deve funcionar offline
        }
    }

    private suspend fun authenticateRemote(email: String, password: String) {
        val authenticationDTO = getAuthenticationDTO(email, password)

        val response = authenticationWebClient.authenticate(
            token = getValidToken(withoutAuthentication = true),
            authenticationDTO = authenticationDTO
        )

        val userExistsLocally = userDAO.hasUserWithEmailAndPassword(email, password)

        if (response.success) {
            serviceTokenRepository.saveTokenInformation(response.tokens)
        } else if (response.code == HTTP_NOT_FOUND && context.isNetworkAvailable() && userExistsLocally) {
            savePersonRemoteAndAuthenticateAgain(email, password)
        } else {
            throw ServiceException(response.error!!)
        }
    }

    private suspend fun getAuthenticationDTO(email: String, password: String): AuthenticationDTO {
        return AuthenticationDTO(
            email = email,
            password = password,
            deviceDTO = deviceRepository.getDeviceDTO(),
            applicationJWT = BuildConfig.APP_JWT
        )
    }

    private suspend fun savePersonRemoteAndAuthenticateAgain(email: String, password: String) {
        val user = findUserByEmail(email)
        val person = personRepository.findPersonByUserId(user?.id!!)

        val success = personRepository.savePersonRemote(person, user)

        if (success) {
            authenticate(email, password)
        }
    }

    suspend fun signInWithGoogle(): AuthResult? {
        return firebaseGoogleAuthenticationService.signIn()
    }

    suspend fun getAuthenticatedTOUser(): TOUser? = withContext(IO) {
        getAuthenticatedUser()?.let(User::getTOUser)
    }

    suspend fun findUserById(userId: String): User? {
        return userDAO.findById(userId)
    }

    suspend fun findUserByEmail(email: String): User? {
        return userDAO.findByEmail(email)
    }

    suspend fun logout() = withContext(IO) {
        val user = getAuthenticatedUser()!!
        val response = authenticationWebClient.logout(
            token = getValidToken(withoutAuthentication = true),
            authenticationDTO = getAuthenticationDTO(user.email!!, user.password!!)
        )

        if (response.success) {
            serviceTokenRepository.saveTokenInformation(response.tokens)
        }

        context.dataStore.edit {
            it.remove(PreferencesKey.USER)
        }

        firebaseDefaultAuthenticationService.logout()
    }
}