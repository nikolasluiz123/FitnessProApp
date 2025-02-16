package br.com.fitnesspor.service.data.access.injection

import br.com.fitnesspor.service.data.access.service.general.IAcademyService
import br.com.fitnesspor.service.data.access.service.general.IAuthenticationService
import br.com.fitnesspor.service.data.access.service.general.IPersonService
import br.com.fitnesspor.service.data.access.service.scheduler.ISchedulerService
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
    fun provideAcademyService(retrofit: Retrofit): IAcademyService {
        return retrofit.create(IAcademyService::class.java)
    }

    @Provides
    fun provideSchedulerService(retrofit: Retrofit): ISchedulerService {
        return retrofit.create(ISchedulerService::class.java)
    }

}