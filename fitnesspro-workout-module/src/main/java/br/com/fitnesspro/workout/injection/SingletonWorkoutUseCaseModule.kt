package br.com.fitnesspro.workout.injection

import android.content.Context
import br.com.fitnesspro.workout.repository.ExerciseExecutionRepository
import br.com.fitnesspro.workout.repository.ExercisePreDefinitionRepository
import br.com.fitnesspro.workout.repository.ExerciseRepository
import br.com.fitnesspro.workout.repository.VideoRepository
import br.com.fitnesspro.workout.repository.WorkoutGroupRepository
import br.com.fitnesspro.workout.repository.WorkoutRepository
import br.com.fitnesspro.workout.usecase.exercise.InactivateExerciseUseCase
import br.com.fitnesspro.workout.usecase.exercise.SaveExerciseExecutionUseCase
import br.com.fitnesspro.workout.usecase.exercise.SaveExerciseUseCase
import br.com.fitnesspro.workout.usecase.exercise.video.SaveExerciseVideoUseCase
import br.com.fitnesspro.workout.usecase.exercise.video.SaveVideoExecutionUseCase
import br.com.fitnesspro.workout.usecase.exercise.video.gallery.SaveExerciseVideoFromGalleryUseCase
import br.com.fitnesspro.workout.usecase.exercise.video.gallery.SaveVideoExecutionFromGalleryUseCase
import br.com.fitnesspro.workout.usecase.workout.EditWorkoutGroupUseCase
import br.com.fitnesspro.workout.usecase.workout.InactivateWorkoutGroupUseCase
import br.com.fitnesspro.workout.usecase.workout.InactivateWorkoutUseCase
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
        @ApplicationContext context: Context,
        workoutGroupRepository: WorkoutGroupRepository,
        exerciseRepository: ExerciseRepository,
        videoRepository: VideoRepository
    ): InactivateWorkoutGroupUseCase {
        return InactivateWorkoutGroupUseCase(
            context = context,
            workoutGroupRepository = workoutGroupRepository,
            exerciseRepository = exerciseRepository,
            videoRepository = videoRepository
        )
    }

    @Provides
    fun provideSaveExerciseVideoUseCase(
        @ApplicationContext context: Context,
        videoRepository: VideoRepository,
    ): SaveExerciseVideoUseCase {
        return SaveExerciseVideoUseCase(
            context = context,
            videoRepository = videoRepository
        )
    }

    @Provides
    fun provideSaveExerciseVideoFromGalleryUseCase(
        @ApplicationContext context: Context,
        saveExerciseVideoUseCase: SaveExerciseVideoUseCase,
    ): SaveExerciseVideoFromGalleryUseCase {
        return SaveExerciseVideoFromGalleryUseCase(
            context = context,
            saveExerciseVideoUseCase = saveExerciseVideoUseCase,
        )
    }

    @Provides
    fun provideInactivateExerciseUseCase(
        @ApplicationContext context: Context,
        exerciseRepository: ExerciseRepository,
        videoRepository: VideoRepository
    ): InactivateExerciseUseCase {
        return InactivateExerciseUseCase(
            context = context,
            exerciseRepository = exerciseRepository,
            videoRepository = videoRepository
        )
    }

    @Provides
    fun provideInactivateWorkoutUseCase(
        @ApplicationContext context: Context,
        workoutRepository: WorkoutRepository,
        videoRepository: VideoRepository
    ): InactivateWorkoutUseCase {
        return InactivateWorkoutUseCase(
            context = context,
            workoutRepository = workoutRepository,
            videoRepository = videoRepository
        )
    }

    @Provides
    fun provideSaveExerciseExecutionUseCase(
        @ApplicationContext context: Context,
        exerciseExecutionRepository: ExerciseExecutionRepository,
        saveVideoExecutionUseCase: SaveVideoExecutionUseCase
    ): SaveExerciseExecutionUseCase {
        return SaveExerciseExecutionUseCase(
            context = context,
            exerciseExecutionRepository = exerciseExecutionRepository,
            saveVideoExecutionUseCase = saveVideoExecutionUseCase
        )
    }

    @Provides
    fun provideSaveVideoExecutionFromGalleryUseCase(
        @ApplicationContext context: Context,
        saveVideoExecutionUseCase: SaveVideoExecutionUseCase
    ): SaveVideoExecutionFromGalleryUseCase {
        return SaveVideoExecutionFromGalleryUseCase(
            context = context,
            saveVideoExecutionUseCase = saveVideoExecutionUseCase
        )
    }

    @Provides
    fun provideSaveVideoExecutionUseCase(
        @ApplicationContext context: Context,
        videoRepository: VideoRepository
    ): SaveVideoExecutionUseCase {
        return SaveVideoExecutionUseCase(
            context = context,
            videoRepository = videoRepository
        )
    }

}