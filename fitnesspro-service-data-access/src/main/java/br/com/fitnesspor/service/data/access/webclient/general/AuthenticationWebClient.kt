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

    suspend fun authenticate(token: String, authenticationDTO: AuthenticationDTO): AuthenticationServiceResponse {
        return authenticationServiceErrorHandlingBlock(
            codeBlock = {
                authenticationService.authenticate(formatToken(token), authenticationDTO).getResponseBody()
            }
        )
    }

    suspend fun logout(token: String, authenticationDTO: AuthenticationDTO): AuthenticationServiceResponse {
        return authenticationServiceErrorHandlingBlock(
            codeBlock = {
                authenticationService.logout(formatToken(token), authenticationDTO).getResponseBody()
            }
        )
    }
}