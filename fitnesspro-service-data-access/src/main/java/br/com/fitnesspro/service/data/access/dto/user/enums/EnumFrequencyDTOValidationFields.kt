package br.com.fitnesspro.service.data.access.dto.user.enums

import br.com.fitnesspro.service.data.access.dto.interfaces.IEnumDTOValidationFields

enum class EnumFrequencyDTOValidationFields(
    override val fieldName: String,
    override val maxLength: Int? = null
): IEnumDTOValidationFields {
    DAY_WEEK(fieldName = "day_week", maxLength = 3),
    START(fieldName = "start"),
    END(fieldName = "end"),
    ACADEMY(fieldName = "academy"),
}