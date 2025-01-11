package br.com.fitnesspro.common.injection

import android.content.Context
import br.com.fitnesspro.common.repository.AcademyRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.usecase.academy.SavePersonAcademyTimeUseCase
import br.com.fitnesspro.common.usecase.login.LoginUseCase
import br.com.fitnesspro.common.usecase.person.SavePersonMockUseCase
import br.com.fitnesspro.common.usecase.person.SavePersonUseCase
import br.com.fitnesspro.common.usecase.scheduler.SaveSchedulerConfigUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SingletonAuthenticationUseCaseModule {

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