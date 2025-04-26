package br.com.fitnesspro.common.usecase.login

import android.content.Context
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.usecase.login.enums.EnumLoginValidationTypes
import br.com.fitnesspro.common.usecase.login.enums.EnumValidatedLoginFields
import br.com.fitnesspro.common.usecase.login.enums.EnumValidatedLoginFields.EMAIL
import br.com.fitnesspro.common.usecase.login.enums.EnumValidatedLoginFields.PASSWORD
import br.com.fitnesspro.core.security.IPasswordHasher
import br.com.fitnesspro.core.validation.FieldValidationError

class DefaultLoginUseCase(
    private val context: Context,
    private val userRepository: UserRepository,
    private val passwordHasher: IPasswordHasher,
    private val personRepository: PersonRepository
) {

    suspend fun execute(email: String?, password: String?, authAgain: Boolean = false): List<FieldValidationError<EnumValidatedLoginFields, EnumLoginValidationTypes>> {
        val validationsResults = mutableListOf(
            validateEmail(email),
            validatePassword(password),
            validateUserCredentials(email, password, authAgain)
        ).filterNotNull().toMutableList()

        if (validationsResults.isEmpty()) {
            val hashedPassword = passwordHasher.hashPassword(password!!)

            userRepository.authenticate(
                email = email!!,
                password = hashedPassword
            )
        }

        return validationsResults
    }

    private fun validatePassword(password: String?): FieldValidationError<EnumValidatedLoginFields, EnumLoginValidationTypes>? {
        return when {
            password?.trim().isNullOrEmpty() -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(PASSWORD.labelResId)
                )

                FieldValidationError(
                    field = PASSWORD,
                    message = message,
                    validationType = EnumLoginValidationTypes.REQUIRED_PASSWORD
                )
            }

            else -> null
        }
    }

    private fun validateEmail(email: String?): FieldValidationError<EnumValidatedLoginFields, EnumLoginValidationTypes>? {
        return when {
            email?.trim().isNullOrEmpty() -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EMAIL.labelResId)
                )

                FieldValidationError(
                    field = EMAIL,
                    message = message,
                    validationType = EnumLoginValidationTypes.REQUIRED_EMAIL
                )
            }

            else -> null
        }
    }

    private suspend fun validateUserCredentials(
        email: String?,
        password: String?,
        authAgain: Boolean
    ): FieldValidationError<EnumValidatedLoginFields, EnumLoginValidationTypes>? {
        val emailTrimmed = email?.trim()
        val passwordTrimmed = password?.trim()

        if (emailTrimmed.isNullOrEmpty() || passwordTrimmed.isNullOrEmpty()) {
            return null
        }

        val invalidLength = emailTrimmed.length > EMAIL.maxLength || passwordTrimmed.length > PASSWORD.maxLength
        val hashedPassword = passwordHasher.hashPassword(passwordTrimmed)
        var userNotExists = !userRepository.hasUserWithCredentials(emailTrimmed, hashedPassword)
        val toPersonRemote = personRepository.findPersonByEmailRemote(emailTrimmed)

        val authUser = userRepository.getAuthenticatedUser()
        val isSameUser = authUser?.email == emailTrimmed && authUser.password == hashedPassword

        if (userNotExists && toPersonRemote != null) {
            personRepository.savePerson(
                toPerson = toPersonRemote,
                isRegisterServiceAuth = false,
                forceInsertLocally = true
            )

            userNotExists = false
        }

        return when {
            invalidLength || userNotExists -> {
                FieldValidationError(
                    field = null,
                    message = context.getString(R.string.validation_msg_invalid_credetials_login),
                    validationType = EnumLoginValidationTypes.INVALID_CREDENTIALS
                )
            }

            authAgain && !isSameUser -> {
                FieldValidationError(
                    field = null,
                    message = context.getString(R.string.validation_msg_not_same_user_auth_again),
                    validationType = EnumLoginValidationTypes.RE_AUTHENTICATION_DIFF_USER
                )
            }

            else -> null
        }
    }


}