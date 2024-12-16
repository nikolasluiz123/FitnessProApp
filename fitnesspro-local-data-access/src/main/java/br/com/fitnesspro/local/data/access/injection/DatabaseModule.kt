package br.com.fitnesspro.local.data.access.injection

import android.content.Context
import androidx.room.Room
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
        return Room.databaseBuilder(context, AppDatabase::class.java, "fitnesspro.db").build()
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
}