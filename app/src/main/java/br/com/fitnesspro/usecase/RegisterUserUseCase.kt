package br.com.fitnesspro.usecase

import android.util.Patterns
import br.com.fitnesspro.R
import br.com.fitnesspro.core.validation.ValidationResult
import br.com.fitnesspro.model.EnumUserValidatedFields
import br.com.fitnesspro.model.User
import br.com.fitnesspro.repository.UserRepository
import java.time.LocalDate

/**
 * Classe com as regras de negócio para o cadastro de um usuário
 */
class RegisterUserUseCase(private val userRepository: UserRepository) {

    /**
     * Função que pode ser usada para salvar um usuário.
     */
    suspend fun saveUser(user: User): ValidationResult {


        val errors = mutableMapOf<EnumUserValidatedFields, Int>()

        if (user.name.isNullOrEmpty()) {
            errors[EnumUserValidatedFields.NAME] = R.string.register_user_name_required_validation_message
        }

        if (user.email.isNullOrEmpty()) {
            errors[EnumUserValidatedFields.EMAIL] = R.string.register_user_email_required_validation_message
        } else if (!Patterns.EMAIL_ADDRESS.matcher(user.email!!).matches()) {
            errors[EnumUserValidatedFields.EMAIL] = R.string.register_user_email_invalid_validation_message
        } else if (userRepository.isEmailUnique(user.email!!)) {
            errors[EnumUserValidatedFields.EMAIL] = R.string.register_user_email_registred_validation_message
        }

        if (user.password.isNullOrEmpty()) {
            errors[EnumUserValidatedFields.PASSWORD] = R.string.register_user_password_required_validation_message
        }

        if (user.birthDate == null) {
            errors[EnumUserValidatedFields.BIRTH_DATE] = R.string.register_user_birth_date_required_validation_message
        } else if (user.birthDate!! > LocalDate.now()) {
            errors[EnumUserValidatedFields.BIRTH_DATE] = R.string.register_user_birth_date_invalid_validation_message
        }

        return if (errors.isEmpty()) {
            userRepository.saveUser(user)
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors)
        }
    }

}