package br.com.fitnesspro.service.data.access.webclient.general

import android.content.Context
import br.com.fitnesspro.service.data.access.extensions.getResponseBody
import br.com.fitnesspro.service.data.access.service.general.IPersonService
import br.com.fitnesspro.service.data.access.webclient.common.FitnessProWebClient
import br.com.fitnesspro.mappers.getPersonDTO
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.shared.communication.dtos.general.FindPersonDTO
import br.com.fitnesspro.shared.communication.dtos.general.PersonDTO
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import br.com.fitnesspro.shared.communication.responses.SingleValueServiceResponse

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

    suspend fun findPersonByEmail(token: String, email: String, password: String?): SingleValueServiceResponse<PersonDTO?> {
        return singleValueErrorHandlingBlock(
            codeBlock = {
                val dto = FindPersonDTO(email = email, password = password)
                personService.findPersonByEmail(formatToken(token), dto).getResponseBody(PersonDTO::class.java)
            }
        )
    }

}