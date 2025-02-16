package br.com.fitnesspro.scheduler.injection

import br.com.fitnesspor.service.data.access.webclient.scheduler.SchedulerWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.SchedulerConfigRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.local.data.access.dao.SchedulerDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutDAO
import br.com.fitnesspro.scheduler.repository.SchedulerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SingletonSchedulerRepositoryModule {

    @Provides
    fun provideSchedulerRepository(
        schedulerDAO: SchedulerDAO,
        workoutDAO: WorkoutDAO,
        userRepository: UserRepository,
        personRepository: PersonRepository,
        schedulerWebClient: SchedulerWebClient
    ): SchedulerRepository {
        return SchedulerRepository(
            schedulerDAO = schedulerDAO,
            workoutDAO = workoutDAO,
            userRepository = userRepository,
            personRepository = personRepository,
            schedulerWebClient = schedulerWebClient
        )
    }

    @Provides
    fun provideSchedulerConfigRepository(
        schedulerDAO: SchedulerDAO,
        userDAO: UserDAO,
        schedulerWebClient: SchedulerWebClient
    ): SchedulerConfigRepository {
        return SchedulerConfigRepository(
            schedulerDAO = schedulerDAO,
            userDAO = userDAO,
            schedulerWebClient = schedulerWebClient
        )
    }

}