package br.com.fitnesspro.health.connect.injection

import br.com.fitnesspro.health.connect.mapper.CaloriesBurnedMapper
import br.com.fitnesspro.health.connect.mapper.HeartRateMapper
import br.com.fitnesspro.health.connect.mapper.SleepSessionMapper
import br.com.fitnesspro.health.connect.mapper.StepsMapper
import br.com.fitnesspro.health.connect.service.CaloriesBurnedService
import br.com.fitnesspro.health.connect.service.HeartRateService
import br.com.fitnesspro.health.connect.service.SleepSessionService
import br.com.fitnesspro.health.connect.service.StepsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SingletonHealthConnectMapperModule {
    @Provides
    fun provideCaloriesBurnedMapper(service: CaloriesBurnedService): CaloriesBurnedMapper {
        return CaloriesBurnedMapper(service)
    }

    @Provides
    fun provideHeartRateMapper(service: HeartRateService): HeartRateMapper {
        return HeartRateMapper(service)
    }

    @Provides
    fun provideSleepSessionMapper(service: SleepSessionService): SleepSessionMapper {
        return SleepSessionMapper(service)
    }

    @Provides
    fun provideStepsMapper(service: StepsService): StepsMapper {
        return StepsMapper(service)
    }
}