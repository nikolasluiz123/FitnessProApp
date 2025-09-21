package br.com.fitnesspro.scheduler.repository.sync.exportation

import android.content.Context
import br.com.fitnesspro.common.repository.sync.exportation.common.AbstractExportationRepository
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.mappers.getReportDTO
import br.com.fitnesspro.mappers.getSchedulerDTO
import br.com.fitnesspro.mappers.getSchedulerReportDTO
import br.com.fitnesspro.model.base.IntegratedModel
import br.com.fitnesspro.model.enums.EnumReportContext
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.enums.EnumUserType.NUTRITIONIST
import br.com.fitnesspro.model.enums.EnumUserType.PERSONAL_TRAINER
import br.com.fitnesspro.model.general.report.Report
import br.com.fitnesspro.model.general.report.SchedulerReport
import br.com.fitnesspro.model.scheduler.Scheduler
import br.com.fitnesspro.scheduler.injection.ISchedulerModuleSyncRepositoryEntryPoint
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumSchedulerType.SUGGESTION
import br.com.fitnesspro.scheduler.usecase.scheduler.enums.EnumSchedulerType.UNIQUE
import br.com.fitnesspro.shared.communication.dtos.sync.SchedulerModuleSyncDTO
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import dagger.hilt.android.EntryPointAccessors
import kotlin.reflect.KClass

class SchedulerModuleExportationRepository(context: Context): AbstractExportationRepository<SchedulerModuleSyncDTO>(context) {

    private val entryPoint = EntryPointAccessors.fromApplication(context, ISchedulerModuleSyncRepositoryEntryPoint::class.java)

    override suspend fun getExportationData(pageSize: Int): Map<KClass<out IntegratedModel>, List<IntegratedModel>> {
        val map = mutableMapOf<KClass<out IntegratedModel>, List<IntegratedModel>>()

        map.put(Scheduler::class, entryPoint.getSchedulerDAO().getExportationData(pageSize))
        map.put(Report::class, entryPoint.getReportDAO().getExportationData(EnumReportContext.SCHEDULERS_REPORT, pageSize))
        map.put(SchedulerReport::class, entryPoint.getSchedulerReportDAO().getExportationData(pageSize))

        return map
    }

    override suspend fun getExportationDTO(models: Map<KClass<out IntegratedModel>, List<IntegratedModel>>): SchedulerModuleSyncDTO {
        val schedulers = models[Scheduler::class]!!.map { scheduler ->
            scheduler as Scheduler

            val userType = getAuthenticatedUser()?.type!!
            val schedulerType =
                if (userType in listOf(PERSONAL_TRAINER, NUTRITIONIST)) UNIQUE else SUGGESTION

            scheduler.getSchedulerDTO(schedulerType.name)
        }
        
        val reports = models[Report::class]!!.map { report ->
            report as Report
            report.getReportDTO()
        }
        
        val schedulerReports = models[SchedulerReport::class]!!.map { schedulerReport ->
            schedulerReport as SchedulerReport
            schedulerReport.getSchedulerReportDTO()
        }
        
        return SchedulerModuleSyncDTO(
            schedulers = schedulers,
            reports = reports,
            schedulerReports = schedulerReports
        )
    }

    override suspend fun callExportationService(
        dto: SchedulerModuleSyncDTO,
        token: String
    ): ExportationServiceResponse {
        return entryPoint.getSchedulerSyncWebClient().export(token, dto)
    }

    override fun getIntegratedMaintenanceDAO(modelClass: KClass<out IntegratedModel>): IntegratedMaintenanceDAO<out IntegratedModel> {
        return when (modelClass) {
            Scheduler::class -> entryPoint.getSchedulerDAO()
            Report::class -> entryPoint.getReportDAO()
            SchedulerReport::class -> entryPoint.getSchedulerReportDAO()
            else -> throw IllegalArgumentException("Não foi possível recuperar o DAO. Classe de modelo inválida.")
        }
    }

    override fun getModule() = EnumSyncModule.SCHEDULER
}