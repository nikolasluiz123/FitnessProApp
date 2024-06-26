package br.com.fitnesspro.service.data.access.webclients.validation

import br.com.fitnesspro.service.data.access.dto.interfaces.IEnumDTOValidationFields

sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Error<ENUM>(
        val fieldErrors: List<Pair<ENUM, String>> = emptyList(),
        val message: String? = null,
        val details: String? = null
    ) : ValidationResult() where ENUM : Enum<ENUM>, ENUM : IEnumDTOValidationFields
}