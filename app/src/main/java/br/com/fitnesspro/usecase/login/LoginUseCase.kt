package br.com.fitnesspro.usecase.login

import android.content.Context
import br.com.fitnesspro.R
import br.com.fitnesspro.core.security.HashHelper
import br.com.fitnesspro.repository.UserRepository
import br.com.fitnesspro.usecase.login.EnumValidatedLoginFields.EMAIL
import br.com.fitnesspro.usecase.login.EnumValidatedLoginFields.PASSWORD

class LoginUseCase(
    private val context: Context,
    private val userRepository: UserRepository,
) {

    suspend fun execute(email: String?, password: String?): List<Pair<EnumValidatedLoginFields?, String>> {
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

    private fun validatePassword(password: String?): Pair<EnumValidatedLoginFields, String>? {
        return when {
            password?.trim().isNullOrEmpty() -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(PASSWORD.labelResId)
                )

                Pair(PASSWORD, message)
            }

            else -> null
        }
    }

    private fun validateEmail(email: String?): Pair<EnumValidatedLoginFields, String>? {
        return when {
            email?.trim().isNullOrEmpty() -> {
                val message = context.getString(
                    R.string.validation_msg_required_field,
                    context.getString(EMAIL.labelResId)
                )

                Pair(EMAIL, message)
            }

            else -> null
        }
    }

    private suspend fun validateUserCredentials(
        email: String?,
        password: String?
    ): Pair<EnumValidatedLoginFields?, String>? {
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
                Pair(null, context.getString(R.string.validation_msg_invalid_credetials_login))
            }

            else -> null
        }
    }


}