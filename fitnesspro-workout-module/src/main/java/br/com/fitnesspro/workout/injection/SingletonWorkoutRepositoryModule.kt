package br.com.fitnesspro.workout.injection

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.ExerciseWebClient
import br.com.fitnesspor.service.data.access.webclient.workout.WorkoutWebClient
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
import br.com.fitnesspro.workout.repository.sync.exportation.ExerciseExecutionExportationRepository
import br.com.fitnesspro.workout.repository.sync.exportation.ExerciseExportationRepository
import br.com.fitnesspro.workout.repository.sync.exportation.ExercisePreDefinitionExportationRepository
import br.com.fitnesspro.workout.repository.sync.exportation.VideoExerciseExecutionExportationRepository
import br.com.fitnesspro.workout.repository.sync.exportation.VideoExerciseExportationRepository
import br.com.fitnesspro.workout.repository.sync.exportation.VideoExercisePreDefinitionExportationRepository
import br.com.fitnesspro.workout.repository.sync.exportation.VideoExportationRepository
import br.com.fitnesspro.workout.repository.sync.exportation.WorkoutExportationRepository
import br.com.fitnesspro.workout.repository.sync.exportation.WorkoutGroupExportationRepository
import br.com.fitnesspro.workout.repository.sync.exportation.WorkoutGroupPreDefinitionExportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.ExerciseExecutionImportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.ExerciseImportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.ExercisePreDefinitionImportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.VideoExerciseExecutionImportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.VideoExerciseImportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.VideoExercisePreDefinitionImportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.VideoImportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.WorkoutGroupImportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.WorkoutGroupPreDefinitionImportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.WorkoutImportationRepository
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
    fun provideWorkoutImportationRepository(
        @ApplicationContext context: Context,
        workoutDAO: WorkoutDAO,
        workoutWebClient: WorkoutWebClient,
        personRepository: PersonRepository
    ): WorkoutImportationRepository {
        return WorkoutImportationRepository(
            context = context,
            workoutDAO = workoutDAO,
            webClient = workoutWebClient,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideWorkoutGroupImportationRepository(
        @ApplicationContext context: Context,
        workoutGroupDAO: WorkoutGroupDAO,
        webClient: WorkoutWebClient,
        personRepository: PersonRepository
        ): WorkoutGroupImportationRepository {
        return WorkoutGroupImportationRepository(
            context = context,
            workoutGroupDAO = workoutGroupDAO,
            webClient = webClient,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideExerciseImportationRepository(
        @ApplicationContext context: Context,
        exerciseDAO: ExerciseDAO,
        exerciseWebClient: ExerciseWebClient,
        personRepository: PersonRepository
    ): ExerciseImportationRepository {
        return ExerciseImportationRepository(
            context = context,
            exerciseDAO = exerciseDAO,
            webClient = exerciseWebClient,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideVideoImportationRepository(
        @ApplicationContext context: Context,
        videoDAO: VideoDAO,
        webClient: ExerciseWebClient,
        personRepository: PersonRepository
    ): VideoImportationRepository {
        return VideoImportationRepository(
            context = context,
            videoDAO = videoDAO,
            webClient = webClient,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideVideoExerciseImportationRepository(
        @ApplicationContext context: Context,
        videoExerciseDAO: VideoExerciseDAO,
        webClient: ExerciseWebClient,
        personRepository: PersonRepository
    ): VideoExerciseImportationRepository {
        return VideoExerciseImportationRepository(
            context = context,
            videoExerciseDAO = videoExerciseDAO,
            webClient = webClient,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideWorkoutExportationRepository(
        @ApplicationContext context: Context,
        workoutDAO: WorkoutDAO,
        workoutWebClient: WorkoutWebClient,
        personRepository: PersonRepository
    ): WorkoutExportationRepository {
        return WorkoutExportationRepository(
            context = context,
            workoutDAO = workoutDAO,
            workoutWebClient = workoutWebClient,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideWorkoutGroupExportationRepository(
        @ApplicationContext context: Context,
        workoutGroupDAO: WorkoutGroupDAO,
        workoutWebClient: WorkoutWebClient,
        personRepository: PersonRepository
    ): WorkoutGroupExportationRepository {
        return WorkoutGroupExportationRepository(
            context = context,
            workoutGroupDAO = workoutGroupDAO,
            workoutWebClient = workoutWebClient,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideExerciseExportationRepository(
        @ApplicationContext context: Context,
        exerciseDAO: ExerciseDAO,
        workoutGroupDAO: WorkoutGroupDAO,
        exerciseWebClient: ExerciseWebClient,
        personRepository: PersonRepository
        ): ExerciseExportationRepository {
        return ExerciseExportationRepository(
            context = context,
            exerciseDAO = exerciseDAO,
            workoutGroupDAO = workoutGroupDAO,
            exerciseWebClient = exerciseWebClient,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideVideoExerciseExportationRepository(
        @ApplicationContext context: Context,
        videoExerciseDAO: VideoExerciseDAO,
        exerciseWebClient: ExerciseWebClient,
        personRepository: PersonRepository
        ): VideoExerciseExportationRepository {
        return VideoExerciseExportationRepository(
            context = context,
            videoExerciseDAO = videoExerciseDAO,
            exerciseWebClient = exerciseWebClient,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideVideoExportationRepository(
        @ApplicationContext context: Context,
        videoDAO: VideoDAO,
        exerciseWebClient: ExerciseWebClient,
        personRepository: PersonRepository
        ): VideoExportationRepository {
        return VideoExportationRepository(
            context = context,
            videoDAO = videoDAO,
            exerciseWebClient = exerciseWebClient
        )
    }

    @Provides
    fun provideExerciseExecutionRepository(
        @ApplicationContext context: Context,
        exerciseExecutionDAO: ExerciseExecutionDAO,
        videoRepository: VideoRepository,
    ): ExerciseExecutionRepository {
        return ExerciseExecutionRepository(
            context = context,
            exerciseExecutionDAO = exerciseExecutionDAO,
            videoRepository = videoRepository,
        )
    }

    @Provides
    fun provideExerciseExecutionImportationRepository(
        @ApplicationContext context: Context,
        exerciseExecutionDAO: ExerciseExecutionDAO,
        exerciseWebClient: ExerciseWebClient,
        personRepository: PersonRepository
    ): ExerciseExecutionImportationRepository {
        return ExerciseExecutionImportationRepository(
            context = context,
            exerciseExecutionDAO = exerciseExecutionDAO,
            webClient = exerciseWebClient,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideExercisePreDefinitionImportationRepository(
        @ApplicationContext context: Context,
         exercisePredefinitionDAO: ExercisePreDefinitionDAO,
         webClient: ExerciseWebClient,
         personRepository: PersonRepository
    ): ExercisePreDefinitionImportationRepository {
        return ExercisePreDefinitionImportationRepository(
            context = context,
            exercisePredefinitionDAO = exercisePredefinitionDAO,
            webClient = webClient,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideVideoExerciseExecutionImportationRepository(
        @ApplicationContext context: Context,
        videoExerciseExecutionDAO: VideoExerciseExecutionDAO,
        webClient: ExerciseWebClient,
        personRepository: PersonRepository
    ): VideoExerciseExecutionImportationRepository {
        return VideoExerciseExecutionImportationRepository(
            context = context,
            videoExerciseExecutionDAO = videoExerciseExecutionDAO,
            webClient = webClient,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideVideoExercisePreDefinitionImportationRepository(
        @ApplicationContext context: Context,
        videoExercisePreDefinitionDAO: VideoExercisePreDefinitionDAO,
        webClient: ExerciseWebClient,
        personRepository: PersonRepository
    ): VideoExercisePreDefinitionImportationRepository {
        return VideoExercisePreDefinitionImportationRepository(
            context = context,
            videoExercisePreDefinitionDAO = videoExercisePreDefinitionDAO,
            webClient = webClient,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideWorkoutGroupPreDefinitionImportationRepository(
        @ApplicationContext context: Context,
        workoutGroupPreDefinitionDAO: WorkoutGroupPreDefinitionDAO,
        webClient: WorkoutWebClient,
        personRepository: PersonRepository
    ): WorkoutGroupPreDefinitionImportationRepository {
        return WorkoutGroupPreDefinitionImportationRepository(
            context = context,
            workoutGroupPreDefinitionDAO = workoutGroupPreDefinitionDAO,
            webClient = webClient,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideExerciseExecutionExportationRepository(
        @ApplicationContext context: Context,
        exerciseExecutionDAO: ExerciseExecutionDAO,
        exerciseWebClient: ExerciseWebClient,
        personRepository: PersonRepository
    ): ExerciseExecutionExportationRepository {
        return ExerciseExecutionExportationRepository(
            context = context,
            exerciseExecutionDAO = exerciseExecutionDAO,
            exerciseWebClient = exerciseWebClient,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideVideoExerciseExecutionExportationRepository(
        @ApplicationContext context: Context,
        videoExerciseExecutionDAO: VideoExerciseExecutionDAO,
        exerciseWebClient: ExerciseWebClient,
        personRepository: PersonRepository
    ): VideoExerciseExecutionExportationRepository {
        return VideoExerciseExecutionExportationRepository(
            context = context,
            videoExerciseExecutionDAO = videoExerciseExecutionDAO,
            exerciseWebClient = exerciseWebClient,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideExercisePreDefinitionExportationRepository(
        @ApplicationContext context: Context,
        exercisePreDefinitionDAO: ExercisePreDefinitionDAO,
        exerciseWebClient: ExerciseWebClient,
        personRepository: PersonRepository
    ): ExercisePreDefinitionExportationRepository {
        return ExercisePreDefinitionExportationRepository(
            context = context,
            exercisePreDefinitionDAO = exercisePreDefinitionDAO,
            exerciseWebClient = exerciseWebClient,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideVideoExercisePreDefinitionExportationRepository(
        @ApplicationContext context: Context,
        videoExercisePreDefinitionDAO: VideoExercisePreDefinitionDAO,
        exerciseWebClient: ExerciseWebClient,
        personRepository: PersonRepository
    ): VideoExercisePreDefinitionExportationRepository {
        return VideoExercisePreDefinitionExportationRepository(
            context = context,
            videoExercisePreDefinitionDAO = videoExercisePreDefinitionDAO,
            exerciseWebClient = exerciseWebClient,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideWorkoutGroupPreDefinitionExportationRepository(
        @ApplicationContext context: Context,
        workoutGroupPreDefinitionDAO: WorkoutGroupPreDefinitionDAO,
        workoutWebClient: WorkoutWebClient,
        personRepository: PersonRepository
    ): WorkoutGroupPreDefinitionExportationRepository {
        return WorkoutGroupPreDefinitionExportationRepository(
            context = context,
            workoutGroupPreDefinitionDAO = workoutGroupPreDefinitionDAO,
            workoutWebClient = workoutWebClient,
            personRepository = personRepository
        )
    }
}