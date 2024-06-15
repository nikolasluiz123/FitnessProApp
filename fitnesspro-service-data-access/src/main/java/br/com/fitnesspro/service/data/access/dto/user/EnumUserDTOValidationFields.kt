package br.com.fitnesspro.service.data.access.dto.user

import br.com.fitnesspro.service.data.access.dto.interfaces.IEnumDTOValidationFields

enum class EnumUserDTOValidationFields(
    override val fieldName: String,
    override val maxLength: Int
): IEnumDTOValidationFields {
    FIRST_NAME(fieldName = "first_name", maxLength = 150),
    LAST_NAME(fieldName = "last_name", maxLength = 150),
    EMAIL(fieldName = "email", maxLength = 254),
    PASSWORD(fieldName = "password", maxLength = 128),
    USERNAME(fieldName = "username", maxLength = 150)
}