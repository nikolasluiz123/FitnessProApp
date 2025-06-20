package br.com.fitnesspro.scheduler.injection

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.scheduler.SchedulerWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.SchedulerConfigRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.local.data.access.dao.SchedulerConfigDAO
import br.com.fitnesspro.local.data.access.dao.SchedulerDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupDAO
import br.com.fitnesspro.scheduler.repository.SchedulerReportRepository
import br.com.fitnesspro.scheduler.repository.SchedulerRepository
import br.com.fitnesspro.scheduler.repository.sync.exportation.SchedulerExportationRepository
import br.com.fitnesspro.scheduler.repository.sync.importation.SchedulerImportationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SingletonSchedulerRepositoryModule {

    @Provides
    fun provideSchedulerRepository(
        @ApplicationContext context: Context,
        schedulerDAO: SchedulerDAO,
        workoutDAO: WorkoutDAO,
        workoutGroupDAO: WorkoutGroupDAO,
        userRepository: UserRepository,
        personRepository: PersonRepository,
        schedulerWebClient: SchedulerWebClient,
    ): SchedulerRepository {
        return SchedulerRepository(
            schedulerDAO = schedulerDAO,
            workoutDAO = workoutDAO,
            userRepository = userRepository,
            personRepository = personRepository,
            schedulerWebClient = schedulerWebClient,
            workoutGroupDAO = workoutGroupDAO,
            context = context,
        )
    }

    @Provides
    fun provideSchedulerConfigRepository(
        @ApplicationContext context: Context,
        schedulerConfigDAO: SchedulerConfigDAO,
        schedulerWebClient: SchedulerWebClient,
    ): SchedulerConfigRepository {
        return SchedulerConfigRepository(
            context = context,
            schedulerConfigDAO = schedulerConfigDAO,
            schedulerWebClient = schedulerWebClient,
        )
    }

    @Provides
    fun provideSchedulerImportationRepository(
        @ApplicationContext context: Context,
        schedulerDAO: SchedulerDAO,
        schedulerWebClient: SchedulerWebClient,
    ): SchedulerImportationRepository {
        return SchedulerImportationRepository(
            schedulerDAO = schedulerDAO,
            webClient = schedulerWebClient,
            context = context,
        )
    }

    @Provides
    fun provideSchedulerExportationRepository(
        @ApplicationContext context: Context,
        schedulerDAO: SchedulerDAO,
        schedulerWebClient: SchedulerWebClient
    ): SchedulerExportationRepository {
        return SchedulerExportationRepository(
            schedulerWebClient = schedulerWebClient,
            schedulerDAO = schedulerDAO,
            context = context
        )
    }

    @Provides
    fun provideSchedulerReportRepository(
        @ApplicationContext context: Context,
        schedulerDAO: SchedulerDAO
    ): SchedulerReportRepository {
        return SchedulerReportRepository(
            context = context,
            schedulerDAO = schedulerDAO
        )
    }

}