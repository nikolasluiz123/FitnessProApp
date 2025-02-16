package br.com.fitnesspor.service.data.access.webclient.general

import android.content.Context
import br.com.fitnesspor.service.data.access.extensions.getResponseBody
import br.com.fitnesspor.service.data.access.service.general.IPersonService
import br.com.fitnesspor.service.data.access.webclient.common.FitnessProWebClient
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.PersonAcademyTime
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.shared.communication.dtos.general.PersonAcademyTimeDTO
import br.com.fitnesspro.shared.communication.dtos.general.PersonDTO
import br.com.fitnesspro.shared.communication.dtos.general.UserDTO
import br.com.fitnesspro.models.general.enums.EnumUserType as EnumUserTypeService

class PersonWebClient(
    context: Context,
    private val personService: IPersonService
): FitnessProWebClient(context) {

    suspend fun savePerson(person: Person, user: User) {
        persistenceServiceErrorHandlingBlock(
            codeBlock = {
                personService.savePerson(personDTO = person.toPersonDTO(user)).getResponseBody()
            }
        )
    }

    suspend fun savePersonAcademyTime(
        token: String,
        personAcademyTime: PersonAcademyTime
    ) {
        persistenceServiceErrorHandlingBlock(
            codeBlock = {
                personService.savePersonAcademyTime(
                    token = formatToken(token),
                    personAcademyTimeDTO = personAcademyTime.toPersonAcademyTimeDTO()
                ).getResponseBody()
            }
        )
    }

    private fun Person.toPersonDTO(user: User): PersonDTO {
        return PersonDTO(
            id = id,
            name = name,
            active = active,
            birthDate = birthDate,
            phone = phone,
            user = user.toUserDTO()
        )
    }

    private fun User.toUserDTO(): UserDTO {
        return UserDTO(
            id = id,
            email = email,
            password = password,
            active = active,
            type = getUserType(type!!),
            authenticated = authenticated
        )
    }

    private fun getUserType(type: EnumUserType): EnumUserTypeService? {
        return when (type) {
            EnumUserType.PERSONAL_TRAINER -> EnumUserTypeService.PERSONAL_TRAINER
            EnumUserType.NUTRITIONIST -> EnumUserTypeService.NUTRITIONIST
            EnumUserType.ACADEMY_MEMBER -> EnumUserTypeService.ACADEMY_MEMBER
        }
    }

    private fun PersonAcademyTime.toPersonAcademyTimeDTO(): PersonAcademyTimeDTO {
        return PersonAcademyTimeDTO(
            id = id,
            personId = personId,
            academyId = academyId,
            timeStart = timeStart,
            timeEnd = timeEnd,
            dayOfWeek = dayOfWeek,
            active = active
        )
    }
}