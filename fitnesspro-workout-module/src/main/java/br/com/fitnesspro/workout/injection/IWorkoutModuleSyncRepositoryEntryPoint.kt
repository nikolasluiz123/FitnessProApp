package br.com.fitnesspro.workout.injection

import br.com.fitnesspro.service.data.access.webclient.sync.WorkoutModuleSyncWebClient
import br.com.fitnesspro.local.data.access.dao.ExerciseDAO
import br.com.fitnesspro.local.data.access.dao.ExerciseExecutionDAO
import br.com.fitnesspro.local.data.access.dao.ExercisePreDefinitionDAO
import br.com.fitnesspro.local.data.access.dao.VideoDAO
import br.com.fitnesspro.local.data.access.dao.VideoExerciseDAO
import br.com.fitnesspro.local.data.access.dao.VideoExerciseExecutionDAO
import br.com.fitnesspro.local.data.access.dao.VideoExercisePreDefinitionDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupPreDefinitionDAO
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface IWorkoutModuleSyncRepositoryEntryPoint {

    fun getWorkoutSyncWebClient(): WorkoutModuleSyncWebClient

    fun getWorkoutDAO(): WorkoutDAO

    fun getWorkoutGroupDAO(): WorkoutGroupDAO

    fun getExerciseDAO(): ExerciseDAO

    fun getVideoDAO(): VideoDAO

    fun getVideoExerciseDAO(): VideoExerciseDAO

    fun getExerciseExecutionDAO(): ExerciseExecutionDAO

    fun getVideoExerciseExecutionDAO(): VideoExerciseExecutionDAO

    fun getWorkoutGroupPreDefinitionDAO(): WorkoutGroupPreDefinitionDAO

    fun getExercisePreDefinitionDAO(): ExercisePreDefinitionDAO

    fun getVideoExercisePreDefinitionDAO(): VideoExercisePreDefinitionDAO
}