package br.com.fitnesspro.service.data.access.service.general

import br.com.fitnesspro.shared.communication.constants.EndPointsV1.PERSON
import br.com.fitnesspro.shared.communication.constants.EndPointsV1.PERSON_EMAIL
import br.com.fitnesspro.shared.communication.dtos.general.FindPersonDTO
import br.com.fitnesspro.shared.communication.dtos.general.PersonDTO
import br.com.fitnesspro.shared.communication.responses.PersistenceServiceResponse
import br.com.fitnesspro.shared.communication.responses.SingleValueServiceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface IPersonService {

    @POST(PERSON)
    suspend fun savePerson(
        @Header("Authorization") token: String,
        @Body personDTO: PersonDTO
    ): Response<PersistenceServiceResponse<PersonDTO>>

    @POST("$PERSON$PERSON_EMAIL")
    suspend fun findPersonByEmail(
        @Header("Authorization") token: String,
        @Body dto: FindPersonDTO
    ): Response<SingleValueServiceResponse<PersonDTO?>>

}