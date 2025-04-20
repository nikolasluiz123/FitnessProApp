package br.com.fitnesspor.service.data.access.injection

import android.content.Context
import br.com.fitnesspor.service.data.access.service.general.IAcademyService
import br.com.fitnesspor.service.data.access.service.general.IAuthenticationService
import br.com.fitnesspor.service.data.access.service.general.IPersonService
import br.com.fitnesspor.service.data.access.service.log.IExecutionLogService
import br.com.fitnesspor.service.data.access.service.scheduler.ISchedulerService
import br.com.fitnesspor.service.data.access.webclient.ExecutionLogWebClient
import br.com.fitnesspor.service.data.access.webclient.general.AcademyWebClient
import br.com.fitnesspor.service.data.access.webclient.general.AuthenticationWebClient
import br.com.fitnesspor.service.data.access.webclient.general.PersonWebClient
import br.com.fitnesspor.service.data.access.webclient.scheduler.SchedulerWebClient
import br.com.fitnesspro.mappers.AcademyModelMapper
import br.com.fitnesspro.mappers.PersonModelMapper
import br.com.fitnesspro.mappers.SchedulerModelMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SingletonWebClientModule {

    @Provides
    fun provideAcademyWebClient(
        @ApplicationContext context: Context,
        academyService: IAcademyService
    ): AcademyWebClient {
        return AcademyWebClient(
            context = context,
            academyService = academyService
        )
    }

    @Provides
    fun provideAuthenticationWebClient(
        @ApplicationContext context: Context,
        authenticationService: IAuthenticationService
    ): AuthenticationWebClient {
        return AuthenticationWebClient(
            context = context,
            authenticationService = authenticationService
        )
    }

    @Provides
    fun providePersonWebClient(
        @ApplicationContext context: Context,
        personService: IPersonService,
        academyModelMapper: AcademyModelMapper,
        personModelMapper: PersonModelMapper
    ): PersonWebClient {
        return PersonWebClient(
            context = context,
            personService = personService,
            academyModelMapper = academyModelMapper,
            personModelMapper = personModelMapper
        )
    }

    @Provides
    fun provideSchedulerWebClient(
        @ApplicationContext context: Context,
        schedulerService: ISchedulerService,
        schedulerModelMapper: SchedulerModelMapper
    ): SchedulerWebClient {
        return SchedulerWebClient(
            context = context,
            schedulerService = schedulerService,
            schedulerModelMapper = schedulerModelMapper
        )
    }

    @Provides
    fun provideExecutionLogWebClient(
        @ApplicationContext context: Context,
        executionLogService: IExecutionLogService
    ): ExecutionLogWebClient {
        return ExecutionLogWebClient(
            context = context,
            executionLogService = executionLogService
        )
    }
}