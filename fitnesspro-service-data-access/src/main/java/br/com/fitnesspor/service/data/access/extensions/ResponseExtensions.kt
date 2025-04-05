package br.com.fitnesspor.service.data.access.extensions

import br.com.fitnesspro.core.extensions.defaultGSon
import br.com.fitnesspro.shared.communication.responses.AuthenticationServiceResponse
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.FitnessProServiceResponse
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import br.com.fitnesspro.shared.communication.responses.ReadServiceResponse
import br.com.fitnesspro.shared.communication.responses.SingleValueServiceResponse
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import retrofit2.Response
import java.lang.reflect.Type

fun Response<PersistenceServiceResponse>.getResponseBody(): PersistenceServiceResponse {
    val type = object : TypeToken<PersistenceServiceResponse>() {}.type
    return this.body() ?: GsonBuilder().defaultGSon().fromJson(this.errorBody()!!.charStream(), type)
}

fun Response<ExportationServiceResponse>.getResponseBody(): ExportationServiceResponse {
    val type = object : TypeToken<ExportationServiceResponse>() {}.type
    return this.body() ?: GsonBuilder().defaultGSon().fromJson(this.errorBody()!!.charStream(), type)
}

fun Response<AuthenticationServiceResponse>.getResponseBody(): AuthenticationServiceResponse {
    val type = object : TypeToken<AuthenticationServiceResponse>() {}.type
    return this.body() ?: GsonBuilder().defaultGSon().fromJson(this.errorBody()!!.charStream(), type)
}

fun <DTO> Response<ReadServiceResponse<DTO>>.getResponseBody(dtoType: Type): ReadServiceResponse<DTO> {
    val type = TypeToken.getParameterized(ReadServiceResponse::class.java, dtoType).type
    return this.body() ?: GsonBuilder().defaultGSon().fromJson(this.errorBody()!!.charStream(), type)
}

fun <DTO> Response<ImportationServiceResponse<DTO>>.getResponseBody(dtoType: Type): ImportationServiceResponse<DTO> {
    val type = TypeToken.getParameterized(ImportationServiceResponse::class.java, dtoType).type
    return this.body() ?: GsonBuilder().defaultGSon().fromJson(this.errorBody()!!.charStream(), type)
}

fun <DTO> Response<SingleValueServiceResponse<DTO>>.getResponseBody(dtoType: Type): SingleValueServiceResponse<DTO> {
    val type = TypeToken.getParameterized(SingleValueServiceResponse::class.java, dtoType).type
    return this.body() ?: GsonBuilder().defaultGSon().fromJson(this.errorBody()!!.charStream(), type)
}

fun Response<FitnessProServiceResponse>.getResponseBody(): FitnessProServiceResponse {
    val type = object : TypeToken<FitnessProServiceResponse>() {}.type
    return this.body() ?: GsonBuilder().defaultGSon().fromJson(this.errorBody()!!.charStream(), type)
}