package br.com.fitnesspro.scheduler.repository.sync.importation

import android.content.Context
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.sync.importation.common.AbstractImportationRepository
import br.com.fitnesspro.common.repository.sync.importation.common.ImportSegregationResult
import br.com.fitnesspro.local.data.access.dao.common.MaintenanceDAO
import br.com.fitnesspro.mappers.getReport
import br.com.fitnesspro.mappers.getScheduler
import br.com.fitnesspro.mappers.getSchedulerReport
import br.com.fitnesspro.model.base.BaseModel
import br.com.fitnesspro.model.general.report.Report
import br.com.fitnesspro.model.general.report.SchedulerReport
import br.com.fitnesspro.model.scheduler.Scheduler
import br.com.fitnesspro.scheduler.injection.ISchedulerModuleSyncRepositoryEntryPoint
import br.com.fitnesspro.shared.communication.dtos.common.BaseDTO
import br.com.fitnesspro.shared.communication.dtos.general.interfaces.IReportDTO
import br.com.fitnesspro.shared.communication.dtos.general.interfaces.ISchedulerReportDTO
import br.com.fitnesspro.shared.communication.dtos.scheduler.interfaces.ISchedulerDTO
import br.com.fitnesspro.shared.communication.dtos.sync.SchedulerModuleSyncDTO
import br.com.fitnesspro.shared.communication.enums.report.EnumReportContext
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.query.filter.importation.SchedulerModuleImportationFilter
import br.com.fitnesspro.shared.communication.responses.ImportationServiceResponse
import dagger.hilt.android.EntryPointAccessors
import java.time.LocalDateTime
import kotlin.reflect.KClass

class SchedulerModuleImportationRepository(
    context: Context,
    private val personRepository: PersonRepository
): AbstractImportationRepository<SchedulerModuleSyncDTO, SchedulerModuleImportationFilter>(context) {

    private val entryPoint = EntryPointAccessors.fromApplication(context, ISchedulerModuleSyncRepositoryEntryPoint::class.java)

    override suspend fun getImportationData(
        token: String,
        filter: SchedulerModuleImportationFilter,
        pageInfos: ImportPageInfos
    ): ImportationServiceResponse<SchedulerModuleSyncDTO> {
        return entryPoint.getSchedulerSyncWebClient().import(token, filter, pageInfos)
    }

    override suspend fun getImportFilter(lastUpdateDate: LocalDateTime?): SchedulerModuleImportationFilter {
        val personId = personRepository.findPersonByUserId(getAuthenticatedUser()!!.id).id

        return SchedulerModuleImportationFilter(
            lastUpdateDate = lastUpdateDate,
            reportContext = EnumReportContext.SCHEDULERS_REPORT,
            personId = personId
        )
    }

    override suspend fun executeSegregation(dto: SchedulerModuleSyncDTO): List<ImportSegregationResult<BaseModel>> {
        val result = mutableListOf<ImportSegregationResult<BaseModel>>()

        segregate(
            dtoList = dto.schedulers,
            hasEntityWithId = entryPoint.getSchedulerDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.reports,
            hasEntityWithId = entryPoint.getReportDAO()::hasEntityWithId
        )?.let(result::add)

        segregate(
            dtoList = dto.schedulerReports,
            hasEntityWithId = entryPoint.getSchedulerReportDAO()::hasEntityWithId
        )?.let(result::add)

        return result
    }

    override fun convertDTOToEntity(dto: BaseDTO): BaseModel {
        return when (dto) {
            is ISchedulerDTO -> dto.getScheduler()
            is IReportDTO -> dto.getReport()
            is ISchedulerReportDTO -> dto.getSchedulerReport()
            else -> {
                throw IllegalArgumentException("Não foi possível converter o DTO. Classe de modelo inválida")
            }
        }
    }

    override fun getMaintenanceDAO(modelClass: KClass<out BaseModel>): MaintenanceDAO<out BaseModel> {
        return when (modelClass) {
            Scheduler::class -> entryPoint.getSchedulerDAO()
            Report::class -> entryPoint.getReportDAO()
            SchedulerReport::class -> entryPoint.getSchedulerReportDAO()
            else -> throw IllegalArgumentException("Não foi possível recuperar o DAO. Classe de modelo inválida.")
        }
    }
}