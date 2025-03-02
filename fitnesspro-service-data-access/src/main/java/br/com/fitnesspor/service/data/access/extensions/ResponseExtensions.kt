package br.com.fitnesspor.service.data.access.extensions

import br.com.fitnesspro.core.extensions.defaultGSon
import br.com.fitnesspro.shared.communication.responses.AuthenticationServiceResponse
import br.com.fitnesspro.shared.communication.responses.FitnessProServiceResponse
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import br.com.fitnesspro.shared.communication.responses.ReadServiceResponse
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import retrofit2.Response

fun Response<PersistenceServiceResponse>.getResponseBody(): PersistenceServiceResponse {
    val type = object : TypeToken<PersistenceServiceResponse>() {}.type
    return this.body() ?: GsonBuilder().defaultGSon().fromJson(this.errorBody()!!.charStream(), type)
}

fun Response<AuthenticationServiceResponse>.getResponseBody(): AuthenticationServiceResponse {
    val type = object : TypeToken<AuthenticationServiceResponse>() {}.type
    return this.body() ?: GsonBuilder().defaultGSon().fromJson(this.errorBody()!!.charStream(), type)
}

fun <DTO> Response<ReadServiceResponse<DTO>>.getResponseBody(): ReadServiceResponse<DTO> {
    val type = object : TypeToken<ReadServiceResponse<DTO>>() {}.type
    return this.body() ?: GsonBuilder().defaultGSon().fromJson(this.errorBody()!!.charStream(), type)
}

fun Response<FitnessProServiceResponse>.getResponseBody(): FitnessProServiceResponse {
    val type = object : TypeToken<FitnessProServiceResponse>() {}.type
    return this.body() ?: GsonBuilder().defaultGSon().fromJson(this.errorBody()!!.charStream(), type)
}