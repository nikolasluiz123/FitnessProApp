package br.com.fitnesspro.repository

import br.com.fitnesspro.model.User
import br.com.fitnesspro.service.data.access.webclients.UserWebClient
import br.com.fitnesspro.service.data.access.webclients.validation.ValidationResult

class UserRepository(
    private val webClient: UserWebClient
) {

    /**
     * @see UserWebClient.register
     */
    suspend fun register(user: User): ValidationResult {
        return webClient.register(user)
    }

}