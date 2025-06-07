package br.com.fitnesspro.workout.repository

import android.content.Context
import br.com.fitnesspro.common.repository.common.FitnessProRepository
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupPreDefinitionDAO
import br.com.fitnesspro.model.workout.predefinition.WorkoutGroupPreDefinition

class WorkoutGroupPreDefinitionRepository(
    context: Context,
    private val workoutGroupPreDefinitionDAO: WorkoutGroupPreDefinitionDAO
): FitnessProRepository(context) {

    suspend fun findById(workoutGroupPreDefinitionId: String?): WorkoutGroupPreDefinition? {
        return workoutGroupPreDefinitionDAO.findById(workoutGroupPreDefinitionId)
    }

}