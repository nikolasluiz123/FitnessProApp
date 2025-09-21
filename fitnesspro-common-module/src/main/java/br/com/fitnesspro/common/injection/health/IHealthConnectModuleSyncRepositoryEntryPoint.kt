package br.com.fitnesspro.common.injection.health

import br.com.fitnesspro.local.data.access.dao.health.HealthConnectCaloriesBurnedDAO
import br.com.fitnesspro.local.data.access.dao.health.HealthConnectHeartRateDAO
import br.com.fitnesspro.local.data.access.dao.health.HealthConnectHeartRateSamplesDAO
import br.com.fitnesspro.local.data.access.dao.health.HealthConnectMetadataDAO
import br.com.fitnesspro.local.data.access.dao.health.HealthConnectSleepSessionDAO
import br.com.fitnesspro.local.data.access.dao.health.HealthConnectSleepStagesDAO
import br.com.fitnesspro.local.data.access.dao.health.HealthConnectStepsDAO
import br.com.fitnesspro.local.data.access.dao.health.SleepSessionExerciseExecutionDAO
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface IHealthConnectModuleSyncRepositoryEntryPoint {

    fun getHealthConnectMetadataDAO(): HealthConnectMetadataDAO
    fun getHealthConnectStepsDAO(): HealthConnectStepsDAO
    fun getHealthConnectCaloriesBurnedDAO(): HealthConnectCaloriesBurnedDAO
    fun getHealthConnectHeartRateDAO(): HealthConnectHeartRateDAO
    fun getHealthConnectHeartRateSamplesDAO(): HealthConnectHeartRateSamplesDAO
    fun getHealthConnectSleepSessionDAO(): HealthConnectSleepSessionDAO
    fun getHealthConnectSleepStagesDAO(): HealthConnectSleepStagesDAO
    fun getSleepSessionExerciseExecutionDAO(): SleepSessionExerciseExecutionDAO
}