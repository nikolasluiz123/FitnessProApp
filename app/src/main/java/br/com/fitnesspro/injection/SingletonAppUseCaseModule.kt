package br.com.fitnesspro.injection

import android.content.Context
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.repository.sync.importation.GeneralModuleImportationRepository
import br.com.fitnesspro.common.repository.sync.importation.storage.ReportStorageImportationRepository
import br.com.fitnesspro.common.repository.sync.importation.storage.VideoStorageImportationRepository
import br.com.fitnesspro.scheduler.repository.sync.importation.SchedulerModuleImportationRepository
import br.com.fitnesspro.usecase.FullManualImportationUseCase
import br.com.fitnesspro.workout.repository.sync.importation.WorkoutModuleImportationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SingletonAppUseCaseModule {

    @Provides
    fun provideFullManualImportationUseCase(
        @ApplicationContext context: Context,
        userRepository: UserRepository,
        generalRepository: GeneralModuleImportationRepository,
        schedulerRepository: SchedulerModuleImportationRepository,
        workoutRepository: WorkoutModuleImportationRepository,
        reportRepository: ReportStorageImportationRepository,
        videoRepository: VideoStorageImportationRepository
    ): FullManualImportationUseCase {
        return FullManualImportationUseCase(
            context = context,
            userRepository = userRepository,
            generalRepository = generalRepository,
            schedulerRepository = schedulerRepository,
            workoutRepository = workoutRepository,
            reportRepository = reportRepository,
            videoRepository = videoRepository
        )
    }
}