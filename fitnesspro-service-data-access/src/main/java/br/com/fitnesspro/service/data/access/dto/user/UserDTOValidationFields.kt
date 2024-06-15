package br.com.fitnesspro.service.data.access.dto.user

import br.com.fitnesspro.service.data.access.dto.interfaces.IEnumDTOValidationFields

enum class UserDTOValidationFields(override val fieldName: String): IEnumDTOValidationFields {
    FIRST_NAME(fieldName = "firstName"),
    LAST_NAME(fieldName = "lastName"),
    EMAIL(fieldName = "email"),
    PASSWORD(fieldName = "password"),
    USERNAME(fieldName = "username")
}