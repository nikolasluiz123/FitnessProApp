package br.com.fitnesspro.scheduler.injection

import android.content.Context
import br.com.fitnesspro.common.repository.AcademyRepository
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.SchedulerConfigRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.usecase.scheduler.SaveSchedulerConfigUseCase
import br.com.fitnesspro.firebase.api.firestore.repository.FirestoreChatRepository
import br.com.fitnesspro.scheduler.repository.SchedulerReportRepository
import br.com.fitnesspro.scheduler.repository.SchedulerRepository
import br.com.fitnesspro.scheduler.usecase.report.GenerateSchedulerReportUseCase
import br.com.fitnesspro.scheduler.usecase.scheduler.CancelSchedulerUseCase
import br.com.fitnesspro.scheduler.usecase.scheduler.ConfirmationSchedulerUseCase
import br.com.fitnesspro.scheduler.usecase.scheduler.SaveCompromiseSuggestionUseCase
import br.com.fitnesspro.scheduler.usecase.scheduler.SaveCompromiseUseCase
import br.com.fitnesspro.scheduler.usecase.scheduler.SaveRecurrentCompromiseUseCase
import br.com.fitnesspro.scheduler.usecase.scheduler.SaveUniqueCompromiseUseCase
import br.com.fitnesspro.scheduler.usecase.scheduler.SendChatMessageUseCase
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
    ): SaveSchedulerConfigUseCase {
        return SaveSchedulerConfigUseCase(
            context = context,
            schedulerConfigRepository = schedulerConfigRepository
        )
    }

    @Provides
    fun provideSaveUniqueCompromiseUseCase(
        @ApplicationContext context: Context,
        schedulerRepository: SchedulerRepository,
        userRepository: UserRepository,
        personRepository: PersonRepository
    ): SaveUniqueCompromiseUseCase {
        return SaveUniqueCompromiseUseCase(
            context = context,
            schedulerRepository = schedulerRepository,
            userRepository = userRepository,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideSaveRecurrentCompromiseUseCase(
        @ApplicationContext context: Context,
        schedulerRepository: SchedulerRepository,
        userRepository: UserRepository,
        personRepository: PersonRepository
    ): SaveRecurrentCompromiseUseCase {
        return SaveRecurrentCompromiseUseCase(
            context = context,
            schedulerRepository = schedulerRepository,
            userRepository = userRepository,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideSaveCompromiseSuggestionUseCase(
        @ApplicationContext context: Context,
        schedulerRepository: SchedulerRepository,
        userRepository: UserRepository,
        academyRepository: AcademyRepository,
        personRepository: PersonRepository
    ): SaveCompromiseSuggestionUseCase {
        return SaveCompromiseSuggestionUseCase(
            context = context,
            schedulerRepository = schedulerRepository,
            userRepository = userRepository,
            academyRepository = academyRepository,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideSaveCompromiseUseCase(
        @ApplicationContext context: Context,
        uniqueCompromiseUseCase: SaveUniqueCompromiseUseCase,
        recurrentCompromiseUseCase: SaveRecurrentCompromiseUseCase,
        suggestionCompromiseUseCase: SaveCompromiseSuggestionUseCase,
    ): SaveCompromiseUseCase {
        return SaveCompromiseUseCase(
            context = context,
            uniqueCompromiseUseCase = uniqueCompromiseUseCase,
            recurrentCompromiseUseCase = recurrentCompromiseUseCase,
            suggestionCompromiseUseCase = suggestionCompromiseUseCase,
        )
    }

    @Provides
    fun provideInactivateSchedulerUseCase(
        @ApplicationContext context: Context,
        schedulerRepository: SchedulerRepository,
        personRepository: PersonRepository
    ): CancelSchedulerUseCase {
        return CancelSchedulerUseCase(
            context = context,
            schedulerRepository = schedulerRepository,
            personRepository = personRepository
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

    @Provides
    fun provideSendChatMessageUseCase(
        firestoreChatRepository: FirestoreChatRepository,
        personRepository: PersonRepository
    ): SendChatMessageUseCase {
        return SendChatMessageUseCase(
            firestoreChatRepository = firestoreChatRepository,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideGenerateSchedulerReportUseCase(
        @ApplicationContext context: Context,
        schedulerReportRepository: SchedulerReportRepository,
        personRepository: PersonRepository
    ): GenerateSchedulerReportUseCase {
        return GenerateSchedulerReportUseCase(
            context = context,
            schedulerReportRepository = schedulerReportRepository,
            personRepository = personRepository
        )
    }

}