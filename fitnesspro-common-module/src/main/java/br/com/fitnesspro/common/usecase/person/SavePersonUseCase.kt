package br.com.fitnesspro.common.usecase.person

import android.content.Context
import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.usecase.person.EnumPersonValidationTypes.INVALID_USER_EMAIL
import br.com.fitnesspro.common.usecase.person.EnumPersonValidationTypes.MAX_LENGTH_PERSON_NAME
import br.com.fitnesspro.common.usecase.person.EnumPersonValidationTypes.MAX_LENGTH_PERSON_PHONE
import br.com.fitnesspro.common.usecase.person.EnumPersonValidationTypes.MAX_LENGTH_USER_EMAIL
import br.com.fitnesspro.common.usecase.person.EnumPersonValidationTypes.MAX_LENGTH_USER_PASSWORD
import br.com.fitnesspro.common.usecase.person.EnumPersonValidationTypes.PERSON_BIRTH_DATE_FUTURE
import br.com.fitnesspro.common.usecase.person.EnumPersonValidationTypes.REQUIRED_PERSON_NAME
import br.com.fitnesspro.common.usecase.person.EnumPersonValidationTypes.REQUIRED_USER_EMAIL
import br.com.fitnesspro.common.usecase.person.EnumPersonValidationTypes.REQUIRED_USER_PASSWORD
import br.com.fitnesspro.common.usecase.person.EnumPersonValidationTypes.USER_EMAIL_IN_USE
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

open class SavePersonUseCase(
    private val context: Context,
    protected val userRepository: UserRepository,
    protected val personRepository: PersonRepository,
    protected val saveSchedulerConfigUseCase: SaveSchedulerConfigUseCase,
    protected val passwordHasher: IPasswordHasher
) {

    suspend fun execute(toPerson: TOPerson): List<FieldValidationError<EnumValidatedPersonFields, EnumPersonValidationTypes>> {
        val validationResults = mutableListOf<FieldValidationError<EnumValidatedPersonFields, EnumPersonValidationTypes>>()
        validationResults.addAll(validateUser(toPerson))
        validationResults.addAll(validatePerson(toPerson))

        if (validationResults.isEmpty()) {
            toPerson.toUser!!.password = passwordHasher.hashPassword(toPerson.toUser?.password!!)

            personRepository.savePerson(toPerson)
            saveSchedulerConfigUseCase.saveConfig(toPerson.id!!)
        }

        return validationResults
    }

    protected fun validatePerson(toPerson: TOPerson): MutableList<FieldValidationError<EnumValidatedPersonFields, EnumPersonValidationTypes>> {
        val validationResults = mutableListOf(
            validatePersonName(toPerson),
            validatePersonBirthDate(toPerson),
            validatePersonPhone(toPerson)
        )

        return validationResults.filterNotNull().toMutableList()
    }

    private fun validatePersonName(toPerson: TOPerson): FieldValidationError<EnumValidatedPersonFields, EnumPersonValidationTypes>? {
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
                    validationType = REQUIRED_PERSON_NAME
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
                    validationType = MAX_LENGTH_PERSON_NAME
                )
            }

            else -> null
        }

        if (validationPair == null) {
            toPerson.name = name
        }

        return validationPair
    }

    private fun validatePersonBirthDate(toPerson: TOPerson): FieldValidationError<EnumValidatedPersonFields, EnumPersonValidationTypes>? {
        val birthDate = toPerson.birthDate ?: return null

        val validationPair = when {
            birthDate > dateNow() -> {
                val message = context.getString(
                    R.string.validation_msg_invalid_field,
                    context.getString(BIRTH_DATE.labelResId)
                )

                FieldValidationError(
                    field = BIRTH_DATE,
                    message = message,
                    validationType = PERSON_BIRTH_DATE_FUTURE
                )
            }

            else -> null
        }

        return validationPair
    }

    private fun validatePersonPhone(toPerson: TOPerson): FieldValidationError<EnumValidatedPersonFields, EnumPersonValidationTypes>? {
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
                    validationType = MAX_LENGTH_PERSON_PHONE
                )
            }

            else -> null
        }

        if (validationPair == null) {
            toPerson.phone = phone
        }

        return validationPair
    }

    protected suspend fun validateUser(toPerson: TOPerson): MutableList<FieldValidationError<EnumValidatedPersonFields, EnumPersonValidationTypes>> {
        val validationResults = mutableListOf(
            validateUserEmail(toPerson),
            validateUserPassword(toPerson)
        )

        return validationResults.filterNotNull().toMutableList()
    }

    private suspend fun validateUserEmail(toPerson: TOPerson): FieldValidationError<EnumValidatedPersonFields, EnumPersonValidationTypes>? {
        val email = toPerson.toUser?.email?.trim()

        val validationPair = when {
            email.isNullOrEmpty() -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EMAIL.labelResId)
                )

                FieldValidationError(
                    field = EMAIL,
                    message = message,
                    validationType = REQUIRED_USER_EMAIL
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
                    validationType = MAX_LENGTH_USER_EMAIL
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
                    validationType = INVALID_USER_EMAIL
                )
            }

            userRepository.hasUserWithEmail(email, toPerson.toUser?.id) -> {
                val message = context.getString(R.string.validation_msg_email_in_use)
                FieldValidationError(
                    field = EMAIL,
                    message = message,
                    validationType = USER_EMAIL_IN_USE
                )
            }

            else -> null
        }

        if (validationPair == null) {
            toPerson.toUser?.email = email
        }

        return validationPair
    }

    private fun validateUserPassword(toPerson: TOPerson): FieldValidationError<EnumValidatedPersonFields, EnumPersonValidationTypes>? {
        val password = toPerson.toUser?.password?.trim()

        val validationPair = when {
            password.isNullOrEmpty() -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(PASSWORD.labelResId)
                )

                FieldValidationError(
                    field = PASSWORD,
                    message = message,
                    validationType = REQUIRED_USER_PASSWORD
                )
            }

            toPerson.toUser?.password!!.length > PASSWORD.maxLength -> {
                val message = context.getString(
                    R.string.validation_msg_field_with_max_length,
                    context.getString(PASSWORD.labelResId),
                    PASSWORD.maxLength
                )

                FieldValidationError(
                    field = PASSWORD,
                    message = message,
                    validationType = MAX_LENGTH_USER_PASSWORD
                )
            }

            else -> null
        }

        if (validationPair == null) {
            toPerson.toUser?.password = password
        }

        return validationPair
    }
}