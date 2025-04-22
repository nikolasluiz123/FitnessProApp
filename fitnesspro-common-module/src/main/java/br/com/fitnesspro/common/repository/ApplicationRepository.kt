package br.com.fitnesspro.common.repository

import android.content.Context
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.ApplicationDAO
import br.com.fitnesspro.mappers.getApplication
import br.com.fitnesspro.shared.communication.dtos.serviceauth.ApplicationDTO

class ApplicationRepository(
    context: Context,
    private val applicationDAO: ApplicationDAO,
) : FitnessProRepository(context) {

    suspend fun saveApplicationLocally(applicationDTO: ApplicationDTO) {
        val application = applicationDTO.getApplication()

        if (applicationDAO.findById(applicationDTO.id!!) == null) {
            applicationDAO.insert(application)
        } else {
            applicationDAO.update(application)
        }
    }
}