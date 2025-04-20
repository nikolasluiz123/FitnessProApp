package br.com.fitnesspor.service.data.access.extensions

import br.com.fitnesspro.core.exceptions.ServiceException
import br.com.fitnesspro.core.extensions.defaultGSon
import br.com.fitnesspro.shared.communication.dtos.common.BaseDTO
import br.com.fitnesspro.shared.communication.responses.AuthenticationServiceResponse
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.FitnessProServiceResponse
import br.com.fitnesspro.shared.communication.responses.IFitnessProServiceResponse
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import br.com.fitnesspro.shared.communication.responses.ReadServiceResponse
import br.com.fitnesspro.shared.communication.responses.SingleValueServiceResponse
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import retrofit2.Response
import java.lang.reflect.Type
import javax.net.ssl.HttpsURLConnection

fun <DTO : BaseDTO> Response<PersistenceServiceResponse<DTO>>.getResponseBody(dtoType: Type): PersistenceServiceResponse<DTO> {
    val type = TypeToken.getParameterized(PersistenceServiceResponse::class.java, dtoType).type

    return getGenericResponse(
        type = type,
        convertAuthResponseToSpecificResponse = {
            PersistenceServiceResponse<DTO>(
                code = it.code,
                success = it.success,
                error = it.error,
                errorType = it.errorType
            )
        }
    )
}

fun Response<ExportationServiceResponse>.getResponseBody(): ExportationServiceResponse {
    val type = object : TypeToken<ExportationServiceResponse>() {}.type

    return getGenericResponse(
        type = type,
        convertAuthResponseToSpecificResponse = {
            ExportationServiceResponse(
                code = it.code,
                success = it.success,
                error = it.error,
                errorType = it.errorType,
                executionLogId = "",
                executionLogPackageId = ""
            )
        }
    )
}

fun Response<AuthenticationServiceResponse>.getResponseBody(): AuthenticationServiceResponse {
    val type = object : TypeToken<AuthenticationServiceResponse>() {}.type
    return getGenericResponse(type = type)
}

fun <DTO> Response<ReadServiceResponse<DTO>>.getResponseBody(dtoType: Type): ReadServiceResponse<DTO> {
    val type = TypeToken.getParameterized(ReadServiceResponse::class.java, dtoType).type

    return getGenericResponse(
        type = type,
        convertAuthResponseToSpecificResponse = {
            ReadServiceResponse(
                code = it.code,
                success = it.success,
                error = it.error,
                errorType = it.errorType,
            )
        }
    )
}

fun <DTO> Response<ImportationServiceResponse<DTO>>.getResponseBody(dtoType: Type): ImportationServiceResponse<DTO> {
    val type = TypeToken.getParameterized(ImportationServiceResponse::class.java, dtoType).type

    return getGenericResponse(
        type = type,
        convertAuthResponseToSpecificResponse = {
            ImportationServiceResponse(
                code = it.code,
                success = it.success,
                error = it.error,
                errorType = it.errorType,
                executionLogId = "",
                executionLogPackageId = ""
            )
        }
    )
}

fun <DTO> Response<SingleValueServiceResponse<DTO>>.getResponseBody(dtoType: Type): SingleValueServiceResponse<DTO> {
    val type = TypeToken.getParameterized(SingleValueServiceResponse::class.java, dtoType).type

    return getGenericResponse(
        type = type,
        convertAuthResponseToSpecificResponse = {
            SingleValueServiceResponse(
                code = it.code,
                success = it.success,
                error = it.error,
                errorType = it.errorType,
            )
        }
    )
}

fun Response<FitnessProServiceResponse>.getResponseBody(): FitnessProServiceResponse {
    val type = object : TypeToken<FitnessProServiceResponse>() {}.type

    return getGenericResponse(
        type = type,
        convertAuthResponseToSpecificResponse = {
            FitnessProServiceResponse(
                code = it.code,
                success = it.success,
                error = it.error,
                errorType = it.errorType,
            )
        }
    )
}

@Suppress("UNCHECKED_CAST")
private fun <R : IFitnessProServiceResponse> Response<R>.getGenericResponse(
    type: Type,
    convertAuthResponseToSpecificResponse: ((AuthenticationServiceResponse) -> R)? = null
): R {
    val defaultGSon = GsonBuilder().defaultGSon()

    return when {
        this.body() != null -> {
            this.body()!!
        }

        this.errorBody() != null -> {
            if (code() == HttpsURLConnection.HTTP_UNAUTHORIZED) {
                val authResponse = defaultGSon.fromJson(this.errorBody()!!.charStream(), AuthenticationServiceResponse::class.java)
                convertAuthResponseToSpecificResponse?.invoke(authResponse) ?: authResponse as R
            } else {
                defaultGSon.fromJson(this.errorBody()!!.charStream(), type)!!
            }
        }

        else -> {
            throw ServiceException("Ocorreu um erro ao recuperar o corpo da resposta")
        }
    }
}
