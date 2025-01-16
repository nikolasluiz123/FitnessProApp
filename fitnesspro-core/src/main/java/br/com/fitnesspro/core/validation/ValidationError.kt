package br.com.fitnesspro.core.validation

class ValidationError<FIELD, VALIDATION>(
    val field: Enum<FIELD>?,
    val validationType: Enum<VALIDATION>,
    val message: String,
) where FIELD : Enum<FIELD>, VALIDATION : Enum<VALIDATION>

fun List<ValidationError<*, *>>.getValidations() = this.map { it.validationType }