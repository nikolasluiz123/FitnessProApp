package br.com.fitnesspro.injection

import android.content.Context
import br.com.fitnesspro.local.data.access.dao.AcademyDAO
import br.com.fitnesspro.local.data.access.dao.PersonDAO
import br.com.fitnesspro.local.data.access.dao.SchedulerDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutDAO
import br.com.fitnesspro.repository.AcademyRepository
import br.com.fitnesspro.repository.SchedulerRepository
import br.com.fitnesspro.repository.UserRepository
import br.com.fitnesspro.usecase.person.SavePersonMockUseCase
import br.com.fitnesspro.usecase.scheduler.SaveSchedulerConfigUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SingletonRepositoryModule {

    @Provides
    fun provideUserRepository(
        personDAO: PersonDAO,
        userDAO: UserDAO,
        academyDAO: AcademyDAO
    ): UserRepository {
        return UserRepository(
            personDAO = personDAO,
            userDAO = userDAO,
            academyDAO = academyDAO
        )
    }

    @Provides
    fun provideAcademyRepository(
        academyDAO: AcademyDAO
    ): AcademyRepository {
        return AcademyRepository(academyDAO = academyDAO)
    }

    @Provides
    fun provideSchedulerRepository(
        schedulerDAO: SchedulerDAO,
        workoutDAO: WorkoutDAO,
        userRepository: UserRepository
    ): SchedulerRepository {
        return SchedulerRepository(
            schedulerDAO = schedulerDAO,
            workoutDAO = workoutDAO,
            userRepository = userRepository
        )
    }

    @Provides
    fun provideSavePersonMockUseCase(
        @ApplicationContext context: Context,
        userRepository: UserRepository,
        saveSchedulerConfigUseCase: SaveSchedulerConfigUseCase
    ): SavePersonMockUseCase {
        return SavePersonMockUseCase(
            context = context,
            userRepository = userRepository,
            saveSchedulerConfigUseCase = saveSchedulerConfigUseCase
        )
    }

}