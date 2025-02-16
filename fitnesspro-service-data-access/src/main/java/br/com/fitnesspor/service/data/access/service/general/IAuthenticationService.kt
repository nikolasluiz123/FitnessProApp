package br.com.fitnesspor.service.data.access.service.general

import br.com.fitnesspro.shared.communication.constants.EndPointsV1
import br.com.fitnesspro.shared.communication.dtos.general.AuthenticationDTO
import br.com.fitnesspro.shared.communication.responses.AuthenticationServiceResponse
import br.com.fitnesspro.shared.communication.responses.FitnessProServiceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface IAuthenticationService {

    @POST(EndPointsV1.AUTHENTICATION_LOGIN)
    suspend fun authenticate(@Body authenticationDTO: AuthenticationDTO): Response<AuthenticationServiceResponse>

    @POST(EndPointsV1.AUTHENTICATION_LOGOUT)
    suspend fun logout(
        @Header("Authorization") token: String,
        @Body authenticationDTO: AuthenticationDTO
    ): Response<FitnessProServiceResponse>
}