package br.com.fitnesspro.workout.injection

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.workout.ExerciseWebClient
import br.com.fitnesspor.service.data.access.webclient.workout.WorkoutWebClient
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
import br.com.fitnesspro.workout.repository.sync.exportation.ExerciseExportationRepository
import br.com.fitnesspro.workout.repository.sync.exportation.VideoExerciseExportationRepository
import br.com.fitnesspro.workout.repository.sync.exportation.VideoExportationRepository
import br.com.fitnesspro.workout.repository.sync.exportation.WorkoutExportationRepository
import br.com.fitnesspro.workout.repository.sync.exportation.WorkoutGroupExportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.ExerciseImportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.VideoExerciseImportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.VideoImportationRepository
import br.com.fitnesspro.workout.repository.sync.importation.WorkoutGroupImportationRepository
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
        workoutWebClient: WorkoutWebClient
    ): WorkoutRepository {
        return WorkoutRepository(
            context = context,
            workoutDAO = workoutDAO,
            workoutGroupDAO = workoutGroupDAO,
            personRepository = personRepository,
            exerciseRepository = exerciseRepository,
            workoutWebClient = workoutWebClient
        )
    }

    @Provides
    fun provideWorkoutGroupRepository(
        @ApplicationContext context: Context,
        workoutGroupDAO: WorkoutGroupDAO,
        exerciseDAO: ExerciseDAO,
        workoutWebClient: WorkoutWebClient
    ): WorkoutGroupRepository {
        return WorkoutGroupRepository(
            context = context,
            workoutGroupDAO = workoutGroupDAO,
            exerciseDAO = exerciseDAO,
            workoutWebClient = workoutWebClient
        )
    }

    @Provides
    fun provideExerciseRepository(
        @ApplicationContext context: Context,
        exerciseDAO: ExerciseDAO,
        workoutGroupDAO: WorkoutGroupDAO,
        workoutGroupRepository: WorkoutGroupRepository,
        exerciseWebClient: ExerciseWebClient,
        videoRepository: VideoRepository
    ): ExerciseRepository {
        return ExerciseRepository(
            context = context,
            exerciseDAO = exerciseDAO,
            workoutGroupDAO = workoutGroupDAO,
            workoutGroupRepository = workoutGroupRepository,
            exerciseWebClient = exerciseWebClient,
            videoRepository = videoRepository
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
        videoExerciseDAO: VideoExerciseDAO,
        exerciseWebClient: ExerciseWebClient
    ): VideoRepository {
        return VideoRepository(
            context = context,
            videoDAO = videoDAO,
            videoExerciseDAO = videoExerciseDAO,
            exerciseWebClient = exerciseWebClient
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
        exerciseWebClient: ExerciseWebClient,
        personRepository: PersonRepository
        ): ExerciseExportationRepository {
        return ExerciseExportationRepository(
            context = context,
            exerciseDAO = exerciseDAO,
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
            exerciseWebClient = exerciseWebClient,
            personRepository = personRepository
        )
    }

}