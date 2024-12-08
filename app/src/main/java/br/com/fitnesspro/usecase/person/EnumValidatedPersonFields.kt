package br.com.fitnesspro.usecase.person

import br.com.fitnesspro.R

enum class EnumValidatedPersonFields(val labelResId: Int) {
    NAME(R.string.label_person_name),
    EMAIL(R.string.label_person_email),
    PASSWORD(R.string.label_person_password),
    BIRTH_DATE(R.string.label_person_birth_date),
    PHONE(R.string.label_person_phone)
}