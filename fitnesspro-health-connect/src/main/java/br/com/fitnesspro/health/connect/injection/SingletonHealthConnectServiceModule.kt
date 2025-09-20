package br.com.fitnesspro.health.connect.injection

import androidx.health.connect.client.HealthConnectClient
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
class SingletonHealthConnectServiceModule {

    @Provides
    fun provideCaloriesBurnedService(
        healthConnectClient: HealthConnectClient
    ): CaloriesBurnedService {
        return CaloriesBurnedService(healthConnectClient)
    }

    @Provides
    fun provideHeartRateService(
        healthConnectClient: HealthConnectClient
    ): HeartRateService {
        return HeartRateService(healthConnectClient)
    }

    @Provides
    fun provideSleepSessionService(
        healthConnectClient: HealthConnectClient
    ): SleepSessionService {
        return SleepSessionService(healthConnectClient)
    }

    @Provides
    fun provideStepsService(
        healthConnectClient: HealthConnectClient
    ): StepsService {
        return StepsService(healthConnectClient)
    }
}