package br.com.fitnesspro.common.usecase.login

import android.content.Context
import br.com.fitnesspro.common.R
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.usecase.login.EnumValidatedLoginFields.EMAIL
import br.com.fitnesspro.common.usecase.login.EnumValidatedLoginFields.PASSWORD
import br.com.fitnesspro.core.security.HashHelper
import br.com.fitnesspro.core.validation.FieldValidationError

class LoginUseCase(
    private val context: Context,
    private val userRepository: UserRepository,
) {

    suspend fun execute(email: String?, password: String?): List<FieldValidationError<EnumValidatedLoginFields, EnumLoginValidationTypes>> {
        val validationsResults = mutableListOf(
            validateEmail(email),
            validatePassword(password),
            validateUserCredentials(email, password)
        ).filterNotNull()

        if (validationsResults.isEmpty()) {
            val hashedPassword = HashHelper.applyHash(password!!)
            userRepository.authenticate(email!!, hashedPassword)
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
        password: String?
    ): FieldValidationError<EnumValidatedLoginFields, EnumLoginValidationTypes>? {
        val emailTrimmed = email?.trim()
        val passwordTrimmed = password?.trim()

        if (emailTrimmed.isNullOrEmpty() || passwordTrimmed.isNullOrEmpty()) {
            return null
        }

        val invalidLength = emailTrimmed.length > EMAIL.maxLength || passwordTrimmed.length > PASSWORD.maxLength
        val hashedPassword = HashHelper.applyHash(passwordTrimmed)
        val userNotExists = !userRepository.hasUserWithCredentials(emailTrimmed, hashedPassword)

        return when {
            invalidLength || userNotExists -> {
                FieldValidationError(
                    field = null,
                    message = context.getString(R.string.validation_msg_invalid_credetials_login),
                    validationType = EnumLoginValidationTypes.INVALID_CREDENTIALS
                )
            }

            else -> null
        }
    }


}