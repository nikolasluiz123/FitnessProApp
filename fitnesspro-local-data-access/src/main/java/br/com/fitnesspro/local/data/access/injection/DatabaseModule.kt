package br.com.fitnesspro.local.data.access.injection

import android.content.Context
import androidx.room.Room
import br.com.fitnesspro.local.data.access.backup.DatabaseBackupExporter.Companion.FITNESS_PRO_DB_FILE
import br.com.fitnesspro.local.data.access.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Modulo de injeção de dependências do Room
 *
 * @author Nikolas Luiz Schmitt
 */
@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room
            .databaseBuilder(context, AppDatabase::class.java, FITNESS_PRO_DB_FILE)
            .fallbackToDestructiveMigration(true) // TODO - Remover quando estiver em 'Producao'
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase) = appDatabase.userDAO()

    @Provides
    @Singleton
    fun providePersonDao(appDatabase: AppDatabase) = appDatabase.personDAO()

    @Provides
    @Singleton
    fun provideAcademyDao(appDatabase: AppDatabase) = appDatabase.academyDAO()

    @Provides
    @Singleton
    fun providePersonAcademyTimeDao(appDatabase: AppDatabase) = appDatabase.personAcademyTimeDAO()

    @Provides
    @Singleton
    fun provideSchedulerDao(appDatabase: AppDatabase) = appDatabase.schedulerDAO()

    @Provides
    @Singleton
    fun provideSchedulerConfigDao(appDatabase: AppDatabase) = appDatabase.schedulerConfigDAO()

    @Provides
    @Singleton
    fun provideWorkoutDao(appDatabase: AppDatabase) = appDatabase.workoutDAO()

    @Provides
    @Singleton
    fun provideWorkoutGroupDao(appDatabase: AppDatabase) = appDatabase.workoutGroupDAO()

    @Provides
    @Singleton
    fun provideSyncHistoryDao(appDatabase: AppDatabase) = appDatabase.syncHistoryDAO()

    @Provides
    @Singleton
    fun provideDeviceDao(appDatabase: AppDatabase) = appDatabase.deviceDAO()

    @Provides
    @Singleton
    fun provideApplicationDao(appDatabase: AppDatabase) = appDatabase.applicationDAO()

    @Provides
    @Singleton
    fun provideServiceTokenDao(appDatabase: AppDatabase) = appDatabase.serviceTokenDAO()

    @Provides
    @Singleton
    fun provideExerciseDao(appDatabase: AppDatabase) = appDatabase.exerciseDAO()

    @Provides
    @Singleton
    fun provideExercisePreDefinitionDao(appDatabase: AppDatabase) = appDatabase.exercisePreDefinitionDAO()

    @Provides
    @Singleton
    fun provideWorkoutGroupPreDefinitionDao(appDatabase: AppDatabase) = appDatabase.workoutGroupPreDefinitionDAO()

    @Provides
    @Singleton
    fun provideVideoDao(appDatabase: AppDatabase) = appDatabase.videoDAO()

    @Provides
    @Singleton
    fun provideVideoExerciseDao(appDatabase: AppDatabase) = appDatabase.videoExerciseDAO()

    @Provides
    @Singleton
    fun provideExerciseExecutionDao(appDatabase: AppDatabase) = appDatabase.exerciseExecutionDAO()

    @Provides
    @Singleton
    fun provideVideoExerciseExecutionDao(appDatabase: AppDatabase) = appDatabase.videoExerciseExecutionDAO()

    @Provides
    @Singleton
    fun provideVideoExercisePreDefinitionDao(appDatabase: AppDatabase) = appDatabase.videoExercisePreDefinitionDAO()

    @Provides
    @Singleton
    fun provideReportDao(appDatabase: AppDatabase) = appDatabase.reportDAO()

    @Provides
    @Singleton
    fun provideSchedulerReportDao(appDatabase: AppDatabase) = appDatabase.schedulerReportDAO()

    @Provides
    @Singleton
    fun provideHealthConnectMetadataDAO(appDatabase: AppDatabase) = appDatabase.healthConnectMetadataDAO()

    @Provides
    @Singleton
    fun provideHealthConnectStepsDAO(appDatabase: AppDatabase) = appDatabase.healthConnectStepsDAO()

    @Provides
    @Singleton
    fun provideHealthConnectCaloriesBurnedDAO(appDatabase: AppDatabase) = appDatabase.healthConnectCaloriesBurnedDAO()

    @Provides
    @Singleton
    fun provideHealthConnectHeartRateDAO(appDatabase: AppDatabase) = appDatabase.healthConnectHeartRateDAO()

    @Provides
    @Singleton
    fun provideHealthConnectHeartRateSamplesDAO(appDatabase: AppDatabase) = appDatabase.healthConnectHeartRateSamplesDAO()

    @Provides
    @Singleton
    fun provideHealthConnectSleepSessionDAO(appDatabase: AppDatabase) = appDatabase.healthConnectSleepSessionDAO()

    @Provides
    @Singleton
    fun provideHealthConnectSleepStagesDAO(appDatabase: AppDatabase) = appDatabase.healthConnectSleepStagesDAO()

    @Provides
    @Singleton
    fun provideSleepSessionExerciseExecutionDAO(appDatabase: AppDatabase) = appDatabase.sleepSessionExerciseExecutionDAO()

    @Provides
    @Singleton
    fun provideWorkoutReportDAO(appDatabase: AppDatabase) = appDatabase.workoutReportDAO()

}