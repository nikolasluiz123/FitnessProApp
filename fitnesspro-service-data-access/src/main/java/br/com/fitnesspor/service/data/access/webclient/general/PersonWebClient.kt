package br.com.fitnesspor.service.data.access.webclient.general

import android.content.Context
import br.com.fitnesspor.service.data.access.extensions.getResponseBody
import br.com.fitnesspor.service.data.access.service.general.IPersonService
import br.com.fitnesspor.service.data.access.webclient.common.FitnessProWebClient
import br.com.fitnesspro.core.extensions.defaultGSon
import br.com.fitnesspro.model.enums.EnumUserType
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.PersonAcademyTime
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.shared.communication.dtos.general.PersonAcademyTimeDTO
import br.com.fitnesspro.shared.communication.dtos.general.PersonDTO
import br.com.fitnesspro.shared.communication.dtos.general.UserDTO
import br.com.fitnesspro.shared.communication.filter.CommonImportFilter
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import com.google.gson.GsonBuilder
import br.com.fitnesspro.models.general.enums.EnumUserType as EnumUserTypeService

class PersonWebClient(
    context: Context,
    private val personService: IPersonService
): FitnessProWebClient(context) {

    suspend fun savePerson(person: Person, user: User): PersistenceServiceResponse {
        return persistenceServiceErrorHandlingBlock(
            codeBlock = {
                personService.savePerson(personDTO = person.toPersonDTO(user)).getResponseBody()
            }
        )
    }

    suspend fun savePersonBatch(
        token: String,
        persons: List<Person>,
        users: List<User>
    ): ExportationServiceResponse {
        return exportationServiceErrorHandlingBlock(
            codeBlock = {
                val personDTOList = persons.mapIndexed { index, person ->
                    person.toPersonDTO(users[index])
                }

                personService.savePersonBatch(
                    token = formatToken(token),
                    personDTOList = personDTOList
                ).getResponseBody()
            }
        )
    }

    suspend fun savePersonAcademyTime(
        token: String,
        personAcademyTime: PersonAcademyTime
    ): PersistenceServiceResponse {
        return persistenceServiceErrorHandlingBlock(
            codeBlock = {
                personService.savePersonAcademyTime(
                    token = formatToken(token),
                    personAcademyTimeDTO = personAcademyTime.toPersonAcademyTimeDTO()
                ).getResponseBody()
            }
        )
    }

    suspend fun savePersonAcademyTimeBatch(
        token: String,
        personAcademyTimeList: List<PersonAcademyTime>
    ): ExportationServiceResponse {
        return exportationServiceErrorHandlingBlock(
            codeBlock = {
                personService.savePersonAcademyTimeBatch(
                    token = formatToken(token),
                    personAcademyTimeDTOList = personAcademyTimeList.map { it.toPersonAcademyTimeDTO() }
                ).getResponseBody()
            }
        )
    }

    suspend fun importUsers(
        token: String,
        filter: CommonImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<UserDTO> {
        return importationServiceErrorHandlingBlock(
            codeBlock = {
                val gson = GsonBuilder().defaultGSon()

                personService.importUsers(
                    token = formatToken(token),
                    filter = gson.toJson(filter),
                    pageInfos = gson.toJson(pageInfos)
                ).getResponseBody(UserDTO::class.java)
            }
        )
    }

    suspend fun importPersons(
        token: String,
        filter: CommonImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<PersonDTO> {
        return importationServiceErrorHandlingBlock(
            codeBlock = {
                val gson = GsonBuilder().defaultGSon()

                personService.importPersons(
                    token = formatToken(token),
                    filter = gson.toJson(filter),
                    pageInfos = gson.toJson(pageInfos)
                ).getResponseBody(PersonDTO::class.java)
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
        )
    }

    private fun getUserType(type: EnumUserType): EnumUserTypeService {
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