package br.com.fitnesspor.service.data.access.service.general

import br.com.fitnesspro.shared.communication.constants.EndPointsV1.AUTHENTICATION
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.AUTHENTICATION_LOGIN
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.AUTHENTICATION_LOGOUT
import br.com.fitnesspro.shared.communication.dtos.general.AuthenticationDTO
import br.com.fitnesspro.shared.communication.responses.AuthenticationServiceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface IAuthenticationService {

    @POST("$AUTHENTICATION$AUTHENTICATION_LOGIN")
    suspend fun authenticate(
        @Header("Authorization") token: String,
        @Body authenticationDTO: AuthenticationDTO
    ): Response<AuthenticationServiceResponse>

    @POST("$AUTHENTICATION$AUTHENTICATION_LOGOUT")
    suspend fun logout(
        @Header("Authorization") token: String,
        @Body authenticationDTO: AuthenticationDTO
    ): Response<AuthenticationServiceResponse>
}