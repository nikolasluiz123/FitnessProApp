package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.shared.communication.dtos.general.PersonDTO
import br.com.fitnesspro.shared.communication.dtos.general.UserDTO
import br.com.fitnesspro.to.TOPerson
import br.com.fitnesspro.to.TOUser

class PersonModelMapper: AbstractModelMapper() {

    init {
        mapper.typeMap(TOPerson::class.java, Person::class.java).addMappings { mapper ->
            mapper.map(
                { to: TOPerson -> to.user?.id },
                { person: Person, value: String? -> person.userId = value }
            )
        }

        mapper.typeMap(PersonDTO::class.java, Person::class.java).addMappings { mapper ->
            mapper.map(
                { to: PersonDTO -> to.user?.id },
                { person: Person, value: String? -> person.userId = value }
            )
        }
    }

    fun getTOPerson(person: Person, user: User): TOPerson {
        return mapper.map(person, TOPerson::class.java).copy(user = getTOUser(user))
    }

    fun getTOPerson(personDTO: PersonDTO): TOPerson {
        return mapper.map(personDTO, TOPerson::class.java)
    }

    fun getPerson(toPerson: TOPerson): Person {
        return mapper.map(toPerson, Person::class.java).apply {
            toPerson.id?.let { id = it }
        }
    }

    fun getPerson(personDTO: PersonDTO): Person {
        return mapper.map(personDTO, Person::class.java)
    }

    fun getPersonDTO(person: Person, user: User): PersonDTO {
        return mapper.map(person, PersonDTO::class.java).copy(user = getUserDTO(user))
    }

    fun getTOUser(user: User): TOUser {
        return mapper.map(user, TOUser::class.java)
    }

    fun getUserDTO(user: User): UserDTO {
        return mapper.map(user, UserDTO::class.java)
    }

    fun getUser(toUser: TOUser): User {
        return mapper.map(toUser, User::class.java).apply {
            toUser.id?.let { id = it }
        }
    }

    fun getUser(userDTO: UserDTO): User {
        return mapper.map(userDTO, User::class.java)
    }
}