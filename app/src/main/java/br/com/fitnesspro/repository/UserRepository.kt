package br.com.fitnesspro.repository

import android.content.Context
import br.com.fitnesspro.extensions.dataStore
import br.com.fitnesspro.extensions.saveSessionUser
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
     * @see UserWebClient.register
     */
    suspend fun register(user: User): ValidationResult {
        return webClient.register(user)
    }

    suspend fun getAcademies(): ResultList<AcademyDTO> {
        return webClient.getAcademies()
    }

    suspend fun authenticate(username: String, password: String): SingleResult<User> {
        val singleResult = webClient.authenticate(username, password)

        if(singleResult.validationResult is ValidationResult.Success) {
            context.dataStore.saveSessionUser(singleResult.data!!)
        }

        return singleResult
    }

}