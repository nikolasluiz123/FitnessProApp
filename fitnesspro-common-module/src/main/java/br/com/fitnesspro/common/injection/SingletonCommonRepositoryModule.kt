package br.com.fitnesspro.common.injection

import android.content.Context
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
import dagger.hilt.android.qualifiers.ApplicationContext
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
        @ApplicationContext context: Context,
        personWebClient: PersonWebClient
    ): UserImportationRepository {
        return UserImportationRepository(
            webClient = personWebClient,
            context = context
        )
    }

    @Provides
    fun provideAcademyImportationRepository(
        @ApplicationContext context: Context,
        academyDAO: AcademyDAO,
        academyWebClient: AcademyWebClient
    ): AcademyImportationRepository {
        return AcademyImportationRepository(
            academyDAO = academyDAO,
            academyWebClient = academyWebClient,
            context = context
        )
    }

    @Provides
    fun providePersonImportationRepository(
        @ApplicationContext context: Context,
        personDAO: PersonDAO,
        personWebClient: PersonWebClient
    ): PersonImportationRepository {
        return PersonImportationRepository(
            personDAO = personDAO,
            webClient = personWebClient,
            context = context
        )
    }

    @Provides
    fun provideSchedulerConfigImportationRepository(
        @ApplicationContext context: Context,
        schedulerConfigDAO: SchedulerConfigDAO,
        schedulerWebClient: SchedulerWebClient
    ): SchedulerConfigImportationRepository {
        return SchedulerConfigImportationRepository(
            schedulerConfigDAO = schedulerConfigDAO,
            webClient = schedulerWebClient,
            context = context
        )
    }

    @Provides
    fun providePersonExportationRepository(
        @ApplicationContext context: Context,
        personDAO: PersonDAO,
        personWebClient: PersonWebClient
    ): PersonExportationRepository {
        return PersonExportationRepository(
            personWebClient = personWebClient,
            personDAO = personDAO,
            context = context
        )
    }

    @Provides
    fun provideSchedulerConfigExportationRepository(
        @ApplicationContext context: Context,
        schedulerWebClient: SchedulerWebClient,
        schedulerConfigDAO: SchedulerConfigDAO
    ): SchedulerConfigExportationRepository {
        return SchedulerConfigExportationRepository(
            schedulerWebClient = schedulerWebClient,
            schedulerConfigDAO = schedulerConfigDAO,
            context = context
        )
    }
}