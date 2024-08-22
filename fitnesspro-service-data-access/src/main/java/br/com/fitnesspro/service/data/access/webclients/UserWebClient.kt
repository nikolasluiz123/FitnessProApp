package br.com.fitnesspro.service.data.access.webclients

import android.content.Context
import br.com.fitnesspro.model.Frequency
import br.com.fitnesspro.model.User
import br.com.fitnesspro.model.enums.EnumUserProfile
import br.com.fitnesspro.service.data.access.dto.user.AcademyDTO
import br.com.fitnesspro.service.data.access.dto.user.AuthenticationDTO
import br.com.fitnesspro.service.data.access.dto.user.FrequencyDTO
import br.com.fitnesspro.service.data.access.dto.user.UserDTO
import br.com.fitnesspro.service.data.access.dto.user.enums.EnumAuthenticationDTOValidationFields
import br.com.fitnesspro.service.data.access.dto.user.enums.EnumFrequencyDTOValidationFields
import br.com.fitnesspro.service.data.access.dto.user.enums.EnumUserDTOValidationFields
import br.com.fitnesspro.service.data.access.enums.EnumUserGroups
import br.com.fitnesspro.service.data.access.services.IUserService
import br.com.fitnesspro.service.data.access.webclients.extensions.toResultList
import br.com.fitnesspro.service.data.access.webclients.extensions.toSingleResult
import br.com.fitnesspro.service.data.access.webclients.extensions.toValidationResult
import br.com.fitnesspro.service.data.access.webclients.result.ResultList
import br.com.fitnesspro.service.data.access.webclients.result.SingleResult
import br.com.fitnesspro.service.data.access.webclients.result.ValidationResult
import okhttp3.Credentials
import java.time.LocalDateTime

class UserWebClient(
    context: Context,
    private val service: IUserService
) : BaseWebClient(context) {

    /**
     * Função para realizar o cadastro do usuário.
     *
     * @param user Objeto contendo as informações informadas pelo usuário.
     */
    suspend fun saveUser(user: User): ValidationResult {
        val dto = UserDTO(
            id = user.id,
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

        return executeValidatedProcessErrorHandlerBlock<EnumUserDTOValidationFields>(
            codeBlock = {
                if (dto.id == null) {
                    service.register(dto).toValidationResult(enumEntries = EnumUserDTOValidationFields.entries)
                } else {
                    service.update(dto, dto.id!!).toValidationResult(enumEntries = EnumUserDTOValidationFields.entries)
                }
            }
        )
    }

    suspend fun getAcademies(): ResultList<AcademyDTO> {
        return executeResultListProcessErrorHandlerBlock(
            codeBlock = {
                service.getAcademies().toResultList()
            }
        )
    }

    suspend fun authenticate(username: String, password: String): SingleResult<User> {
        val authenticationDTO = AuthenticationDTO(username, password)

        return executeSingleResultProcessErrorHandlerBlock<User, EnumAuthenticationDTOValidationFields>(
            codeBlock = {
                val singleResult = service.authenticate(authenticationDTO).toSingleResult(
                    UserDTO::class.java,
                    EnumAuthenticationDTOValidationFields.entries
                )

                if (singleResult.validationResult is ValidationResult.Success) {
                    SingleResult(
                        data = User(
                            id = singleResult.data!!.id,
                            firstName = singleResult.data.firstName,
                            lastName = singleResult.data.lastName,
                            username = singleResult.data.username,
                            email = singleResult.data.email,
                            password = singleResult.data.password,
                            profile = getUserProfileOfGroups(singleResult.data.groupNames),
                            credentials = Credentials.basic(
                                singleResult.data.username,
                                singleResult.data.password
                            )
                        ),
                        validationResult = ValidationResult.Success
                    )
                } else {
                    SingleResult(
                        data = null,
                        validationResult = singleResult.validationResult
                    )
                }
            }
        )
    }

    private fun getUserProfileOfGroups(groups: List<String>): EnumUserProfile {
        return when {
            groups.contains(EnumUserGroups.STUDENT_PERMISSIONS.name) -> EnumUserProfile.STUDENT
            groups.contains(EnumUserGroups.TRAINER_PERMISSIONS.name) -> EnumUserProfile.TRAINER
            groups.contains(EnumUserGroups.NUTRITIONIST_PERMISSIONS.name) -> EnumUserProfile.NUTRITIONIST
            else -> throw IllegalArgumentException("Não foi possível identificar o perfil do usuário.")
        }
    }

    suspend fun saveAcademyFrequency(username: String, password: String, frequency: Frequency): ValidationResult {
        val dto = FrequencyDTO(
            id = frequency.id,
            dayWeek = frequency.dayWeek!!,
            start = frequency.start,
            end = frequency.end,
            username = frequency.username!!,
            academy = frequency.academy!!
        )

        return executeValidatedProcessErrorHandlerBlock<EnumFrequencyDTOValidationFields>(
            codeBlock = {
                service.saveAcademyFrequency(
                    credentials = Credentials.basic(username, password),
                    frequencyDTO = dto
                ).toValidationResult(enumEntries = EnumFrequencyDTOValidationFields.entries)
            }
        )
    }

    suspend fun getAcademyFrequencies(username: String, password: String): ResultList<Frequency> {
        return executeResultListProcessErrorHandlerBlock(
            codeBlock = {
                val result = service.getAcademyFrequencies(Credentials.basic(username, password), username).toResultList()

                ResultList(
                    data = result.data.map {
                        Frequency(
                            id = it.id,
                            dayWeek = it.dayWeek,
                            start = it.start,
                            end = it.end,
                            academy = it.academy,
                            username = it.username,
                            academyName = it.academyName,
                            dayWeekDisplay = it.dayWeekDisplay
                        )
                    },
                    result.error
                )
            }
        )
    }
}