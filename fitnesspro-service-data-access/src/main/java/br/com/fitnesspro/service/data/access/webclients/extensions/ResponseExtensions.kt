package br.com.fitnesspro.service.data.access.webclients.extensions

import android.util.Log
import br.com.fitnesspro.service.data.access.dto.interfaces.IEnumDTOValidationFields
import br.com.fitnesspro.service.data.access.webclients.validation.ValidationResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Response
import kotlin.enums.EnumEntries

private const val TAG = "RESPONSE_DETAILS"

/**
 * Função que pode ser usada a partir de um [ResponseBody] para obter a lista de erros de validação dos campos
 * do DTO que foi enviado ao serviço.
 *
 * Os campos que são validados devem estar contidos em um enum que implemente [IEnumDTOValidationFields]
 */
fun <ENUM> ResponseBody.toErrorsList(enumEntries: EnumEntries<ENUM>): List<Pair<ENUM, String>> where ENUM : Enum<ENUM>, ENUM : IEnumDTOValidationFields {
    val gson = Gson()
    val type = object : TypeToken<Map<String, List<String>>>() {}.type
    val errorMap: Map<String, List<String>> = gson.fromJson(this.charStream(), type)

    Log.d(TAG, "Error Map: $errorMap")

    return errorMap.map { map ->
        val enumKey = enumEntries.first { it.fieldName == map.key }
        val values = map.value.first()

        Pair(enumKey, values)
    }
}

/**
 * Função que pode ser usada a partir de um [Response<ResponseBody>] para obter o [ValidationResult] que indica se a operação
 * com um DTO específico foi bem sucedida ou não.
 *
 * O uso principal dessa função é no momento de salvar alguma informação pois nesses casos o serviço sempre retorna os campos
 * que estão incorretos e uma mensagem de erro.
 */
fun <ENUM> Response<ResponseBody>.toValidationResult(enumEntries: EnumEntries<ENUM>): ValidationResult where ENUM : Enum<ENUM>, ENUM : IEnumDTOValidationFields {
    return if (isSuccessful) {
        ValidationResult.Success
    } else {
        ValidationResult.Error(errorBody()!!.toErrorsList(enumEntries = enumEntries))
    }
}