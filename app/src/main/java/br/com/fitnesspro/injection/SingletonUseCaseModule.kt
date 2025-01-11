package br.com.fitnesspro.injection

import android.content.Context
import br.com.fitnesspro.repository.AcademyRepository
import br.com.fitnesspro.repository.SchedulerRepository
import br.com.fitnesspro.repository.UserRepository
import br.com.fitnesspro.usecase.academy.SavePersonAcademyTimeUseCase
import br.com.fitnesspro.usecase.login.LoginUseCase
import br.com.fitnesspro.usecase.person.SavePersonUseCase
import br.com.fitnesspro.usecase.scheduler.SaveCompromiseSuggestionUseCase
import br.com.fitnesspro.usecase.scheduler.SaveCompromiseUseCase
import br.com.fitnesspro.usecase.scheduler.SaveRecurrentCompromiseUseCase
import br.com.fitnesspro.usecase.scheduler.SaveSchedulerConfigUseCase
import br.com.fitnesspro.usecase.scheduler.SaveUniqueCompromiseUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SingletonUseCaseModule {

    @Provides
    fun provideSavePersonUseCase(
        @ApplicationContext context: Context,
        userRepository: UserRepository,
        saveSchedulerConfigUseCase: SaveSchedulerConfigUseCase
    ): SavePersonUseCase {
        return SavePersonUseCase(
            context = context,
            userRepository = userRepository,
            saveSchedulerConfigUseCase = saveSchedulerConfigUseCase
        )
    }

    @Provides
    fun provideSavePersonAcademyTimeUseCase(
        @ApplicationContext context: Context,
        academyRepository: AcademyRepository,
        userRepository: UserRepository,
    ): SavePersonAcademyTimeUseCase {
        return SavePersonAcademyTimeUseCase(
            context = context,
            academyRepository = academyRepository,
            userRepository = userRepository,
        )
    }

    @Provides
    fun provideLoginUseCase(
        @ApplicationContext context: Context,
        userRepository: UserRepository,
    ): LoginUseCase {
        return LoginUseCase(
            context = context,
            userRepository = userRepository,
        )
    }

    @Provides
    fun provideSaveSchedulerUseCase(
        @ApplicationContext context: Context,
        schedulerRepository: SchedulerRepository,
        userRepository: UserRepository
    ): SaveSchedulerConfigUseCase {
        return SaveSchedulerConfigUseCase(
            context = context,
            schedulerRepository = schedulerRepository,
            userRepository = userRepository
        )
    }

    @Provides
    fun provideSaveUniqueCompromiseUseCase(
        @ApplicationContext context: Context,
        schedulerRepository: SchedulerRepository,
        userRepository: UserRepository
    ): SaveUniqueCompromiseUseCase {
        return SaveUniqueCompromiseUseCase(
            context = context,
            schedulerRepository = schedulerRepository,
            userRepository = userRepository
        )
    }

    @Provides
    fun provideSaveRecurrentCompromiseUseCase(
        @ApplicationContext context: Context,
        schedulerRepository: SchedulerRepository,
        userRepository: UserRepository
    ): SaveRecurrentCompromiseUseCase {
        return SaveRecurrentCompromiseUseCase(
            context = context,
            schedulerRepository = schedulerRepository,
            userRepository = userRepository
        )
    }

    @Provides
    fun provideSaveCompromiseSuggestionUseCase(
        @ApplicationContext context: Context,
        schedulerRepository: SchedulerRepository,
        userRepository: UserRepository
    ): SaveCompromiseSuggestionUseCase {
        return SaveCompromiseSuggestionUseCase(
            context = context,
            schedulerRepository = schedulerRepository,
            userRepository = userRepository
        )
    }

    @Provides
    fun provideSaveCompromiseUseCase(
        uniqueCompromiseUseCase: SaveUniqueCompromiseUseCase,
        recurrentCompromiseUseCase: SaveRecurrentCompromiseUseCase,
        suggestionCompromiseUseCase: SaveCompromiseSuggestionUseCase,
    ): SaveCompromiseUseCase {
        return SaveCompromiseUseCase(
            uniqueCompromiseUseCase = uniqueCompromiseUseCase,
            recurrentCompromiseUseCase = recurrentCompromiseUseCase,
            suggestionCompromiseUseCase = suggestionCompromiseUseCase,
        )
    }
}