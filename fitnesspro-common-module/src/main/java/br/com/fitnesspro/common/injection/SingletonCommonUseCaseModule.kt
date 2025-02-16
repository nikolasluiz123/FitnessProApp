package br.com.fitnesspro.common.injection

import android.content.Context
import br.com.fitnesspro.common.repository.AcademyRepository
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.usecase.academy.SavePersonAcademyTimeBatchUseCase
import br.com.fitnesspro.common.usecase.academy.SavePersonAcademyTimeUseCase
import br.com.fitnesspro.common.usecase.login.DefaultLoginUseCase
import br.com.fitnesspro.common.usecase.login.GoogleLoginUseCase
import br.com.fitnesspro.common.usecase.person.SavePersonBatchUseCase
import br.com.fitnesspro.common.usecase.person.SavePersonUseCase
import br.com.fitnesspro.common.usecase.scheduler.SaveSchedulerConfigUseCase
import br.com.fitnesspro.core.security.DefaultPasswordHasher
import br.com.fitnesspro.core.security.IPasswordHasher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SingletonCommonUseCaseModule {

    @Provides
    fun provideSavePersonUseCase(
        @ApplicationContext context: Context,
        userRepository: UserRepository,
        personRepository: PersonRepository,
        saveSchedulerConfigUseCase: SaveSchedulerConfigUseCase
    ): SavePersonUseCase {
        return SavePersonUseCase(
            context = context,
            userRepository = userRepository,
            personRepository = personRepository,
            saveSchedulerConfigUseCase = saveSchedulerConfigUseCase,
            passwordHasher = DefaultPasswordHasher()
        )
    }

    @Provides
    fun providePasswordHasher(): IPasswordHasher {
        return DefaultPasswordHasher()
    }

    @Provides
    fun provideSavePersonAcademyTimeUseCase(
        @ApplicationContext context: Context,
        academyRepository: AcademyRepository,
    ): SavePersonAcademyTimeUseCase {
        return SavePersonAcademyTimeUseCase(
            context = context,
            academyRepository = academyRepository,
        )
    }

    @Provides
    fun provideSavePersonAcademyTimeBatchUseCase(
        @ApplicationContext context: Context,
        academyRepository: AcademyRepository,
    ): SavePersonAcademyTimeBatchUseCase {
        return SavePersonAcademyTimeBatchUseCase(
            context = context,
            academyRepository = academyRepository,
        )
    }

    @Provides
    fun provideLoginUseCase(
        @ApplicationContext context: Context,
        userRepository: UserRepository,
        passwordHasher: IPasswordHasher
    ): DefaultLoginUseCase {
        return DefaultLoginUseCase(
            context = context,
            userRepository = userRepository,
            passwordHasher = passwordHasher
        )
    }

    @Provides
    fun provideSavePersonMockUseCase(
        @ApplicationContext context: Context,
        userRepository: UserRepository,
        personRepository: PersonRepository,
        saveSchedulerConfigUseCase: SaveSchedulerConfigUseCase,
        passwordHasher: IPasswordHasher
    ): SavePersonBatchUseCase {
        return SavePersonBatchUseCase(
            context = context,
            userRepository = userRepository,
            personRepository = personRepository,
            saveSchedulerConfigUseCase = saveSchedulerConfigUseCase,
            passwordHasher = passwordHasher
        )
    }

    @Provides
    fun provideGoogleLoginUseCase(
        @ApplicationContext context: Context,
        userRepository: UserRepository,
    ): GoogleLoginUseCase {
        return GoogleLoginUseCase(
            context = context,
            userRepository = userRepository
        )
    }
}