package br.com.fitnesspro.common.injection

import br.com.fitnesspro.common.repository.AcademyRepository
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.firebase.api.authentication.DefaultAuthenticationService
import br.com.fitnesspro.firebase.api.authentication.GoogleAuthenticationService
import br.com.fitnesspro.local.data.access.dao.AcademyDAO
import br.com.fitnesspro.local.data.access.dao.PersonDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SingletonCommonRepositoryModule {

    @Provides
    fun provideUserRepository(
        userDAO: UserDAO,
        defaultAuthenticationService: DefaultAuthenticationService,
        googleAuthenticationService: GoogleAuthenticationService
    ): UserRepository {
        return UserRepository(
            userDAO = userDAO,
            defaultAuthenticationService = defaultAuthenticationService,
            googleAuthenticationService = googleAuthenticationService
        )
    }

    @Provides
    fun providePersonRepository(
        personDAO: PersonDAO,
        userDAO: UserDAO,
        defaultAuthenticationService: DefaultAuthenticationService
    ): PersonRepository {
        return PersonRepository(
            personDAO = personDAO,
            userDAO = userDAO,
            defaultAuthenticationService = defaultAuthenticationService
        )
    }

    @Provides
    fun provideAcademyRepository(
        academyDAO: AcademyDAO
    ): AcademyRepository {
        return AcademyRepository(academyDAO = academyDAO)
    }
}