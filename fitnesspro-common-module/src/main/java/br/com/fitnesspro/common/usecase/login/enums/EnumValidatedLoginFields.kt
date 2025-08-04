package br.com.fitnesspro.common.usecase.login.enums

import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.usecase.person.EnumValidatedPersonFields
import br.com.fitnesspro.core.enums.IEnumFieldValidation

enum class EnumValidatedLoginFields(override val labelResId: Int, override val maxLength: Int = 0) : IEnumFieldValidation {
    EMAIL(R.string.label_person_email, EnumValidatedPersonFields.EMAIL.maxLength),
    PASSWORD(R.string.label_person_password, EnumValidatedPersonFields.PASSWORD.maxLength),
}