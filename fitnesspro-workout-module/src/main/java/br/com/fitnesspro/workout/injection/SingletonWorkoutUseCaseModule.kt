package br.com.fitnesspro.workout.injection

import android.content.Context
import br.com.fitnesspro.workout.repository.ExercisePreDefinitionRepository
import br.com.fitnesspro.workout.repository.ExerciseRepository
import br.com.fitnesspro.workout.repository.WorkoutGroupRepository
import br.com.fitnesspro.workout.usecase.exercise.SaveExerciseUseCase
import br.com.fitnesspro.workout.usecase.workout.EditWorkoutGroupUseCase
import br.com.fitnesspro.workout.usecase.workout.InactivateWorkoutGroupUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SingletonWorkoutUseCaseModule {

    @Provides
    fun provideSaveExerciseUseCase(
        @ApplicationContext context: Context,
        workoutGroupRepository: WorkoutGroupRepository,
        exercisePreDefinitionRepository: ExercisePreDefinitionRepository,
        exerciseRepository: ExerciseRepository
    ): SaveExerciseUseCase {
        return SaveExerciseUseCase(
            context = context,
            workoutGroupRepository = workoutGroupRepository,
            exercisePreDefinitionRepository = exercisePreDefinitionRepository,
            exerciseRepository = exerciseRepository
        )
    }

    @Provides
    fun provideEditWorkoutGroupUseCase(
        @ApplicationContext context: Context,
        workoutGroupRepository: WorkoutGroupRepository
    ): EditWorkoutGroupUseCase {
        return EditWorkoutGroupUseCase(
            context = context,
            workoutGroupRepository = workoutGroupRepository
        )
    }

    @Provides
    fun provideInactivateWorkoutGroupUseCase(
        workoutGroupRepository: WorkoutGroupRepository,
        exerciseRepository: ExerciseRepository
    ): InactivateWorkoutGroupUseCase {
        return InactivateWorkoutGroupUseCase(
            workoutGroupRepository = workoutGroupRepository,
            exerciseRepository = exerciseRepository
        )
    }
}