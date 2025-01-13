package br.com.fitnesspro.common.usecase.person

import android.content.Context
import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.usecase.person.EnumValidatedPersonFields.BIRTH_DATE
import br.com.fitnesspro.common.usecase.person.EnumValidatedPersonFields.EMAIL
import br.com.fitnesspro.common.usecase.person.EnumValidatedPersonFields.NAME
import br.com.fitnesspro.common.usecase.person.EnumValidatedPersonFields.PASSWORD
import br.com.fitnesspro.common.usecase.person.EnumValidatedPersonFields.PHONE
import br.com.fitnesspro.common.usecase.scheduler.SaveSchedulerConfigUseCase
import br.com.fitnesspro.core.security.IPasswordHasher
import br.com.fitnesspro.to.TOPerson
import java.time.LocalDate

open class SavePersonUseCase(
    private val context: Context,
    protected val userRepository: UserRepository,
    protected val saveSchedulerConfigUseCase: SaveSchedulerConfigUseCase,
    protected val passwordHasher: IPasswordHasher
) {

    suspend fun execute(toPerson: TOPerson): List<Pair<EnumValidatedPersonFields, String>> {
        val validationResults = mutableListOf<Pair<EnumValidatedPersonFields, String>>()
        validationResults.addAll(validateUser(toPerson))
        validationResults.addAll(validatePerson(toPerson))

        if (validationResults.isEmpty()) {
            toPerson.toUser!!.password = passwordHasher.hashPassword(toPerson.toUser?.password!!)

            userRepository.savePerson(toPerson)
            saveSchedulerConfigUseCase.saveConfig(toPerson.id!!)
        }

        return validationResults
    }

    protected fun validatePerson(toPerson: TOPerson): MutableList<Pair<EnumValidatedPersonFields, String>> {
        val validationResults = mutableListOf(
            validatePersonName(toPerson),
            validatePersonBirthDate(toPerson),
            validatePersonPhone(toPerson)
        )

        return validationResults.filterNotNull().toMutableList()
    }

    private fun validatePersonName(toPerson: TOPerson): Pair<EnumValidatedPersonFields, String>? {
        val name = toPerson.name?.trim()

        val validationPair = when {
            name.isNullOrEmpty() -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(NAME.labelResId)
                )

                Pair(NAME, message)
            }

            name.length > NAME.maxLength -> {
                val message = context.getString(
                    R.string.validation_msg_field_with_max_length,
                    context.getString(NAME.labelResId),
                    NAME.maxLength
                )

                Pair(NAME, message)
            }

            else -> null
        }

        if (validationPair == null) {
            toPerson.name = name
        }

        return validationPair
    }

    private fun validatePersonBirthDate(toPerson: TOPerson): Pair<EnumValidatedPersonFields, String>? {
        val birthDate = toPerson.birthDate ?: return null

        val validationPair = when {
            birthDate > LocalDate.now() -> {
                val message = context.getString(
                    R.string.validation_msg_invalid_field,
                    context.getString(BIRTH_DATE.labelResId)
                )

                Pair(BIRTH_DATE, message)
            }

            else -> null
        }

        return validationPair
    }

    private fun validatePersonPhone(toPerson: TOPerson): Pair<EnumValidatedPersonFields, String>? {
        val phone = toPerson.phone?.trim() ?: return null

        val validationPair = when {
            phone.length > PHONE.maxLength -> {
                val message = context.getString(
                    R.string.validation_msg_field_with_max_length,
                    context.getString(PHONE.labelResId),
                    PHONE.maxLength
                )

                Pair(PHONE, message)
            }

            else -> null
        }

        if (validationPair == null) {
            toPerson.phone = phone
        }

        return validationPair
    }

    protected suspend fun validateUser(toPerson: TOPerson): MutableList<Pair<EnumValidatedPersonFields, String>> {
        val validationResults = mutableListOf(
            validateUserEmail(toPerson),
            validateUserPassword(toPerson)
        )

        return validationResults.filterNotNull().toMutableList()
    }

    private suspend fun validateUserEmail(toPerson: TOPerson): Pair<EnumValidatedPersonFields, String>? {
        val email = toPerson.toUser?.email?.trim()

        val validationPair = when {
            email.isNullOrEmpty() -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EMAIL.labelResId)
                )

                Pair(EMAIL, message)
            }

            email.length > EMAIL.maxLength -> {
                val message = context.getString(
                    R.string.validation_msg_field_with_max_length,
                    context.getString(EMAIL.labelResId),
                    EMAIL.maxLength
                )

                Pair(EMAIL, message)
            }

            !EMAIL_ADDRESS.matcher(email).matches() -> {
                val message = context.getString(
                    R.string.validation_msg_invalid_field,
                    context.getString(EMAIL.labelResId)
                )

                Pair(EMAIL, message)
            }

            userRepository.hasUserWithEmail(email, toPerson.toUser?.id) -> {
                val message = context.getString(R.string.validation_msg_email_in_use)
                Pair(EMAIL, message)
            }

            else -> null
        }

        if (validationPair == null) {
            toPerson.toUser?.email = email
        }

        return validationPair
    }

    private fun validateUserPassword(toPerson: TOPerson): Pair<EnumValidatedPersonFields, String>? {
        val password = toPerson.toUser?.password?.trim()

        val validationPair = when {
            password.isNullOrEmpty() -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(PASSWORD.labelResId)
                )

                Pair(PASSWORD, message)
            }

            toPerson.toUser?.password!!.length > PASSWORD.maxLength -> {
                val message = context.getString(
                    R.string.validation_msg_field_with_max_length,
                    context.getString(PASSWORD.labelResId),
                    PASSWORD.maxLength
                )

                Pair(PASSWORD, message)
            }

            else -> null
        }

        if (validationPair == null) {
            toPerson.toUser?.password = password
        }

        return validationPair
    }
}