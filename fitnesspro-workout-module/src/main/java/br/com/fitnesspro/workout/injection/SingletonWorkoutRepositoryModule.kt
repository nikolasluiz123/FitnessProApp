package br.com.fitnesspro.workout.injection

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.ExerciseWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.local.data.access.dao.ExerciseDAO
import br.com.fitnesspro.local.data.access.dao.ExercisePreDefinitionDAO
import br.com.fitnesspro.local.data.access.dao.VideoDAO
import br.com.fitnesspro.local.data.access.dao.VideoExerciseDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupPreDefinitionDAO
import br.com.fitnesspro.workout.repository.ExercisePreDefinitionRepository
import br.com.fitnesspro.workout.repository.ExerciseRepository
import br.com.fitnesspro.workout.repository.VideoRepository
import br.com.fitnesspro.workout.repository.WorkoutGroupPreDefinitionRepository
import br.com.fitnesspro.workout.repository.WorkoutGroupRepository
import br.com.fitnesspro.workout.repository.WorkoutRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SingletonWorkoutRepositoryModule {

    @Provides
    fun provideWorkoutRepository(
        @ApplicationContext context: Context,
        workoutDAO: WorkoutDAO,
        personRepository: PersonRepository
    ): WorkoutRepository {
        return WorkoutRepository(
            context = context,
            workoutDAO = workoutDAO,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideWorkoutGroupRepository(
        @ApplicationContext context: Context,
        workoutGroupDAO: WorkoutGroupDAO,
        exerciseDAO: ExerciseDAO
    ): WorkoutGroupRepository {
        return WorkoutGroupRepository(
            context = context,
            workoutGroupDAO = workoutGroupDAO,
            exerciseDAO = exerciseDAO
        )
    }

    @Provides
    fun provideExerciseRepository(
        @ApplicationContext context: Context,
        exerciseDAO: ExerciseDAO,
        workoutGroupRepository: WorkoutGroupRepository,
        exerciseWebClient: ExerciseWebClient,
    ): ExerciseRepository {
        return ExerciseRepository(
            context = context,
            exerciseDAO = exerciseDAO,
            workoutGroupRepository = workoutGroupRepository,
            exerciseWebClient = exerciseWebClient
        )
    }

    @Provides
    fun provideExercisePreDefinitionRepository(
        @ApplicationContext context: Context,
        exercisePreDefinitionDAO: ExercisePreDefinitionDAO
    ): ExercisePreDefinitionRepository {
        return ExercisePreDefinitionRepository(
            context = context,
            exercisePreDefinitionDAO = exercisePreDefinitionDAO
        )
    }

    @Provides
    fun provideWorkoutGroupPreDefinitionRepository(
        @ApplicationContext context: Context,
        workoutGroupPreDefinitionDAO: WorkoutGroupPreDefinitionDAO
    ): WorkoutGroupPreDefinitionRepository {
        return WorkoutGroupPreDefinitionRepository(
            context = context,
            workoutGroupPreDefinitionDAO = workoutGroupPreDefinitionDAO
        )
    }

    @Provides
    fun provideVideoRepository(
        @ApplicationContext context: Context,
        videoDAO: VideoDAO,
        videoExerciseDAO: VideoExerciseDAO
    ): VideoRepository {
        return VideoRepository(
            context = context,
            videoDAO = videoDAO,
            videoExerciseDAO = videoExerciseDAO
        )
    }
}