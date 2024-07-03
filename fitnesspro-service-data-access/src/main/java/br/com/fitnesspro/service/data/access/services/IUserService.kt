package br.com.fitnesspro.service.data.access.services

import br.com.fitnesspro.service.data.access.dto.user.AcademyDTO
import br.com.fitnesspro.service.data.access.dto.user.AuthenticationDTO
import br.com.fitnesspro.service.data.access.dto.user.UserDTO
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Interface para acesso dos end points de manutenção de usuários.
 */
interface IUserService {

    /**
     * Função para chamar o endpoint de cadastro de usuário.
     *
     * Esta é uma chamada que não demanda autenticação.
     */
    @POST("users/")
    suspend fun register(@Body userDTO: UserDTO): Response<ResponseBody>

    @GET("academies/list")
    suspend fun getAcademies(): Response<List<AcademyDTO>>

    @POST("users/authenticate")
    suspend fun authenticate(@Body authenticationDTO: AuthenticationDTO): Response<ResponseBody>
}