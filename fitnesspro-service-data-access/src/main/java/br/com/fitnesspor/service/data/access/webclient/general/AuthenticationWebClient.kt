package br.com.fitnesspor.service.data.access.webclient.general

import android.content.Context
import br.com.fitnesspor.service.data.access.extensions.getResponseBody
import br.com.fitnesspor.service.data.access.service.general.IAuthenticationService
import br.com.fitnesspor.service.data.access.webclient.common.FitnessProWebClient
import br.com.fitnesspro.shared.communication.dtos.general.AuthenticationDTO
import br.com.fitnesspro.shared.communication.responses.AuthenticationServiceResponse

class AuthenticationWebClient(
    context: Context,
    private val authenticationService: IAuthenticationService
): FitnessProWebClient(context) {

    suspend fun authenticate(email: String, password: String): AuthenticationServiceResponse {
        val authenticationDTO = AuthenticationDTO(
            email = email,
            password = password
        )

        val response = authenticationServiceErrorHandlingBlock(
            codeBlock = {
                authenticationService.authenticate(authenticationDTO).getResponseBody()
            }
        )

        return response
    }

    suspend fun logout(token: String, email: String, password: String) {
        val authenticationDTO = AuthenticationDTO(
            email = email,
            password = password
        )

        serviceErrorHandlingBlock(
            codeBlock = {
                authenticationService.logout(token, authenticationDTO).getResponseBody()
            }
        )
    }
}