package br.com.fitnesspro.service.data.access.webclients.extensions

import br.com.fitnesspro.service.data.access.dto.interfaces.IEnumDTOValidationFields
import br.com.fitnesspro.service.data.access.extensions.defaultGSon
import br.com.fitnesspro.service.data.access.response.ErrorDetails
import br.com.fitnesspro.service.data.access.webclients.result.ResultList
import br.com.fitnesspro.service.data.access.webclients.result.SingleResult
import br.com.fitnesspro.service.data.access.webclients.result.ValidationResult
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import okhttp3.ResponseBody
import retrofit2.Response
import kotlin.enums.EnumEntries

/**
 * Função que pode ser usada a partir de um [Response<ResponseBody>] para obter o [ValidationResult] que indica se a operação
 * com um DTO específico foi bem sucedida ou não.
 *
 * O uso principal dessa função é no momento de salvar alguma informação pois nesses casos o serviço sempre retorna os campos
 * que estão incorretos e uma mensagem de erro.
 */
fun <ENUM> Response<ResponseBody>.toValidationResult(enumEntries: EnumEntries<ENUM>): ValidationResult where ENUM : Enum<ENUM>, ENUM : IEnumDTOValidationFields {
    return if (this.isSuccessful) {
        ValidationResult.Success
    } else {
        val errorDetails = this.errorBody()!!.getErrorDetails()

        val nonFieldError = errorDetails.errors?.entries?.firstOrNull { it.key == "non_field_errors" }

        if (nonFieldError != null) {
            ValidationResult.Error(
                fieldErrors = emptyList(),
                message = nonFieldError.value.first(),
                details = errorDetails.details
            )
        } else {
            val fieldErrors = getFieldErrors(errorDetails, enumEntries)

            ValidationResult.Error(
                fieldErrors = fieldErrors,
                message = errorDetails.message,
                details = errorDetails.details
            )
        }
    }
}

private fun ResponseBody.getErrorDetails(): ErrorDetails {
    val gson = GsonBuilder().defaultGSon()
    val jsonElement = JsonParser.parseReader(this.charStream())

    return if (jsonElement.isJsonArray) {
        val jsonArray = jsonElement.getAsJsonArray()
        val firstJsonElement = jsonArray.get(0)

        gson.fromJson(firstJsonElement, ErrorDetails::class.java)
    } else {
        gson.fromJson(jsonElement, ErrorDetails::class.java)
    }
}

private fun <ENUM> getFieldErrors(
    errorDetails: ErrorDetails,
    enumEntries: EnumEntries<ENUM>
) where ENUM : Enum<ENUM>, ENUM : IEnumDTOValidationFields = errorDetails.errors?.map { map ->
    val enumKey = enumEntries.first { it.fieldName == map.key }
    val values = map.value.first()

    Pair(enumKey, values)
} ?: emptyList()

fun <T> Response<List<T>>.toResultList(): ResultList<T> {
    val gson = GsonBuilder().defaultGSon()

    return if (this.isSuccessful) {
        val list = this.body()!!
        ResultList(data = list)
    } else {
        val errorDetails = gson.fromJson(this.errorBody()!!.charStream(), ErrorDetails::class.java)
        ResultList(data = emptyList(), error = errorDetails)
    }
}

fun <T, ENUM> Response<ResponseBody>.toSingleResult(
    resultClass: Class<T>,
    enumEntries: EnumEntries<ENUM>
): SingleResult<T> where ENUM : Enum<ENUM>, ENUM : IEnumDTOValidationFields {
    val gson = GsonBuilder().defaultGSon()
    val validationResult = this.toValidationResult(enumEntries)

    return if (this.isSuccessful) {
        val data = gson.fromJson(this.body()!!.charStream(), resultClass)
        SingleResult(data = data, validationResult = validationResult)
    } else {
        SingleResult(
            data = null,
            validationResult = validationResult
        )
    }
}

const val TAG = "ResponseExtension"