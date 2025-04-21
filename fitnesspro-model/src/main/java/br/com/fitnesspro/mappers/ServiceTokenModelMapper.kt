package br.com.fitnesspro.mappers

import br.com.fitnesspro.model.authentication.ServiceToken
import br.com.fitnesspro.shared.communication.dtos.serviceauth.ServiceTokenDTO

class ServiceTokenModelMapper: AbstractModelMapper() {

    init {
        mapper.typeMap(ServiceTokenDTO::class.java, ServiceToken::class.java).addMappings { mapper ->
            mapper.map(
                { to: ServiceTokenDTO -> to.user?.id },
                { serviceToken: ServiceToken, value: String? -> serviceToken.userId = value }
            )
        }

        mapper.typeMap(ServiceTokenDTO::class.java, ServiceToken::class.java).addMappings { mapper ->
            mapper.map(
                { to: ServiceTokenDTO -> to.device?.id },
                { serviceToken: ServiceToken, value: String? -> serviceToken.deviceId = value }
            )
        }

        mapper.typeMap(ServiceTokenDTO::class.java, ServiceToken::class.java).addMappings { mapper ->
            mapper.map(
                { to: ServiceTokenDTO -> to.application?.id },
                { serviceToken: ServiceToken, value: String? -> serviceToken.applicationId = value }
            )
        }
    }

    fun getServiceToken(serviceTokenDTO: ServiceTokenDTO): ServiceToken {
        return mapper.map(serviceTokenDTO, ServiceToken::class.java)
    }
}