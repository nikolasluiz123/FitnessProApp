package br.com.fitnesspro.injection

import android.content.Context
import androidx.room.Room
import br.com.fitnesspro.local.data.access.database.AppDatabase
import br.com.fitnesspro.local.data.access.injection.DatabaseModule
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
class DatabaseModuleTests {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
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
}