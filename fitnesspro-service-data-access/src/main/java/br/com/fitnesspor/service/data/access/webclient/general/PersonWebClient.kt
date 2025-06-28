package br.com.fitnesspor.service.data.access.webclient.general

import android.content.Context
import br.com.fitnesspor.service.data.access.extensions.getResponseBody
import br.com.fitnesspor.service.data.access.service.general.IPersonService
import br.com.fitnesspor.service.data.access.webclient.common.FitnessProWebClient
import br.com.fitnesspro.core.extensions.defaultGSon
import br.com.fitnesspro.mappers.getPersonAcademyTimeDTO
import br.com.fitnesspro.mappers.getPersonDTO
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.PersonAcademyTime
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.shared.communication.dtos.general.FindPersonDTO
import br.com.fitnesspro.shared.communication.dtos.general.PersonAcademyTimeDTO
import br.com.fitnesspro.shared.communication.dtos.general.PersonDTO
import br.com.fitnesspro.shared.communication.dtos.general.UserDTO
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.CommonImportFilter
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import br.com.fitnesspro.shared.communication.responses.SingleValueServiceResponse
import com.google.gson.GsonBuilder

class PersonWebClient(
    context: Context,
    private val personService: IPersonService,
): FitnessProWebClient(context) {

    suspend fun savePerson(token: String, person: Person, user: User): PersistenceServiceResponse<PersonDTO> {
        return persistenceServiceErrorHandlingBlock(
            codeBlock = {
                val personDTO = person.getPersonDTO(user)
                personService.savePerson( formatToken(token), personDTO).getResponseBody(PersonDTO::class.java)
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
                    person.getPersonDTO(users[index])
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
    ): PersistenceServiceResponse<PersonAcademyTimeDTO> {
        return persistenceServiceErrorHandlingBlock(
            codeBlock = {
                personService.savePersonAcademyTime(
                    token = formatToken(token),
                    personAcademyTimeDTO = personAcademyTime.getPersonAcademyTimeDTO()
                ).getResponseBody(PersonAcademyTimeDTO::class.java)
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
                    personAcademyTimeDTOList = personAcademyTimeList.map(PersonAcademyTime::getPersonAcademyTimeDTO)
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

    suspend fun importPersonAcademyTime(
        token: String,
        filter: CommonImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<PersonAcademyTimeDTO> {
        return importationServiceErrorHandlingBlock(
            codeBlock = {
                val gson = GsonBuilder().defaultGSon()

                personService.importPersonAcademyTimes(
                    token = formatToken(token),
                    filter = gson.toJson(filter),
                    pageInfos = gson.toJson(pageInfos)
                ).getResponseBody(PersonAcademyTimeDTO::class.java)
            }
        )
    }

    suspend fun findPersonByEmail(token: String, email: String, password: String?): SingleValueServiceResponse<PersonDTO?> {
        return singleValueErrorHandlingBlock(
            codeBlock = {
                val dto = FindPersonDTO(email = email, password = password)
                personService.findPersonByEmail(formatToken(token), dto).getResponseBody(PersonDTO::class.java)
            }
        )
    }

}