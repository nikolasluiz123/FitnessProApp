package br.com.fitnesspro.common.repository.sync.exportation

import android.content.Context
import br.com.fitnesspro.common.injection.IGeneralModuleSyncRepositoryEntryPoint
import br.com.fitnesspro.common.repository.sync.exportation.common.AbstractExportationRepository
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.mappers.getPersonAcademyTimeDTO
import br.com.fitnesspro.mappers.getPersonDTO
import br.com.fitnesspro.mappers.getSchedulerConfigDTO
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.PersonAcademyTime
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.model.scheduler.SchedulerConfig
import br.com.fitnesspro.shared.communication.dtos.sync.GeneralModuleSyncDTO
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import dagger.hilt.android.EntryPointAccessors
import kotlin.reflect.KClass

class GeneralModuleExportationRepository(context: Context): AbstractExportationRepository<GeneralModuleSyncDTO>(context) {

    private val entryPoint = EntryPointAccessors.fromApplication(context, IGeneralModuleSyncRepositoryEntryPoint::class.java)

    override suspend fun getExportationData(pageSize: Int): Map<KClass<out IntegratedModel>, List<IntegratedModel>> {
        val map = mutableMapOf<KClass<out IntegratedModel>, List<IntegratedModel>>()

        map.put(Person::class, entryPoint.getPersonDAO().getExportationData(pageSize))
        map.put(PersonAcademyTime::class, entryPoint.getPersonAcademyTimeDAO().getExportationData(pageSize))
        map.put(SchedulerConfig::class, entryPoint.getSchedulerConfigDAO().getExportationData(pageSize))

        return map
    }

    override suspend fun getExportationDTO(models: Map<KClass<out IntegratedModel>, List<IntegratedModel>>): GeneralModuleSyncDTO {
        val persons = models[Person::class]!!.map { person ->
            person as Person

            val user = entryPoint.getUserDAO().findById(person.userId!!)!!
            person.getPersonDTO(user)
        }

        val personAcademyTimes = models[PersonAcademyTime::class]!!.map { personAcademyTime ->
            personAcademyTime as PersonAcademyTime
            personAcademyTime.getPersonAcademyTimeDTO()
        }

        val schedulerConfigs = models[SchedulerConfig::class]!!.map { schedulerConfig ->
            schedulerConfig as SchedulerConfig
            schedulerConfig.getSchedulerConfigDTO()
        }

        return GeneralModuleSyncDTO(
            persons = persons,
            personAcademyTimes = personAcademyTimes,
            schedulerConfigs = schedulerConfigs
        )
    }

    override suspend fun callExportationService(
        dto: GeneralModuleSyncDTO,
        token: String
    ): ExportationServiceResponse {
        return entryPoint.getGeneralSyncWebClient().export(token, dto)
    }

    override fun getIntegratedMaintenanceDAO(modelClass: KClass<out IntegratedModel>): IntegratedMaintenanceDAO<out IntegratedModel> {
        return when (modelClass) {
            Person::class -> entryPoint.getPersonDAO()
            User::class -> entryPoint.getUserDAO()
            PersonAcademyTime::class -> entryPoint.getPersonAcademyTimeDAO()
            SchedulerConfig::class -> entryPoint.getSchedulerConfigDAO()
            else -> throw IllegalArgumentException("Não foi possível recuperar o DAO. Classe de modelo inválida.")
        }
    }
}