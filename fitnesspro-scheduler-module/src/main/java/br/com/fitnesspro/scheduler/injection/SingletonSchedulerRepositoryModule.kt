package br.com.fitnesspro.scheduler.injection

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.general.ReportWebClient
import br.com.fitnesspor.service.data.access.webclient.scheduler.SchedulerWebClient
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
import br.com.fitnesspro.scheduler.repository.sync.exportation.ReportFromSchedulerExportationRepository
import br.com.fitnesspro.scheduler.repository.sync.exportation.SchedulerExportationRepository
import br.com.fitnesspro.scheduler.repository.sync.exportation.SchedulerReportExportationRepository
import br.com.fitnesspro.scheduler.repository.sync.importation.ReportFromSchedulerImportationRepository
import br.com.fitnesspro.scheduler.repository.sync.importation.SchedulerImportationRepository
import br.com.fitnesspro.scheduler.repository.sync.importation.SchedulerReportImportationRepository
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
        schedulerDAO: SchedulerDAO,
        schedulerReportDAO: SchedulerReportDAO,
        reportDAO: ReportDAO,
        reportWebClient: ReportWebClient
    ): SchedulerReportRepository {
        return SchedulerReportRepository(
            context = context,
            schedulerDAO = schedulerDAO,
            schedulerReportDAO = schedulerReportDAO,
            reportDAO = reportDAO
        )
    }

    @Provides
    fun provideSchedulerReportImportationRepository(
        @ApplicationContext context: Context,
        schedulerReportDAO: SchedulerReportDAO,
        reportWebClient: ReportWebClient,
        personRepository: PersonRepository
        ): SchedulerReportImportationRepository {
        return SchedulerReportImportationRepository(
            context = context,
            schedulerReportDAO = schedulerReportDAO,
            webClient = reportWebClient,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideReportFromSchedulerImportationRepository(
        @ApplicationContext context: Context,
        reportDAO: ReportDAO,
        reportWebClient: ReportWebClient,
        personRepository: PersonRepository
    ): ReportFromSchedulerImportationRepository {
        return ReportFromSchedulerImportationRepository(
            context = context,
            reportDAO = reportDAO,
            webClient = reportWebClient,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideSchedulerReportExportationRepository(
        @ApplicationContext context: Context,
        schedulerReportDAO: SchedulerReportDAO,
        reportWebClient: ReportWebClient
    ): SchedulerReportExportationRepository {
        return SchedulerReportExportationRepository(
            context = context,
            schedulerReportDAO = schedulerReportDAO,
            reportWebClient = reportWebClient
        )
    }

    @Provides
    fun provideReportFromSchedulerExportationRepository(
        @ApplicationContext context: Context,
        reportDAO: ReportDAO,
        reportWebClient: ReportWebClient
    ): ReportFromSchedulerExportationRepository {
        return ReportFromSchedulerExportationRepository(
            context = context,
            reportDAO = reportDAO,
            reportWebClient = reportWebClient
        )
    }

}