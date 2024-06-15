package br.com.fitnesspro.service.data.access.webclients.extensions

import android.util.Log
import br.com.fitnesspro.service.data.access.dto.interfaces.IEnumDTOValidationFields
import br.com.fitnesspro.service.data.access.dto.user.UserDTOValidationFields
import br.com.fitnesspro.service.data.access.webclients.validation.ValidationResult
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Response
import kotlin.enums.EnumEntries

private const val TAG = "RESPONSE_DETAILS"

fun <ENUM> ResponseBody.toErrorsMap(enumEntries: EnumEntries<ENUM>): List<Pair<ENUM, String>> where ENUM : Enum<ENUM>, ENUM : IEnumDTOValidationFields {
    val gson = Gson()
    val type = object : TypeToken<Map<String, List<String>>>() {}.type
    val errorMap: Map<String, List<String>> = gson.fromJson(this.charStream(), type)

    Log.d(TAG, "Error Map: $errorMap")

    return errorMap.map { map ->
        val enumKey = enumEntries.first { it.name == map.key }
        val values = map.value.first()

        Pair(enumKey, values)
    }
}

fun <ENUM> Response<ResponseBody>.toValidationResult(enumEntries: EnumEntries<ENUM>): ValidationResult where ENUM : Enum<ENUM>, ENUM : IEnumDTOValidationFields {
    return if (isSuccessful) {
        ValidationResult.Success
    } else {
        ValidationResult.Error(errorBody()!!.toErrorsMap(enumEntries = enumEntries))
    }
}