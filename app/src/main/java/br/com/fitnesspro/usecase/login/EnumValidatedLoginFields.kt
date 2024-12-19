package br.com.fitnesspro.usecase.login

import br.com.fitnesspro.R
import br.com.fitnesspro.usecase.person.EnumValidatedPersonFields

enum class EnumValidatedLoginFields(val labelResId: Int, val maxLength: Int = 0) {
    EMAIL(R.string.label_person_email, EnumValidatedPersonFields.EMAIL.maxLength),
    PASSWORD(R.string.label_person_password, EnumValidatedPersonFields.PASSWORD.maxLength),
}