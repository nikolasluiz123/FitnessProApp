package br.com.fitnesspro.core.validation

class FieldValidationError<FIELD, VALIDATION>(
    val field: Enum<FIELD>?,
    validationType: Enum<VALIDATION>,
    message: String,
): ValidationError<VALIDATION>(validationType, message) where FIELD : Enum<FIELD>, VALIDATION : Enum<VALIDATION>

fun List<FieldValidationError<*, *>>.getValidations() = this.map { it.validationType }

open class ValidationError<VALIDATION>(
    val validationType: Enum<VALIDATION>,
    val message: String,
) where VALIDATION : Enum<VALIDATION>