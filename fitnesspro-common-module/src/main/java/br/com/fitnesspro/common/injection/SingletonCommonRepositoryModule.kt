package br.com.fitnesspro.common.injection

import br.com.fitnesspro.common.repository.AcademyRepository
import br.com.fitnesspro.common.repository.UserRepository
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
        personDAO: PersonDAO,
        userDAO: UserDAO
    ): UserRepository {
        return UserRepository(
            personDAO = personDAO,
            userDAO = userDAO,
        )
    }

    @Provides
    fun provideAcademyRepository(
        academyDAO: AcademyDAO
    ): AcademyRepository {
        return AcademyRepository(academyDAO = academyDAO)
    }
}