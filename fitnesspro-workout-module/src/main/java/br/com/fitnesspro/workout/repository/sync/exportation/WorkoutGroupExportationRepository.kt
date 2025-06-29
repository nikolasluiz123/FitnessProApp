package br.com.fitnesspro.workout.repository.sync.exportation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.WorkoutWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.sync.exportation.common.AbstractExportationRepository
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.workout.WorkoutGroup
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.workout.R

class WorkoutGroupExportationRepository(
    context: Context,
    private val workoutGroupDAO: WorkoutGroupDAO,
    private val workoutWebClient: WorkoutWebClient,
    private val personRepository: PersonRepository
): AbstractExportationRepository<WorkoutGroup, WorkoutGroupDAO>(context) {

    override suspend fun getExportationData(
        pageInfos: ExportPageInfos
    ): List<WorkoutGroup> {
        val personId = personRepository.findPersonByUserId(getAuthenticatedUser()?.id!!).id
        return workoutGroupDAO.getExportationData(pageInfos, personId)
    }

    override fun getOperationDAO(): WorkoutGroupDAO {
        return workoutGroupDAO
    }

    override suspend fun callExportationService(
        modelList: List<WorkoutGroup>,
        token: String
    ): ExportationServiceResponse {
        return workoutWebClient.saveWorkoutGroupBatch(token, modelList)
    }

    override fun getDescription(): String {
        return context.getString(R.string.workout_group_exportation_description)
    }

    override fun getModule() = EnumSyncModule.WORKOUT
}