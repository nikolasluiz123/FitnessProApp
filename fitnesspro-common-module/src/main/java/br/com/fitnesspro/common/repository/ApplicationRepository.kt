package br.com.fitnesspro.common.repository

import android.content.Context
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.ApplicationDAO
import br.com.fitnesspro.mappers.ApplicationModelMapper
import br.com.fitnesspro.shared.communication.dtos.serviceauth.ApplicationDTO

class ApplicationRepository(
    context: Context,
    private val applicationDAO: ApplicationDAO,
    private val applicationModelMapper: ApplicationModelMapper
) : FitnessProRepository(context) {

    suspend fun saveApplicationLocally(applicationDTO: ApplicationDTO) {
        val application = applicationModelMapper.getApplication(applicationDTO)

        if (applicationDAO.findById(applicationDTO.id!!) == null) {
            applicationDAO.insert(application)
        } else {
            applicationDAO.update(application)
        }
    }
}