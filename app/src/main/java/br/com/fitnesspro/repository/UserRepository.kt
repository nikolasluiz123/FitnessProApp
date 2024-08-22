package br.com.fitnesspro.repository

import android.content.Context
import br.com.fitnesspro.extensions.dataStore
import br.com.fitnesspro.extensions.getUserSession
import br.com.fitnesspro.extensions.saveSessionUser
import br.com.fitnesspro.model.Frequency
import br.com.fitnesspro.model.User
import br.com.fitnesspro.service.data.access.dto.user.AcademyDTO
import br.com.fitnesspro.service.data.access.webclients.UserWebClient
import br.com.fitnesspro.service.data.access.webclients.result.ResultList
import br.com.fitnesspro.service.data.access.webclients.result.SingleResult
import br.com.fitnesspro.service.data.access.webclients.result.ValidationResult

class UserRepository(
    private val context: Context,
    private val webClient: UserWebClient
) {

    /**
     * @see UserWebClient.saveUser
     */
    suspend fun saveUser(user: User): ValidationResult {
        return webClient.saveUser(user)
    }

    suspend fun getAcademies(): ResultList<AcademyDTO> {
        return webClient.getAcademies()
    }

    suspend fun authenticate(username: String, password: String): SingleResult<User> {
        val singleResult = webClient.authenticate(username, password)

        if(singleResult.validationResult is ValidationResult.Success) {
            val user = singleResult.data!!
            user.password = password

            context.dataStore.saveSessionUser(user)
        }

        return singleResult
    }

    suspend fun saveAcademyFrequency(frequency: Frequency): ValidationResult {
        val user = context.dataStore.getUserSession()!!
        frequency.username = user.username

        return webClient.saveAcademyFrequency(user.username, user.password, frequency)
    }

    suspend fun getAcademyFrequencies(): ResultList<Frequency> {
        val user = context.dataStore.getUserSession()!!

        return webClient.getAcademyFrequencies(user.username, user.password)
    }

}