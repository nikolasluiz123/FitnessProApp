package br.com.fitnesspro.common.usecase.person

import br.com.fitnesspro.common.R


enum class EnumValidatedPersonFields(val labelResId: Int, val maxLength: Int = 0) {
    NAME(R.string.label_person_name, 512),
    EMAIL(R.string.label_person_email, 512),
    PASSWORD(R.string.label_person_password, 1024),
    BIRTH_DATE(R.string.label_person_birth_date),
    PHONE(R.string.label_person_phone, 11)
}