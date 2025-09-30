package br.com.fitnesspro.common.usecase.login.enums

import br.com.android.ui.compose.components.fields.validation.interfaces.IEnumFieldValidation
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.usecase.person.EnumValidatedPersonFields

enum class EnumValidatedLoginFields(override val labelResId: Int, override val maxLength: Int = 0) :
    IEnumFieldValidation {
    EMAIL(R.string.label_person_email, EnumValidatedPersonFields.EMAIL.maxLength),
    PASSWORD(R.string.label_person_password, EnumValidatedPersonFields.PASSWORD.maxLength),
}