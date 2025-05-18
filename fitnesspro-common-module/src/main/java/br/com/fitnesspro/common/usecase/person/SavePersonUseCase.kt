package br.com.fitnesspro.common.usecase.person

import android.content.Context
import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.usecase.person.EnumValidatedPersonFields.BIRTH_DATE
import br.com.fitnesspro.common.usecase.person.EnumValidatedPersonFields.EMAIL
import br.com.fitnesspro.common.usecase.person.EnumValidatedPersonFields.NAME
import br.com.fitnesspro.common.usecase.person.EnumValidatedPersonFields.PASSWORD
import br.com.fitnesspro.common.usecase.person.EnumValidatedPersonFields.PHONE
import br.com.fitnesspro.common.usecase.scheduler.SaveSchedulerConfigUseCase
import br.com.fitnesspro.core.extensions.dateNow
import br.com.fitnesspro.core.security.IPasswordHasher
import br.com.fitnesspro.core.validation.FieldValidationError
import br.com.fitnesspro.to.TOPerson
import java.time.ZoneId

class SavePersonUseCase(
    private val context: Context,
    private val userRepository: UserRepository,
    private val personRepository: PersonRepository,
    private val saveSchedulerConfigUseCase: SaveSchedulerConfigUseCase,
    private val passwordHasher: IPasswordHasher
) {

    suspend fun execute(toPerson: TOPerson, isRegisterServiceAuth: Boolean): List<FieldValidationError<EnumValidatedPersonFields>> {
        val validationResults = mutableListOf<FieldValidationError<EnumValidatedPersonFields>>()
        validationResults.addAll(validateUser(toPerson))
        validationResults.addAll(validatePerson(toPerson))

        if (validationResults.isEmpty()) {
            toPerson.user!!.password = passwordHasher.hashPassword(toPerson.user?.password!!)

            personRepository.runInTransaction {
                personRepository.savePerson(toPerson, isRegisterServiceAuth)
                saveSchedulerConfigUseCase.saveConfig(toPerson)
            }
        }

        return validationResults
    }

    private fun validatePerson(toPerson: TOPerson): MutableList<FieldValidationError<EnumValidatedPersonFields>> {
        val validationResults = mutableListOf(
            validatePersonName(toPerson),
            validatePersonBirthDate(toPerson),
            validatePersonPhone(toPerson)
        )

        return validationResults.filterNotNull().toMutableList()
    }

    private fun validatePersonName(toPerson: TOPerson): FieldValidationError<EnumValidatedPersonFields>? {
        val name = toPerson.name?.trim()

        val validationPair = when {
            name.isNullOrEmpty() -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(NAME.labelResId)
                )

                FieldValidationError(
                    field = NAME,
                    message = message,
                )
            }

            name.length > NAME.maxLength -> {
                val message = context.getString(
                    R.string.validation_msg_field_with_max_length,
                    context.getString(NAME.labelResId),
                    NAME.maxLength
                )

                FieldValidationError(
                    field = NAME,
                    message = message,
                )
            }

            else -> null
        }

        if (validationPair == null) {
            toPerson.name = name
        }

        return validationPair
    }

    private fun validatePersonBirthDate(toPerson: TOPerson): FieldValidationError<EnumValidatedPersonFields>? {
        val birthDate = toPerson.birthDate ?: return null

        val validationPair = when {
            birthDate > dateNow(ZoneId.systemDefault()) -> {
                val message = context.getString(
                    R.string.validation_msg_invalid_field,
                    context.getString(BIRTH_DATE.labelResId)
                )

                FieldValidationError(
                    field = BIRTH_DATE,
                    message = message,
                )
            }

            else -> null
        }

        return validationPair
    }

    private fun validatePersonPhone(toPerson: TOPerson): FieldValidationError<EnumValidatedPersonFields>? {
        val phone = toPerson.phone?.trim() ?: return null

        val validationPair = when {
            phone.length > PHONE.maxLength -> {
                val message = context.getString(
                    R.string.validation_msg_field_with_max_length,
                    context.getString(PHONE.labelResId),
                    PHONE.maxLength
                )

                FieldValidationError(
                    field = PHONE,
                    message = message,
                )
            }

            else -> null
        }

        if (validationPair == null) {
            toPerson.phone = phone
        }

        return validationPair
    }

    private suspend fun validateUser(toPerson: TOPerson): MutableList<FieldValidationError<EnumValidatedPersonFields>> {
        val validationResults = mutableListOf(
            validateUserEmail(toPerson),
            validateUserPassword(toPerson)
        )

        return validationResults.filterNotNull().toMutableList()
    }

    private suspend fun validateUserEmail(toPerson: TOPerson): FieldValidationError<EnumValidatedPersonFields>? {
        val email = toPerson.user?.email?.trim()

        val validationPair = when {
            email.isNullOrEmpty() -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EMAIL.labelResId)
                )

                FieldValidationError(
                    field = EMAIL,
                    message = message,
                )
            }

            email.length > EMAIL.maxLength -> {
                val message = context.getString(
                    R.string.validation_msg_field_with_max_length,
                    context.getString(EMAIL.labelResId),
                    EMAIL.maxLength
                )

                FieldValidationError(
                    field = EMAIL,
                    message = message,
                )
            }

            !EMAIL_ADDRESS.matcher(email).matches() -> {
                val message = context.getString(
                    R.string.validation_msg_invalid_field,
                    context.getString(EMAIL.labelResId)
                )

                FieldValidationError(
                    field = EMAIL,
                    message = message,
                )
            }

            userRepository.hasUserWithEmail(email, toPerson.user?.id) -> {
                val message = context.getString(R.string.validation_msg_email_in_use)
                FieldValidationError(
                    field = EMAIL,
                    message = message,
                )
            }

            else -> null
        }

        if (validationPair == null) {
            toPerson.user?.email = email
        }

        return validationPair
    }

    private fun validateUserPassword(toPerson: TOPerson): FieldValidationError<EnumValidatedPersonFields>? {
        val password = toPerson.user?.password?.trim()

        val validationPair = when {
            password.isNullOrEmpty() -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(PASSWORD.labelResId)
                )

                FieldValidationError(
                    field = PASSWORD,
                    message = message,
                )
            }

            toPerson.user?.password!!.length > PASSWORD.maxLength -> {
                val message = context.getString(
                    R.string.validation_msg_field_with_max_length,
                    context.getString(PASSWORD.labelResId),
                    PASSWORD.maxLength
                )

                FieldValidationError(
                    field = PASSWORD,
                    message = message,
                )
            }

            else -> null
        }

        if (validationPair == null) {
            toPerson.user?.password = password
        }

        return validationPair
    }
}