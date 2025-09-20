package br.com.fitnesspro.workout.injection

import android.content.Context
import br.com.fitnesspro.common.repository.PersonRepository
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
import br.com.fitnesspro.workout.repository.ExerciseExecutionRepository
import br.com.fitnesspro.workout.repository.ExercisePreDefinitionRepository
import br.com.fitnesspro.workout.repository.ExerciseRepository
import br.com.fitnesspro.workout.repository.VideoRepository
import br.com.fitnesspro.workout.repository.WorkoutGroupRepository
import br.com.fitnesspro.workout.repository.WorkoutRepository
import br.com.fitnesspro.workout.repository.sync.exportation.HealthConnectModuleExportationRepository
import br.com.fitnesspro.workout.repository.sync.exportation.WorkoutModuleExportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.HealthConnectModuleImportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.WorkoutModuleImportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.integration.CaloriesIntegrationRepository
import br.com.fitnesspro.workout.repository.sync.importation.integration.HealthConnectIntegrationRepository
import br.com.fitnesspro.workout.repository.sync.importation.integration.HeartRateIntegrationRepository
import br.com.fitnesspro.workout.repository.sync.importation.integration.SleepIntegrationRepository
import br.com.fitnesspro.workout.repository.sync.importation.integration.StepsIntegrationRepository
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
        workoutGroupDAO: WorkoutGroupDAO,
        personRepository: PersonRepository,
        exerciseRepository: ExerciseRepository,
    ): WorkoutRepository {
        return WorkoutRepository(
            context = context,
            workoutDAO = workoutDAO,
            workoutGroupDAO = workoutGroupDAO,
            personRepository = personRepository,
            exerciseRepository = exerciseRepository,
        )
    }

    @Provides
    fun provideWorkoutGroupRepository(
        @ApplicationContext context: Context,
        workoutGroupDAO: WorkoutGroupDAO,
        exerciseDAO: ExerciseDAO,
    ): WorkoutGroupRepository {
        return WorkoutGroupRepository(
            context = context,
            workoutGroupDAO = workoutGroupDAO,
            exerciseDAO = exerciseDAO,
        )
    }

    @Provides
    fun provideExerciseRepository(
        @ApplicationContext context: Context,
        exerciseDAO: ExerciseDAO,
        workoutGroupRepository: WorkoutGroupRepository,
        videoRepository: VideoRepository
    ): ExerciseRepository {
        return ExerciseRepository(
            context = context,
            exerciseDAO = exerciseDAO,
            workoutGroupRepository = workoutGroupRepository,
            videoRepository = videoRepository
        )
    }

    @Provides
    fun provideExercisePreDefinitionRepository(
        @ApplicationContext context: Context,
        exercisePreDefinitionDAO: ExercisePreDefinitionDAO,
        workoutGroupPreDefinitionDAO: WorkoutGroupPreDefinitionDAO,
        videoRepository: VideoRepository
    ): ExercisePreDefinitionRepository {
        return ExercisePreDefinitionRepository(
            context = context,
            exercisePreDefinitionDAO = exercisePreDefinitionDAO,
            workoutGroupPreDefinitionDAO = workoutGroupPreDefinitionDAO,
            videoRepository = videoRepository
        )
    }

    @Provides
    fun provideVideoRepository(
        @ApplicationContext context: Context,
        videoDAO: VideoDAO,
        videoExerciseDAO: VideoExerciseDAO,
        videoExerciseExecutionDAO: VideoExerciseExecutionDAO,
        videoExercisePreDefinitionDAO: VideoExercisePreDefinitionDAO,
    ): VideoRepository {
        return VideoRepository(
            context = context,
            videoDAO = videoDAO,
            videoExerciseDAO = videoExerciseDAO,
            videoExerciseExecutionDAO = videoExerciseExecutionDAO,
            videoExercisePreDefinitionDAO = videoExercisePreDefinitionDAO,
        )
    }

    @Provides
    fun provideExerciseExecutionRepository(
        @ApplicationContext context: Context,
        exerciseExecutionDAO: ExerciseExecutionDAO,
        videoRepository: VideoRepository,
        personRepository: PersonRepository
    ): ExerciseExecutionRepository {
        return ExerciseExecutionRepository(
            context = context,
            exerciseExecutionDAO = exerciseExecutionDAO,
            videoRepository = videoRepository,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideWorkoutModuleImportationRepository(
        @ApplicationContext context: Context,
        personRepository: PersonRepository
    ): WorkoutModuleImportationRepository {
        return WorkoutModuleImportationRepository(
            context = context,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideWorkoutModuleExportationRepository(
        @ApplicationContext context: Context,
        personRepository: PersonRepository
    ): WorkoutModuleExportationRepository {
        return WorkoutModuleExportationRepository(
            context = context,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideCaloriesIntegrationRepository(
        @ApplicationContext context: Context,
    ): CaloriesIntegrationRepository {
        return CaloriesIntegrationRepository(
            context = context,
        )
    }

    @Provides
    fun provideHeartRateIntegrationRepository(
        @ApplicationContext context: Context,
    ): HeartRateIntegrationRepository {
        return HeartRateIntegrationRepository(
            context = context,
        )
    }

    @Provides
    fun provideSleepIntegrationRepository(
        @ApplicationContext context: Context,
    ): SleepIntegrationRepository {
        return SleepIntegrationRepository(
            context = context,
        )
    }

    @Provides
    fun provideStepsIntegrationRepository(
        @ApplicationContext context: Context,
    ): StepsIntegrationRepository {
        return StepsIntegrationRepository(
            context = context,
        )
    }

    @Provides
    fun provideHealthConnectModuleExportationRepository(
        @ApplicationContext context: Context,
        personRepository: PersonRepository
    ): HealthConnectModuleExportationRepository {
        return HealthConnectModuleExportationRepository(
            context = context,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideHealthConnectModuleImportationRepository(
        @ApplicationContext context: Context,
        personRepository: PersonRepository
    ): HealthConnectModuleImportationRepository {
        return HealthConnectModuleImportationRepository(
            context = context,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideHealthConnectIntegrationRepository(
        @ApplicationContext context: Context,
        personRepository: PersonRepository,
        caloriesIntegrationRepository: CaloriesIntegrationRepository,
        heartRateIntegrationRepository: HeartRateIntegrationRepository,
        sleepIntegrationRepository: SleepIntegrationRepository,
        stepsIntegrationRepository: StepsIntegrationRepository,
    ): HealthConnectIntegrationRepository {
        return HealthConnectIntegrationRepository(
            context = context,
            personRepository = personRepository,
            caloriesIntegrationRepository = caloriesIntegrationRepository,
            heartRateIntegrationRepository = heartRateIntegrationRepository,
            sleepIntegrationRepository = sleepIntegrationRepository,
            stepsIntegrationRepository = stepsIntegrationRepository,
        )
    }
}