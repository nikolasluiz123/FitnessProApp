package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.general.Academy
import br.com.fitnesspro.model.general.PersonAcademyTime
import br.com.fitnesspro.shared.communication.dtos.general.AcademyDTO
import br.com.fitnesspro.shared.communication.dtos.general.PersonAcademyTimeDTO
import br.com.fitnesspro.to.TOAcademy
import br.com.fitnesspro.to.TOPersonAcademyTime

class AcademyModelMapper: AbstractModelMapper() {

    fun getTOAcademy(academy: Academy): TOAcademy {
        return mapper.map(academy, TOAcademy::class.java)
    }

    fun getAcademy(academyDTO: AcademyDTO): Academy {
        return mapper.map(academyDTO, Academy::class.java)
    }

    fun getTOPersonAcademyTime(personAcademyTime: PersonAcademyTime): TOPersonAcademyTime {
        return mapper.map(personAcademyTime, TOPersonAcademyTime::class.java)
    }

    fun getPersonAcademyTime(toPersonAcademyTime: TOPersonAcademyTime): PersonAcademyTime {
        return mapper.map(toPersonAcademyTime, PersonAcademyTime::class.java).apply {
            toPersonAcademyTime.id?.let { id = it }
        }
    }

    fun getPersonAcademyTime(personAcademyTimeDTO: PersonAcademyTimeDTO): PersonAcademyTime {
        return mapper.map(personAcademyTimeDTO, PersonAcademyTime::class.java)
    }

    fun getPersonAcademyTimeDTO(personAcademyTime: PersonAcademyTime): PersonAcademyTimeDTO {
        return mapper.map(personAcademyTime, PersonAcademyTimeDTO::class.java)
    }
}