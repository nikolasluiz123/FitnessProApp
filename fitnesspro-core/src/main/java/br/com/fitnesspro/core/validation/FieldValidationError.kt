package br.com.fitnesspro.core.validation

import br.com.fitnesspro.core.enums.IEnumFieldValidation

open class FieldValidationError<FIELD>(
    val field: Enum<FIELD>?,
    message: String,
): ValidationError(message) where FIELD : Enum<FIELD>, FIELD : IEnumFieldValidation

open class ValidationError(
    val message: String,
)