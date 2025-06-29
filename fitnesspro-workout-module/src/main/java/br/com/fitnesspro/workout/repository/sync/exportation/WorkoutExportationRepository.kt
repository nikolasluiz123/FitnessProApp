package br.com.fitnesspro.workout.repository.sync.exportation

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.WorkoutWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.sync.exportation.common.AbstractExportationRepository
import br.com.fitnesspro.local.data.access.dao.WorkoutDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.enums.EnumSyncModule
import br.com.fitnesspro.model.workout.Workout
import br.com.fitnesspro.shared.communication.responses.ExportationServiceResponse
import br.com.fitnesspro.workout.R

class WorkoutExportationRepository(
    context: Context,
    private val workoutDAO: WorkoutDAO,
    private val workoutWebClient: WorkoutWebClient,
    private val personRepository: PersonRepository
): AbstractExportationRepository<Workout, WorkoutDAO>(context) {

    override suspend fun getExportationData(
        pageInfos: ExportPageInfos
    ): List<Workout> {
        val personId = personRepository.findPersonByUserId(getAuthenticatedUser()?.id!!).id
        return workoutDAO.getExportationData(pageInfos, personId)
    }

    override fun getOperationDAO(): WorkoutDAO {
        return workoutDAO
    }

    override suspend fun callExportationService(
        modelList: List<Workout>,
        token: String
    ): ExportationServiceResponse {
        return workoutWebClient.saveWorkoutBatch(token, modelList)
    }

    override fun getDescription(): String {
        return context.getString(R.string.workout_exportation_description)
    }

    override fun getModule() = EnumSyncModule.WORKOUT
}