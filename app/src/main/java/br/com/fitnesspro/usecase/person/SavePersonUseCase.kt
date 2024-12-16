package br.com.fitnesspro.usecase.person

import android.content.Context
import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import br.com.fitnesspro.R
import br.com.fitnesspro.core.security.HashHelper
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.repository.UserRepository
import br.com.fitnesspro.ui.screen.registeruser.to.TOPerson
import br.com.fitnesspro.usecase.person.EnumValidatedPersonFields.BIRTH_DATE
import br.com.fitnesspro.usecase.person.EnumValidatedPersonFields.EMAIL
import br.com.fitnesspro.usecase.person.EnumValidatedPersonFields.NAME
import br.com.fitnesspro.usecase.person.EnumValidatedPersonFields.PASSWORD
import br.com.fitnesspro.usecase.person.EnumValidatedPersonFields.PHONE
import java.time.LocalDate

class SavePersonUseCase(
    private val context: Context,
    private val userRepository: UserRepository,
) {

    suspend fun execute(toPerson: TOPerson): List<Pair<EnumValidatedPersonFields, String>> {
        val user = User(
            email = toPerson.toUser?.email,
            password = toPerson.toUser?.password,
            type = toPerson.toUser?.type
        )

        val person = Person(
            name = toPerson.name,
            birthDate = toPerson.birthDate,
            phone = toPerson.phone,
            userId = user.id
        )

        val validationResults = mutableListOf<Pair<EnumValidatedPersonFields, String>>()
        validationResults.addAll(validateUser(user))
        validationResults.addAll(validatePerson(person))

        if (validationResults.isEmpty()) {
            toPerson.toUser?.id = user.id
            toPerson.id = person.id

            user.password = HashHelper.applyHash(user.password!!)

            userRepository.savePerson(user, person)
        }

        return validationResults
    }

    private fun validatePerson(person: Person): MutableList<Pair<EnumValidatedPersonFields, String>> {
        val validationResults = mutableListOf(
            validatePersonName(person),
            validatePersonBirthDate(person),
            validatePersonPhone(person)
        )

        return validationResults.filterNotNull().toMutableList()
    }

    private fun validatePersonName(person: Person): Pair<EnumValidatedPersonFields, String>? {
        val name = person.name?.trim()

        val validationPair = when {
            name.isNullOrEmpty() -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(NAME.labelResId)
                )

                Pair(NAME, message)
            }

            name.length > 512 -> {
                val message = context.getString(
                    R.string.validation_msg_field_with_max_length,
                    context.getString(NAME.labelResId),
                    512
                )

                Pair(NAME, message)
            }

            else -> null
        }

        if (validationPair == null) {
            person.name = name
        }

        return validationPair
    }

    private fun validatePersonBirthDate(person: Person): Pair<EnumValidatedPersonFields, String>? {
        val birthDate = person.birthDate ?: return null

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

    private fun validatePersonPhone(person: Person): Pair<EnumValidatedPersonFields, String>? {
        val phone = person.phone?.trim() ?: return null

        val validationPair = when {
            phone.length > 11 -> {
                val message = context.getString(
                    R.string.validation_msg_field_with_max_length,
                    context.getString(PHONE.labelResId),
                    11
                )

                Pair(PHONE, message)
            }

            else -> null
        }

        if (validationPair == null) {
            person.phone = phone
        }

        return validationPair
    }

    private suspend fun validateUser(user: User): MutableList<Pair<EnumValidatedPersonFields, String>> {
        val validationResults = mutableListOf(
            validateUserEmail(user),
            validateUserPassword(user)
        )

        return validationResults.filterNotNull().toMutableList()
    }

    private suspend fun validateUserEmail(user: User): Pair<EnumValidatedPersonFields, String>? {
        val email = user.email?.trim()

        val validationPair = when {
            email.isNullOrEmpty() -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EMAIL.labelResId)
                )

                Pair(EMAIL, message)
            }

            email.length > 512 -> {
                val message = context.getString(
                    R.string.validation_msg_field_with_max_length,
                    context.getString(EMAIL.labelResId),
                    512
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

            userRepository.hasUserWithEmail(email) -> {
                val message = context.getString(R.string.validation_msg_email_in_use)
                Pair(EMAIL, message)
            }

            else -> null
        }

        if (validationPair == null) {
            user.email = email
        }

        return validationPair
    }

    private fun validateUserPassword(user: User): Pair<EnumValidatedPersonFields, String>? {
        val password = user.password?.trim()

        val validationPair = when {
            password.isNullOrEmpty() -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(PASSWORD.labelResId)
                )

                Pair(PASSWORD, message)
            }

            user.password!!.length > 1024 -> {
                val message = context.getString(
                    R.string.validation_msg_field_with_max_length,
                    context.getString(PASSWORD.labelResId),
                    1024
                )

                Pair(PASSWORD, message)
            }

            else -> null
        }

        if (validationPair == null) {
            user.password = password
        }

        return validationPair
    }
}