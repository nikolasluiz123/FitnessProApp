package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.authentication.Application
import br.com.fitnesspro.shared.communication.dtos.serviceauth.ApplicationDTO

class ApplicationModelMapper: AbstractModelMapper() {

    fun getApplication(applicationDTO: ApplicationDTO): Application {
        return mapper.map(applicationDTO, Application::class.java)
    }
}