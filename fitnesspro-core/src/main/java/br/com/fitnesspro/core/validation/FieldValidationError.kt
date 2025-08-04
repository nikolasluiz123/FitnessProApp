package br.com.fitnesspro.core.validation

import br.com.fitnesspro.core.enums.IEnumFieldValidation

class FieldValidationTypedError<FIELD, TYPE>(
    val type: Enum<TYPE>?,
    field: Enum<FIELD>?,
    message: String,
): FieldValidationError<FIELD>(field, message) where FIELD : Enum<FIELD>, TYPE : Enum<TYPE>, FIELD : IEnumFieldValidation

open class FieldValidationError<FIELD>(
    val field: Enum<FIELD>?,
    message: String,
): ValidationError(message) where FIELD : Enum<FIELD>, FIELD : IEnumFieldValidation

open class ValidationError(
    val message: String,
)