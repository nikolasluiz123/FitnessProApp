package br.com.fitnesspro.core.validation

/**
 * Classe resultado das validações das entidades nos UseCases
 */
sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Error<T: Enum<*>>(val fieldErrors: Map<T, Int>) : ValidationResult()
}