package br.com.fitnesspro.core.validation

class FieldValidationTypedError<FIELD, TYPE>(
    val type: Enum<TYPE>?,
    field: Enum<FIELD>?,
    message: String,
): FieldValidationError<FIELD>(field, message) where FIELD : Enum<FIELD>, TYPE : Enum<TYPE>

open class FieldValidationError<FIELD>(
    val field: Enum<FIELD>?,
    message: String,
): ValidationError(message) where FIELD : Enum<FIELD>

open class ValidationError(
    val message: String,
)