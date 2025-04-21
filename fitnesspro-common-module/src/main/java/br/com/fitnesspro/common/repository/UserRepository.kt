package br.com.fitnesspro.common.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import br.com.fitnesspor.service.data.access.webclient.general.AuthenticationWebClient
import br.com.fitnesspro.common.BuildConfig
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.core.extensions.PreferencesKey
import br.com.fitnesspro.core.extensions.dataStore
import br.com.fitnesspro.core.extensions.isNetworkAvailable
import br.com.fitnesspro.firebase.api.authentication.FirebaseDefaultAuthenticationService
import br.com.fitnesspro.firebase.api.authentication.FirebaseGoogleAuthenticationService
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.mappers.PersonModelMapper
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.shared.communication.dtos.general.AuthenticationDTO
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
    private val personRepository: PersonRepository,
    private val personModelMapper: PersonModelMapper,
    private val deviceRepository: DeviceRepository,
    private val serviceTokenRepository: ServiceTokenRepository
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
        runInTransaction {
            val authenticationDTO = getAuthenticationDTO(email, password)

            val response = authenticationWebClient.authenticate(
                token = getValidToken(),
                authenticationDTO = authenticationDTO
            )

            if (response.success) {
                serviceTokenRepository.saveTokenInformation(response.tokens)
            } else if (response.code == HTTP_NOT_FOUND && context.isNetworkAvailable()) {
                savePersonRemoteAndAuthenticateAgain(email, password)
            }
        }
    }

    private suspend fun getAuthenticationDTO(email: String, password: String): AuthenticationDTO {
        val deviceDTO = deviceRepository.getDeviceDTO()

        val authenticationDTO = AuthenticationDTO(
            email = email,
            password = password,
            deviceDTO = deviceDTO,
            applicationJWT = BuildConfig.APP_JWT
        )
        return authenticationDTO
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
        getAuthenticatedUser()?.let(personModelMapper::getTOUser)
    }

    suspend fun findUserById(userId: String): User? = withContext(IO) {
        userDAO.findById(userId)
    }

    suspend fun findUserByEmail(email: String): User? = withContext(IO) {
        userDAO.findByEmail(email)
    }

    suspend fun logout() = withContext(IO) {
        context.dataStore.edit {
            it.remove(PreferencesKey.USER)
        }

        firebaseDefaultAuthenticationService.logout()

        getAuthenticatedUser()?.let { user ->
            val response = authenticationWebClient.logout(
                token = getValidToken(),
                authenticationDTO = getAuthenticationDTO(user.email!!, user.password!!)
            )

            if (response.success) {
                serviceTokenRepository.saveTokenInformation(response.tokens)
            }
        }
    }
}