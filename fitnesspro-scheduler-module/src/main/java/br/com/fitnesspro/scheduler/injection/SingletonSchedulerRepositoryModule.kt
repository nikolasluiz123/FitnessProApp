package br.com.fitnesspro.scheduler.injection

import br.com.fitnesspor.service.data.access.webclient.scheduler.SchedulerWebClient
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.SchedulerConfigRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.local.data.access.dao.SchedulerConfigDAO
import br.com.fitnesspro.local.data.access.dao.SchedulerDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupDAO
import br.com.fitnesspro.scheduler.repository.SchedulerRepository
import br.com.fitnesspro.scheduler.repository.importation.SchedulerImportationRepository
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
        workoutGroupDAO: WorkoutGroupDAO,
        userRepository: UserRepository,
        personRepository: PersonRepository,
        schedulerWebClient: SchedulerWebClient
    ): SchedulerRepository {
        return SchedulerRepository(
            schedulerDAO = schedulerDAO,
            workoutDAO = workoutDAO,
            userRepository = userRepository,
            personRepository = personRepository,
            schedulerWebClient = schedulerWebClient,
            workoutGroupDAO = workoutGroupDAO
        )
    }

    @Provides
    fun provideSchedulerConfigRepository(
        schedulerConfigDAO: SchedulerConfigDAO,
        schedulerWebClient: SchedulerWebClient,
        userDAO: UserDAO
    ): SchedulerConfigRepository {
        return SchedulerConfigRepository(
            schedulerConfigDAO = schedulerConfigDAO,
            schedulerWebClient = schedulerWebClient,
            userDAO = userDAO
        )
    }

    @Provides
    fun provideSchedulerImportationRepository(
        schedulerDAO: SchedulerDAO,
        schedulerWebClient: SchedulerWebClient
    ): SchedulerImportationRepository {
        return SchedulerImportationRepository(
            schedulerDAO = schedulerDAO,
            webClient = schedulerWebClient
        )
    }

}