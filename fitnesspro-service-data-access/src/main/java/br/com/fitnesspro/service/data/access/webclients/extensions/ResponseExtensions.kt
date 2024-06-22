package br.com.fitnesspro.service.data.access.webclients.extensions

import br.com.fitnesspro.service.data.access.dto.interfaces.IEnumDTOValidationFields
import br.com.fitnesspro.service.data.access.response.ErrorDetails
import br.com.fitnesspro.service.data.access.webclients.validation.ValidationResult
import com.google.gson.Gson
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
    val gson = Gson()

    return if (this.isSuccessful) {
        ValidationResult.Success
    } else {
        val errorDetails = gson.fromJson(this.errorBody()!!.charStream(), ErrorDetails::class.java)

        val fieldErrors = errorDetails.errors?.map { map ->
            val enumKey = enumEntries.first { it.fieldName == map.key }
            val values = map.value.first()

            Pair(enumKey, values)
        } ?: emptyList()

        ValidationResult.Error(fieldErrors = fieldErrors, message = errorDetails.message, details = errorDetails.details)
    }
}