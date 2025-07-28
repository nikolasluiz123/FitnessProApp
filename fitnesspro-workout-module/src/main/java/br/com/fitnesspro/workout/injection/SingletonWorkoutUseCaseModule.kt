package br.com.fitnesspro.workout.injection

import android.content.Context
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.workout.repository.ExerciseExecutionRepository
import br.com.fitnesspro.workout.repository.ExercisePreDefinitionRepository
import br.com.fitnesspro.workout.repository.ExerciseRepository
import br.com.fitnesspro.workout.repository.VideoRepository
import br.com.fitnesspro.workout.repository.WorkoutGroupRepository
import br.com.fitnesspro.workout.repository.WorkoutRepository
import br.com.fitnesspro.workout.usecase.exercise.InactivateExercisePreDefinitionUseCase
import br.com.fitnesspro.workout.usecase.exercise.InactivateExerciseUseCase
import br.com.fitnesspro.workout.usecase.exercise.SaveExerciseExecutionUseCase
import br.com.fitnesspro.workout.usecase.exercise.SaveExercisePreDefinitionUseCase
import br.com.fitnesspro.workout.usecase.exercise.SaveExerciseUseCase
import br.com.fitnesspro.workout.usecase.exercise.SaveGroupPreDefinitionUseCase
import br.com.fitnesspro.workout.usecase.exercise.video.SaveExerciseVideoUseCase
import br.com.fitnesspro.workout.usecase.exercise.video.SaveVideoExecutionUseCase
import br.com.fitnesspro.workout.usecase.exercise.video.SaveVideoPreDefinitionUseCase
import br.com.fitnesspro.workout.usecase.exercise.video.gallery.SaveExerciseVideoFromGalleryUseCase
import br.com.fitnesspro.workout.usecase.exercise.video.gallery.SaveVideoExecutionFromGalleryUseCase
import br.com.fitnesspro.workout.usecase.exercise.video.gallery.SaveVideoExercisePreDefinitionFromGalleryUseCase
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
        workoutGroupRepository: WorkoutGroupRepository,
        exerciseRepository: ExerciseRepository,
    ): InactivateWorkoutGroupUseCase {
        return InactivateWorkoutGroupUseCase(
            workoutGroupRepository = workoutGroupRepository,
            exerciseRepository = exerciseRepository,
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
        exerciseRepository: ExerciseRepository,
    ): InactivateExerciseUseCase {
        return InactivateExerciseUseCase(
            exerciseRepository = exerciseRepository,
        )
    }

    @Provides
    fun provideInactivateWorkoutUseCase(
        workoutRepository: WorkoutRepository,
    ): InactivateWorkoutUseCase {
        return InactivateWorkoutUseCase(
            workoutRepository = workoutRepository,
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

    @Provides
    fun provideSaveExercisePreDefinitionUseCase(
        @ApplicationContext context: Context,
        exercisePreDefinitionRepository: ExercisePreDefinitionRepository,
        saveVideoPreDefinitionUseCase: SaveVideoPreDefinitionUseCase,
        personRepository: PersonRepository
    ): SaveExercisePreDefinitionUseCase {
        return SaveExercisePreDefinitionUseCase(
            context = context,
            exercisePreDefinitionRepository = exercisePreDefinitionRepository,
            saveVideoPreDefinitionUseCase = saveVideoPreDefinitionUseCase,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideSaveVideoPreDefinitionUseCase(
        @ApplicationContext context: Context,
        videoRepository: VideoRepository
    ): SaveVideoPreDefinitionUseCase {
        return SaveVideoPreDefinitionUseCase(
            context = context,
            videoRepository = videoRepository
        )
    }

    @Provides
    fun provideSaveVideoPreDefinitionFromGalleryUseCase(
        @ApplicationContext context: Context,
        saveVideoPreDefinitionUseCase: SaveVideoPreDefinitionUseCase
    ): SaveVideoExercisePreDefinitionFromGalleryUseCase {
        return SaveVideoExercisePreDefinitionFromGalleryUseCase(
            context = context,
            saveVideoPreDefinitionUseCase = saveVideoPreDefinitionUseCase
        )
    }

    @Provides
    fun provideSaveGroupPreDefinitionUseCase(
        @ApplicationContext context: Context,
        exercisePreDefinitionRepository: ExercisePreDefinitionRepository,
        saveExercisePreDefinitionUseCase: SaveExercisePreDefinitionUseCase,
        personRepository: PersonRepository
    ): SaveGroupPreDefinitionUseCase {
        return SaveGroupPreDefinitionUseCase(
            context = context,
            exercisePreDefinitionRepository = exercisePreDefinitionRepository,
            saveExercisePreDefinitionUseCase = saveExercisePreDefinitionUseCase,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideInactivateExercisePreDefinitionUseCase(
        exercisePreDefinitionRepository: ExercisePreDefinitionRepository,
    ): InactivateExercisePreDefinitionUseCase {
        return InactivateExercisePreDefinitionUseCase(
            exercisePreDefinitionRepository = exercisePreDefinitionRepository,
        )
    }

}