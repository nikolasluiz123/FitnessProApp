package br.com.fitnesspor.service.data.access.injection

import android.content.Context
import br.com.fitnesspor.service.data.access.service.general.IAuthenticationService
import br.com.fitnesspor.service.data.access.service.general.IPersonService
import br.com.fitnesspor.service.data.access.service.log.IExecutionLogService
import br.com.fitnesspor.service.data.access.service.scheduler.ISchedulerService
import br.com.fitnesspor.service.data.access.service.storage.IStorageService
import br.com.fitnesspor.service.data.access.service.sync.GeneralModuleSyncService
import br.com.fitnesspor.service.data.access.service.sync.SchedulerModuleSyncService
import br.com.fitnesspor.service.data.access.service.sync.WorkoutModuleSyncService
import br.com.fitnesspor.service.data.access.webclient.general.AuthenticationWebClient
import br.com.fitnesspor.service.data.access.webclient.general.PersonWebClient
import br.com.fitnesspor.service.data.access.webclient.log.ExecutionLogWebClient
import br.com.fitnesspor.service.data.access.webclient.scheduler.SchedulerWebClient
import br.com.fitnesspor.service.data.access.webclient.storage.StorageWebClient
import br.com.fitnesspor.service.data.access.webclient.sync.GeneralModuleSyncWebClient
import br.com.fitnesspor.service.data.access.webclient.sync.SchedulerModuleSyncWebClient
import br.com.fitnesspor.service.data.access.webclient.sync.WorkoutModuleSyncWebClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SingletonWebClientModule {

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
    ): PersonWebClient {
        return PersonWebClient(
            context = context,
            personService = personService,
        )
    }

    @Provides
    fun provideSchedulerWebClient(
        @ApplicationContext context: Context,
        schedulerService: ISchedulerService,
    ): SchedulerWebClient {
        return SchedulerWebClient(
            context = context,
            schedulerService = schedulerService,
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

    @Provides
    fun provideStorageWebClient(
        @ApplicationContext context: Context,
        storageService: IStorageService
    ): StorageWebClient {
        return StorageWebClient(
            context = context,
            storageService = storageService
        )
    }

    @Provides
    fun provideGeneralModuleSyncWebClient(
        @ApplicationContext context: Context,
        service: GeneralModuleSyncService
    ): GeneralModuleSyncWebClient {
        return GeneralModuleSyncWebClient(
            context = context,
            service = service
        )
    }

    @Provides
    fun provideSchedulerModuleSyncWebClient(
        @ApplicationContext context: Context,
        service: SchedulerModuleSyncService
    ): SchedulerModuleSyncWebClient {
        return SchedulerModuleSyncWebClient(
            context = context,
            service = service
        )
    }

    @Provides
    fun provideWorkoutModuleSyncWebClient(
        @ApplicationContext context: Context,
        service: WorkoutModuleSyncService
    ): WorkoutModuleSyncWebClient {
        return WorkoutModuleSyncWebClient(
            context = context,
            service = service
        )
    }
}