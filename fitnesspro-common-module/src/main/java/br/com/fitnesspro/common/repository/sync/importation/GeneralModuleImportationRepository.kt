package br.com.fitnesspro.common.repository.sync.importation

import android.content.Context
import br.com.fitnesspro.common.injection.IGeneralModuleSyncRepositoryEntryPoint
import br.com.fitnesspro.common.repository.sync.importation.common.AbstractImportationRepository
import br.com.fitnesspro.common.repository.sync.importation.common.ImportSegregationResult
import br.com.fitnesspro.local.data.access.dao.common.MaintenanceDAO
import br.com.fitnesspro.mappers.getAcademy
import br.com.fitnesspro.mappers.getPerson
import br.com.fitnesspro.mappers.getPersonAcademyTime
import br.com.fitnesspro.mappers.getSchedulerConfig
import br.com.fitnesspro.mappers.getUser
import br.com.fitnesspro.model.base.BaseModel
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.general.Academy
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.PersonAcademyTime
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.model.scheduler.SchedulerConfig
import br.com.fitnesspro.shared.communication.dtos.common.BaseDTO
import br.com.fitnesspro.shared.communication.dtos.general.interfaces.IAcademyDTO
import br.com.fitnesspro.shared.communication.dtos.general.interfaces.IPersonAcademyTimeDTO
import br.com.fitnesspro.shared.communication.dtos.general.interfaces.IPersonDTO
import br.com.fitnesspro.shared.communication.dtos.general.interfaces.IUserDTO
import br.com.fitnesspro.shared.communication.dtos.scheduler.interfaces.ISchedulerConfigDTO
import br.com.fitnesspro.shared.communication.dtos.sync.GeneralModuleSyncDTO
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.CommonImportFilter
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import dagger.hilt.android.EntryPointAccessors
import java.time.LocalDateTime
import kotlin.reflect.KClass

class GeneralModuleImportationRepository(context: Context): AbstractImportationRepository<GeneralModuleSyncDTO, CommonImportFilter>(context) {

    private val entryPoint = EntryPointAccessors.fromApplication(context, IGeneralModuleSyncRepositoryEntryPoint::class.java)

    override suspend fun getImportationData(
        token: String,
        filter: CommonImportFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<GeneralModuleSyncDTO> {
        return entryPoint.getGeneralSyncWebClient().import(token, filter, pageInfos)
    }

    override suspend fun executeSegregation(dto: GeneralModuleSyncDTO): List<ImportSegregationResult<BaseModel>> {
        val result = mutableListOf<ImportSegregationResult<BaseModel>>()

        segregate(
            dtoList = dto.academies,
            hasEntityWithId = entryPoint.getAcademyDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.persons.mapNotNull { it.user },
            hasEntityWithId = entryPoint.getUserDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.persons,
            hasEntityWithId = entryPoint.getPersonDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.personAcademyTimes,
            hasEntityWithId = entryPoint.getPersonAcademyTimeDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.schedulerConfigs,
            hasEntityWithId = entryPoint.getSchedulerConfigDAO()::hasEntityWithId
        )?.let(result::add)

        return result
    }

    override fun convertDTOToEntity(dto: BaseDTO): BaseModel {
        return when (dto) {
            is IAcademyDTO -> dto.getAcademy()
            is IUserDTO -> dto.getUser()
            is IPersonDTO -> dto.getPerson()
            is IPersonAcademyTimeDTO -> dto.getPersonAcademyTime()
            is ISchedulerConfigDTO -> dto.getSchedulerConfig()
            else -> {
                throw IllegalArgumentException("Não foi possível converter o DTO. Classe de modelo inválida")
            }
        }
    }

    override fun getMaintenanceDAO(modelClass: KClass<out BaseModel>): MaintenanceDAO<out BaseModel> {
        return when (modelClass) {
            Academy::class -> entryPoint.getAcademyDAO()
            Person::class -> entryPoint.getPersonDAO()
            User::class -> entryPoint.getUserDAO()
            PersonAcademyTime::class -> entryPoint.getPersonAcademyTimeDAO()
            SchedulerConfig::class -> entryPoint.getSchedulerConfigDAO()
            else -> throw IllegalArgumentException("Não foi possível recuperar o DAO. Classe de modelo inválida.")
        }
    }

    override fun getListModelClassesNames(): List<String> {
        return listOf(
            Academy::class.simpleName!!,
            Person::class.simpleName!!,
            User::class.simpleName!!,
            PersonAcademyTime::class.simpleName!!,
            SchedulerConfig::class.simpleName!!
        )
    }

    override fun getCursorDataFrom(syncDTO: GeneralModuleSyncDTO): MutableMap<String, LocalDateTime?> {
        val cursorTimestampMap = mutableMapOf<String, LocalDateTime?>()

        syncDTO.academies.populateCursorInfos(cursorTimestampMap, Academy::class)
        syncDTO.persons.populateCursorInfos(cursorTimestampMap, Person::class)
        syncDTO.persons.mapNotNull { it.user }.populateCursorInfos(cursorTimestampMap, User::class)
        syncDTO.personAcademyTimes.populateCursorInfos(cursorTimestampMap, PersonAcademyTime::class)
        syncDTO.schedulerConfigs.populateCursorInfos(cursorTimestampMap, SchedulerConfig::class)

        return cursorTimestampMap
    }

    override fun getModule() = EnumSyncModule.GENERAL
}