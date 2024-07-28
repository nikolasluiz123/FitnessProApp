package br.com.fitnesspro.service.data.access.dto.interfaces

/**
 * Interface contendo os campos necessários nos enumeradores de validação dos DTOs.
 */
interface IEnumDTOValidationFields {
    val fieldName: String
    val maxLength: Int?
}