package br.com.fitnesspro.common.injection

import br.com.fitnesspor.service.data.access.webclient.general.AcademyWebClient
import br.com.fitnesspor.service.data.access.webclient.general.AuthenticationWebClient
import br.com.fitnesspor.service.data.access.webclient.general.PersonWebClient
import br.com.fitnesspor.service.data.access.webclient.scheduler.SchedulerWebClient
import br.com.fitnesspro.common.repository.AcademyRepository
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.repository.sync.exportation.PersonExportationRepository
import br.com.fitnesspro.common.repository.sync.exportation.SchedulerConfigExportationRepository
import br.com.fitnesspro.common.repository.sync.importation.AcademyImportationRepository
import br.com.fitnesspro.common.repository.sync.importation.PersonImportationRepository
import br.com.fitnesspro.common.repository.sync.importation.SchedulerConfigImportationRepository
import br.com.fitnesspro.common.repository.sync.importation.UserImportationRepository
import br.com.fitnesspro.firebase.api.authentication.FirebaseDefaultAuthenticationService
import br.com.fitnesspro.firebase.api.authentication.FirebaseGoogleAuthenticationService
import br.com.fitnesspro.local.data.access.dao.AcademyDAO
import br.com.fitnesspro.local.data.access.dao.PersonAcademyTimeDAO
import br.com.fitnesspro.local.data.access.dao.PersonDAO
import br.com.fitnesspro.local.data.access.dao.SchedulerConfigDAO
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
        firebaseDefaultAuthenticationService: FirebaseDefaultAuthenticationService,
        firebaseGoogleAuthenticationService: FirebaseGoogleAuthenticationService,
        authenticationWebClient: AuthenticationWebClient
    ): UserRepository {
        return UserRepository(
            userDAO = userDAO,
            firebaseDefaultAuthenticationService = firebaseDefaultAuthenticationService,
            firebaseGoogleAuthenticationService = firebaseGoogleAuthenticationService,
            authenticationWebClient = authenticationWebClient
        )
    }

    @Provides
    fun providePersonRepository(
        personDAO: PersonDAO,
        userDAO: UserDAO,
        firebaseDefaultAuthenticationService: FirebaseDefaultAuthenticationService,
        personWebClient: PersonWebClient
    ): PersonRepository {
        return PersonRepository(
            personDAO = personDAO,
            userDAO = userDAO,
            firebaseDefaultAuthenticationService = firebaseDefaultAuthenticationService,
            personWebClient = personWebClient
        )
    }

    @Provides
    fun provideAcademyRepository(
        academyDAO: AcademyDAO,
        personAcademyTimeDAO: PersonAcademyTimeDAO,
        userDAO: UserDAO,
        personWebClient: PersonWebClient
    ): AcademyRepository {
        return AcademyRepository(
            academyDAO = academyDAO,
            userDAO = userDAO,
            personWebClient = personWebClient,
            personAcademyTimeDAO = personAcademyTimeDAO
        )
    }

    @Provides
    fun provideUserImportationRepository(
        personWebClient: PersonWebClient
    ): UserImportationRepository {
        return UserImportationRepository(
            webClient = personWebClient
        )
    }

    @Provides
    fun provideAcademyImportationRepository(
        academyDAO: AcademyDAO,
        academyWebClient: AcademyWebClient
    ): AcademyImportationRepository {
        return AcademyImportationRepository(
            academyDAO = academyDAO,
            academyWebClient = academyWebClient
        )
    }

    @Provides
    fun providePersonImportationRepository(
        personDAO: PersonDAO,
        personWebClient: PersonWebClient
    ): PersonImportationRepository {
        return PersonImportationRepository(
            personDAO = personDAO,
            webClient = personWebClient
        )
    }

    @Provides
    fun provideSchedulerConfigImportationRepository(
        schedulerConfigDAO: SchedulerConfigDAO,
        schedulerWebClient: SchedulerWebClient
    ): SchedulerConfigImportationRepository {
        return SchedulerConfigImportationRepository(
            schedulerConfigDAO = schedulerConfigDAO,
            webClient = schedulerWebClient
        )
    }

    @Provides
    fun providePersonExportationRepository(
        personWebClient: PersonWebClient
    ): PersonExportationRepository {
        return PersonExportationRepository(
            personWebClient = personWebClient
        )
    }

    @Provides
    fun provideSchedulerConfigExportationRepository(
        schedulerWebClient: SchedulerWebClient
    ): SchedulerConfigExportationRepository {
        return SchedulerConfigExportationRepository(
            schedulerWebClient = schedulerWebClient
        )
    }
}