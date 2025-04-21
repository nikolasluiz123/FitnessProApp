package br.com.fitnesspro.common.injection

import android.content.Context
import br.com.fitnesspor.service.data.access.webclient.general.AcademyWebClient
import br.com.fitnesspor.service.data.access.webclient.general.AuthenticationWebClient
import br.com.fitnesspor.service.data.access.webclient.general.PersonWebClient
import br.com.fitnesspor.service.data.access.webclient.scheduler.SchedulerWebClient
import br.com.fitnesspro.common.repository.AcademyRepository
import br.com.fitnesspro.common.repository.ApplicationRepository
import br.com.fitnesspro.common.repository.DeviceRepository
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.ServiceTokenRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.repository.sync.exportation.PersonAcademyTimeExportationRepository
import br.com.fitnesspro.common.repository.sync.exportation.PersonExportationRepository
import br.com.fitnesspro.common.repository.sync.exportation.SchedulerConfigExportationRepository
import br.com.fitnesspro.common.repository.sync.importation.AcademyImportationRepository
import br.com.fitnesspro.common.repository.sync.importation.PersonAcademyTimeImportationRepository
import br.com.fitnesspro.common.repository.sync.importation.PersonImportationRepository
import br.com.fitnesspro.common.repository.sync.importation.SchedulerConfigImportationRepository
import br.com.fitnesspro.common.repository.sync.importation.UserImportationRepository
import br.com.fitnesspro.firebase.api.authentication.FirebaseDefaultAuthenticationService
import br.com.fitnesspro.firebase.api.authentication.FirebaseGoogleAuthenticationService
import br.com.fitnesspro.local.data.access.dao.AcademyDAO
import br.com.fitnesspro.local.data.access.dao.ApplicationDAO
import br.com.fitnesspro.local.data.access.dao.DeviceDAO
import br.com.fitnesspro.local.data.access.dao.PersonAcademyTimeDAO
import br.com.fitnesspro.local.data.access.dao.PersonDAO
import br.com.fitnesspro.local.data.access.dao.SchedulerConfigDAO
import br.com.fitnesspro.local.data.access.dao.ServiceTokenDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.mappers.AcademyModelMapper
import br.com.fitnesspro.mappers.ApplicationModelMapper
import br.com.fitnesspro.mappers.DeviceModelMapper
import br.com.fitnesspro.mappers.PersonModelMapper
import br.com.fitnesspro.mappers.SchedulerModelMapper
import br.com.fitnesspro.mappers.ServiceTokenModelMapper
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
        @ApplicationContext context: Context,
        userDAO: UserDAO,
        firebaseDefaultAuthenticationService: FirebaseDefaultAuthenticationService,
        firebaseGoogleAuthenticationService: FirebaseGoogleAuthenticationService,
        authenticationWebClient: AuthenticationWebClient,
        personRepository: PersonRepository,
        personModelMapper: PersonModelMapper,
        deviceRepository: DeviceRepository,
        serviceTokenRepository: ServiceTokenRepository
    ): UserRepository {
        return UserRepository(
            userDAO = userDAO,
            firebaseDefaultAuthenticationService = firebaseDefaultAuthenticationService,
            firebaseGoogleAuthenticationService = firebaseGoogleAuthenticationService,
            authenticationWebClient = authenticationWebClient,
            context = context,
            personRepository = personRepository,
            personModelMapper = personModelMapper,
            deviceRepository = deviceRepository,
            serviceTokenRepository = serviceTokenRepository
        )
    }

    @Provides
    fun providePersonRepository(
        @ApplicationContext context: Context,
        personDAO: PersonDAO,
        userDAO: UserDAO,
        firebaseDefaultAuthenticationService: FirebaseDefaultAuthenticationService,
        personWebClient: PersonWebClient,
        personModelMapper: PersonModelMapper
    ): PersonRepository {
        return PersonRepository(
            personDAO = personDAO,
            userDAO = userDAO,
            firebaseDefaultAuthenticationService = firebaseDefaultAuthenticationService,
            personWebClient = personWebClient,
            context = context,
            personModelMapper = personModelMapper
        )
    }

    @Provides
    fun provideAcademyRepository(
        @ApplicationContext context: Context,
        academyDAO: AcademyDAO,
        personAcademyTimeDAO: PersonAcademyTimeDAO,
        personWebClient: PersonWebClient,
        academyModelMapper: AcademyModelMapper
    ): AcademyRepository {
        return AcademyRepository(
            context = context,
            academyDAO = academyDAO,
            personAcademyTimeDAO = personAcademyTimeDAO,
            personWebClient = personWebClient,
            academyModelMapper = academyModelMapper
        )
    }

    @Provides
    fun provideUserImportationRepository(
        @ApplicationContext context: Context,
        personWebClient: PersonWebClient,
        personModelMapper: PersonModelMapper
    ): UserImportationRepository {
        return UserImportationRepository(
            webClient = personWebClient,
            context = context,
            personModelMapper = personModelMapper
        )
    }

    @Provides
    fun provideAcademyImportationRepository(
        @ApplicationContext context: Context,
        academyDAO: AcademyDAO,
        academyWebClient: AcademyWebClient,
        academyModelMapper: AcademyModelMapper
    ): AcademyImportationRepository {
        return AcademyImportationRepository(
            academyDAO = academyDAO,
            academyWebClient = academyWebClient,
            context = context,
            academyModelMapper = academyModelMapper
        )
    }

    @Provides
    fun providePersonImportationRepository(
        @ApplicationContext context: Context,
        personDAO: PersonDAO,
        personWebClient: PersonWebClient,
        personModelMapper: PersonModelMapper
    ): PersonImportationRepository {
        return PersonImportationRepository(
            personDAO = personDAO,
            webClient = personWebClient,
            context = context,
            personModelMapper = personModelMapper
        )
    }

    @Provides
    fun providePersonAcademyTimeImportationRepository(
        @ApplicationContext context: Context,
        personAcademyTimeDAO: PersonAcademyTimeDAO,
        personWebClient: PersonWebClient,
        academyModelMapper: AcademyModelMapper
    ): PersonAcademyTimeImportationRepository {
        return PersonAcademyTimeImportationRepository(
            personAcademyTimeDAO = personAcademyTimeDAO,
            webClient = personWebClient,
            context = context,
            academyModelMapper = academyModelMapper
        )
    }

    @Provides
    fun provideSchedulerConfigImportationRepository(
        @ApplicationContext context: Context,
        schedulerConfigDAO: SchedulerConfigDAO,
        schedulerWebClient: SchedulerWebClient,
        schedulerModelMapper: SchedulerModelMapper
    ): SchedulerConfigImportationRepository {
        return SchedulerConfigImportationRepository(
            schedulerConfigDAO = schedulerConfigDAO,
            webClient = schedulerWebClient,
            context = context,
            schedulerModelMapper = schedulerModelMapper
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
    fun providePersonAcademyTimeExportationRepository(
        @ApplicationContext context: Context,
        personAcademyTimeDAO: PersonAcademyTimeDAO,
        personWebClient: PersonWebClient
    ): PersonAcademyTimeExportationRepository {
        return PersonAcademyTimeExportationRepository(
            personAcademyTimeDAO = personAcademyTimeDAO,
            personWebClient = personWebClient,
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

    @Provides
    fun provideDeviceRepository(
        @ApplicationContext context: Context,
        deviceDAO: DeviceDAO,
        deviceModelMapper: DeviceModelMapper
    ): DeviceRepository {
        return DeviceRepository(
            context = context,
            deviceDAO = deviceDAO,
            deviceModelMapper = deviceModelMapper
        )
    }

    @Provides
    fun provideApplicationRepository(
        @ApplicationContext context: Context,
        applicationDAO: ApplicationDAO,
        applicationModelMapper: ApplicationModelMapper
    ): ApplicationRepository {
        return ApplicationRepository(
            context = context,
            applicationDAO = applicationDAO,
            applicationModelMapper = applicationModelMapper
        )
    }

    @Provides
    fun provideServiceTokenRepository(
        @ApplicationContext context: Context,
        deviceRepository: DeviceRepository,
        applicationRepository: ApplicationRepository,
        serviceTokenDAO: ServiceTokenDAO,
        serviceTokenModelMapper: ServiceTokenModelMapper
    ): ServiceTokenRepository {
        return ServiceTokenRepository(
            context = context,
            deviceRepository = deviceRepository,
            applicationRepository = applicationRepository,
            serviceTokenDAO = serviceTokenDAO,
            serviceTokenModelMapper = serviceTokenModelMapper
        )
    }
}