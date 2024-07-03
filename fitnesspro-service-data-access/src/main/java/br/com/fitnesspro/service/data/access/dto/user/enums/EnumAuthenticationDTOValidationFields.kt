package br.com.fitnesspro.service.data.access.dto.user.enums

import br.com.fitnesspro.service.data.access.dto.interfaces.IEnumDTOValidationFields

enum class EnumAuthenticationDTOValidationFields(
    override val fieldName: String,
    override val maxLength: Int
) : IEnumDTOValidationFields {
    PASSWORD(
        fieldName = EnumUserDTOValidationFields.PASSWORD.fieldName,
        maxLength = EnumUserDTOValidationFields.PASSWORD.maxLength
    ),
    USERNAME(
        fieldName = EnumUserDTOValidationFields.USERNAME.fieldName,
        maxLength = EnumUserDTOValidationFields.PASSWORD.maxLength
    )
}