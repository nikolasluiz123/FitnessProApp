package br.com.fitnesspro.workout.injection

import android.content.Context
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.local.data.access.dao.ExerciseDAO
import br.com.fitnesspro.local.data.access.dao.ExercisePreDefinitionDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupDAO
import br.com.fitnesspro.workout.repository.ExercisePreDefinitionRepository
import br.com.fitnesspro.workout.repository.ExerciseRepository
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
        workoutGroupDAO: WorkoutGroupDAO
    ): ExerciseRepository {
        return ExerciseRepository(
            context = context,
            exerciseDAO = exerciseDAO,
            workoutGroupDAO = workoutGroupDAO
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
}