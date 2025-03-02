package br.com.fitnesspro.scheduler.repository.sync.importation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.scheduler.SchedulerWebClient
import br.com.fitnesspro.common.repository.sync.importation.common.AbstractImportationRepository
import br.com.fitnesspro.local.data.access.dao.SchedulerDAO
import br.com.fitnesspro.model.enums.EnumCompromiseType
import br.com.fitnesspro.model.enums.EnumSchedulerSituation
import br.com.fitnesspro.model.scheduler.Scheduler
import br.com.fitnesspro.model.sync.EnumSyncModule
import br.com.fitnesspro.scheduler.R
import br.com.fitnesspro.shared.communication.dtos.scheduler.SchedulerDTO
import br.com.fitnesspro.shared.communication.filter.CommonImportFilter
import br.com.fitnesspro.shared.communication.paging.ImportPageInfos
import br.com.fitnesspro.shared.communication.responses.ReadServiceResponse
import br.com.fitnesspro.models.scheduler.enums.EnumCompromiseType as EnumCompromiseTypeService
import br.com.fitnesspro.models.scheduler.enums.EnumSchedulerSituation as EnumSchedulerSituationService

class SchedulerImportationRepository(
    context: Context,
    private val schedulerDAO: SchedulerDAO,
    private val webClient: SchedulerWebClient
): AbstractImportationRepository<SchedulerDTO, Scheduler, SchedulerDAO>(context) {

    override fun getDescription(): String {
        return context.getString(R.string.scheduler_importation_descrition)
    }

    override fun getModule() = EnumSyncModule.SCHEDULER

    override suspend fun getImportationData(
        token: String,
        filter: CommonImportFilter,
        pageInfos: ImportPageInfos
    ): ReadServiceResponse<SchedulerDTO> {
        return webClient.importSchedulers(token, filter, pageInfos)
    }

    override suspend fun hasEntityWithId(id: String): Boolean {
        return schedulerDAO.hasSchedulerWithId(id)
    }

    override fun getOperationDAO(): SchedulerDAO {
        return schedulerDAO
    }

    override suspend fun convertDTOToEntity(dto: SchedulerDTO): Scheduler {
        return Scheduler(
            id = dto.id!!,
            academyMemberPersonId = dto.academyMemberPersonId,
            professionalPersonId = dto.professionalPersonId,
            scheduledDate = dto.scheduledDate,
            start = dto.timeStart,
            end = dto.timeEnd,
            canceledDate = dto.canceledDate,
            situation = getEnumSchedulerSituation(dto.situation!!),
            compromiseType = getEnumCompromiseType(dto.compromiseType!!),
            observation = dto.observation,
            active = dto.active
        )
    }

    private fun getEnumCompromiseType(compromiseType: EnumCompromiseTypeService): EnumCompromiseType {
        return when (compromiseType) {
            EnumCompromiseTypeService.FIRST -> EnumCompromiseType.FIRST
            EnumCompromiseTypeService.RECURRENT -> EnumCompromiseType.RECURRENT
        }
    }

    private fun getEnumSchedulerSituation(situation: EnumSchedulerSituationService): EnumSchedulerSituation {
        return when (situation) {
            EnumSchedulerSituationService.SCHEDULED -> EnumSchedulerSituation.SCHEDULED
            EnumSchedulerSituationService.CONFIRMED -> EnumSchedulerSituation.CONFIRMED
            EnumSchedulerSituationService.COMPLETED -> EnumSchedulerSituation.COMPLETED
            EnumSchedulerSituationService.CANCELLED -> EnumSchedulerSituation.CANCELLED
        }
    }
}