package br.com.fitnesspro.common.repository

import android.content.Context
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.ServiceTokenDAO
import br.com.fitnesspro.mappers.getServiceToken
import br.com.fitnesspro.shared.communication.dtos.serviceauth.ServiceTokenDTO

class ServiceTokenRepository(
    context: Context,
    private val deviceRepository: DeviceRepository,
    private val applicationRepository: ApplicationRepository,
    private val serviceTokenDAO: ServiceTokenDAO,
): FitnessProRepository(context) {

    suspend fun saveTokenInformation(tokens: List<ServiceTokenDTO>) {
        tokens.forEach { tokenServiceDTO ->
            tokenServiceDTO.device?.let {
                applicationRepository.saveApplicationLocally(it.application!!)
                deviceRepository.saveDeviceLocally(it)
            }

            tokenServiceDTO.application?.let {
                applicationRepository.saveApplicationLocally(it)
            }

            val serviceToken = tokenServiceDTO.getServiceToken()

            if (serviceTokenDAO.findById(tokenServiceDTO.id!!) == null) {
                serviceTokenDAO.insert(serviceToken)
            } else {
                serviceTokenDAO.update(serviceToken)
            }
        }
    }
}