package br.com.fitnesspro.common.usecase.person

import br.com.fitnesspro.common.R
import br.com.fitnesspro.core.enums.IEnumFieldValidation


enum class EnumValidatedPersonFields(override val labelResId: Int, override val maxLength: Int = 0) : IEnumFieldValidation {
    NAME(R.string.label_person_name, 512),
    EMAIL(R.string.label_person_email, 64),
    PASSWORD(R.string.label_person_password, 1024),
    BIRTH_DATE(R.string.label_person_birth_date),
    PHONE(R.string.label_person_phone, 11)
}