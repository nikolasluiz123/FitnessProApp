package br.com.fitnesspro.injection

import android.content.Context
import br.com.fitnesspro.local.data.access.dao.AcademyDAO
import br.com.fitnesspro.local.data.access.dao.PersonDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.repository.AcademyRepository
import br.com.fitnesspro.repository.UserRepository
import br.com.fitnesspro.usecase.academy.SavePersonAcademyTimeUseCase
import br.com.fitnesspro.usecase.login.LoginUseCase
import br.com.fitnesspro.usecase.person.SavePersonUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SingletonModule {

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
    fun provideSavePersonUseCase(
        @ApplicationContext context: Context,
        userRepository: UserRepository,
    ): SavePersonUseCase {
        return SavePersonUseCase(
            context = context,
            userRepository = userRepository,
        )
    }

    @Provides
    fun provideAcademyRepository(
        academyDAO: AcademyDAO
    ): AcademyRepository {
        return AcademyRepository(academyDAO = academyDAO)
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

}