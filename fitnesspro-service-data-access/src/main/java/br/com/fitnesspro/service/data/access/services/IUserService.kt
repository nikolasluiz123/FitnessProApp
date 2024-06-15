package br.com.fitnesspro.service.data.access.services

import br.com.fitnesspro.service.data.access.dto.user.UserDTO
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface IUserService {

    @POST("users/")
    suspend fun register(@Body userDTO: UserDTO): Response<ResponseBody>
}