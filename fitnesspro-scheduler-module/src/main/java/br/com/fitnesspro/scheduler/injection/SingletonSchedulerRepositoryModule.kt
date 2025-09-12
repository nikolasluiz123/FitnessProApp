package br.com.fitnesspro.scheduler.injection

import android.content.Context
import br.com.fitnesspro.service.data.access.webclient.scheduler.SchedulerWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.SchedulerConfigRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.local.data.access.dao.ReportDAO
import br.com.fitnesspro.local.data.access.dao.SchedulerConfigDAO
import br.com.fitnesspro.local.data.access.dao.SchedulerDAO
import br.com.fitnesspro.local.data.access.dao.SchedulerReportDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupDAO
import br.com.fitnesspro.scheduler.repository.SchedulerReportRepository
import br.com.fitnesspro.scheduler.repository.SchedulerRepository
import br.com.fitnesspro.scheduler.repository.sync.exportation.SchedulerModuleExportationRepository
import br.com.fitnesspro.scheduler.repository.sync.importation.SchedulerModuleImportationRepository
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
    ): SchedulerConfigRepository {
        return SchedulerConfigRepository(
            context = context,
            schedulerConfigDAO = schedulerConfigDAO,
        )
    }

    @Provides
    fun provideSchedulerReportRepository(
        @ApplicationContext context: Context,
        schedulerDAO: SchedulerDAO,
        schedulerReportDAO: SchedulerReportDAO,
        reportDAO: ReportDAO,
    ): SchedulerReportRepository {
        return SchedulerReportRepository(
            context = context,
            schedulerDAO = schedulerDAO,
            schedulerReportDAO = schedulerReportDAO,
            reportDAO = reportDAO
        )
    }

    @Provides
    fun provideSchedulerModuleImportationRepository(
        @ApplicationContext context: Context,
        personRepository: PersonRepository
    ): SchedulerModuleImportationRepository {
        return SchedulerModuleImportationRepository(context, personRepository)
    }

    @Provides
    fun provideSchedulerModuleExportationRepository(
        @ApplicationContext context: Context,
    ): SchedulerModuleExportationRepository {
        return SchedulerModuleExportationRepository(context)
    }
}