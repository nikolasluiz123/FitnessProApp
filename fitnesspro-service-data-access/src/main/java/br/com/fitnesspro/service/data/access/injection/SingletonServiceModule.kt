package br.com.fitnesspro.service.data.access.injection

import br.com.fitnesspro.service.data.access.service.general.IAuthenticationService
import br.com.fitnesspro.service.data.access.service.general.IPersonService
import br.com.fitnesspro.service.data.access.service.log.IExecutionLogService
import br.com.fitnesspro.service.data.access.service.scheduler.ISchedulerService
import br.com.fitnesspro.service.data.access.service.storage.IStorageService
import br.com.fitnesspro.service.data.access.service.sync.GeneralModuleSyncService
import br.com.fitnesspro.service.data.access.service.sync.SchedulerModuleSyncService
import br.com.fitnesspro.service.data.access.service.sync.WorkoutModuleSyncService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
class SingletonServiceModule {

    @Provides
    fun provideAuthenticationService(retrofit: Retrofit): IAuthenticationService {
        return retrofit.create(IAuthenticationService::class.java)
    }

    @Provides
    fun providePersonService(retrofit: Retrofit): IPersonService {
        return retrofit.create(IPersonService::class.java)
    }

    @Provides
    fun provideSchedulerService(retrofit: Retrofit): ISchedulerService {
        return retrofit.create(ISchedulerService::class.java)
    }

    @Provides
    fun provideExecutionLogService(retrofit: Retrofit): IExecutionLogService {
        return retrofit.create(IExecutionLogService::class.java)
    }

    @Provides
    fun provideStorageService(retrofit: Retrofit): IStorageService {
        return retrofit.create(IStorageService::class.java)
    }

    @Provides
    fun provideGeneralModuleSyncService(retrofit: Retrofit): GeneralModuleSyncService {
        return retrofit.create(GeneralModuleSyncService::class.java)
    }

    @Provides
    fun provideSchedulerModuleSyncService(retrofit: Retrofit): SchedulerModuleSyncService {
        return retrofit.create(SchedulerModuleSyncService::class.java)
    }

    @Provides
    fun provideWorkoutModuleSyncService(retrofit: Retrofit): WorkoutModuleSyncService {
        return retrofit.create(WorkoutModuleSyncService::class.java)
    }

}