package br.com.fitnesspro.service.data.access.webclients

import br.com.fitnesspro.model.User
import br.com.fitnesspro.service.data.access.dto.user.EnumUserDTOValidationFields
import br.com.fitnesspro.service.data.access.dto.user.UserDTO
import br.com.fitnesspro.service.data.access.services.IUserService
import br.com.fitnesspro.service.data.access.webclients.extensions.toValidationResult
import br.com.fitnesspro.service.data.access.webclients.validation.ValidationResult
import java.time.LocalDateTime

class UserWebClient(private val service: IUserService) {

    /**
     * Função para realizar o cadastro do usuário.
     *
     * @param user Objeto contendo as informações informadas pelo usuário.
     */
    suspend fun register(user: User): ValidationResult {
        val dto = UserDTO(
            firstName = user.firstName,
            lastName = user.lastName,
            username = user.username,
            email = user.email,
            password = user.password,
            isStaff = false,
            isActive = true,
            dateJoined = LocalDateTime.now(),
            lastLogin = null,
            profile = user.profile
        )

        return service.register(dto).toValidationResult(enumEntries = EnumUserDTOValidationFields.entries)
    }
}