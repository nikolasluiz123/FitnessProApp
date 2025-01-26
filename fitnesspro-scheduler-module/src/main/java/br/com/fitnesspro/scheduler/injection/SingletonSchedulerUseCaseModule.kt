package br.com.fitnesspro.scheduler.injection

import android.content.Context
import br.com.fitnesspro.common.repository.AcademyRepository
import br.com.fitnesspro.common.repository.SchedulerConfigRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.usecase.scheduler.SaveSchedulerConfigUseCase
import br.com.fitnesspro.scheduler.repository.SchedulerRepository
import br.com.fitnesspro.scheduler.usecase.scheduler.ConfirmationSchedulerUseCase
import br.com.fitnesspro.scheduler.usecase.scheduler.InactivateSchedulerUseCase
import br.com.fitnesspro.scheduler.usecase.scheduler.SaveCompromiseSuggestionUseCase
import br.com.fitnesspro.scheduler.usecase.scheduler.SaveCompromiseUseCase
import br.com.fitnesspro.scheduler.usecase.scheduler.SaveRecurrentCompromiseUseCase
import br.com.fitnesspro.scheduler.usecase.scheduler.SaveUniqueCompromiseUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SingletonSchedulerUseCaseModule {

    @Provides
    fun provideSaveSchedulerUseCase(
        @ApplicationContext context: Context,
        schedulerConfigRepository: SchedulerConfigRepository,
        userRepository: UserRepository
    ): SaveSchedulerConfigUseCase {
        return SaveSchedulerConfigUseCase(
            context = context,
            schedulerConfigRepository = schedulerConfigRepository,
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
        userRepository: UserRepository,
        academyRepository: AcademyRepository
    ): SaveCompromiseSuggestionUseCase {
        return SaveCompromiseSuggestionUseCase(
            context = context,
            schedulerRepository = schedulerRepository,
            userRepository = userRepository,
            academyRepository = academyRepository
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

    @Provides
    fun provideInactivateSchedulerUseCase(
        @ApplicationContext context: Context,
        schedulerRepository: SchedulerRepository,
    ): InactivateSchedulerUseCase {
        return InactivateSchedulerUseCase(
            context = context,
            schedulerRepository = schedulerRepository,
        )
    }

    @Provides
    fun provideConfirmationSchedulerUseCase(
        @ApplicationContext context: Context,
        schedulerRepository: SchedulerRepository,
    ): ConfirmationSchedulerUseCase {
        return ConfirmationSchedulerUseCase(
            context = context,
            schedulerRepository = schedulerRepository,
        )
    }
}