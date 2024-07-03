package br.com.fitnesspro.service.data.access.webclients.result

class SingleResult<T>(
    val data: T?,
    val validationResult: ValidationResult,
)