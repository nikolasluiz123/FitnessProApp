package br.com.fitnesspro.common.injection

import android.content.Context
import br.com.fitnesspro.common.repository.AcademyRepository
import br.com.fitnesspro.common.repository.ApplicationRepository
import br.com.fitnesspro.common.repository.DeviceRepository
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.common.repository.ReportRepository
import br.com.fitnesspro.common.repository.ServiceTokenRepository
import br.com.fitnesspro.common.repository.UserRepository
import br.com.fitnesspro.common.repository.sync.exportation.GeneralModuleExportationRepository
import br.com.fitnesspro.common.repository.sync.exportation.storage.ReportStorageExportationRepository
import br.com.fitnesspro.common.repository.sync.exportation.storage.VideoStorageExportationRepository
import br.com.fitnesspro.common.repository.sync.importation.GeneralModuleImportationRepository
import br.com.fitnesspro.common.repository.sync.importation.storage.ReportStorageImportationRepository
import br.com.fitnesspro.common.repository.sync.importation.storage.VideoStorageImportationRepository
import br.com.fitnesspro.firebase.api.authentication.FirebaseDefaultAuthenticationService
import br.com.fitnesspro.firebase.api.authentication.FirebaseGoogleAuthenticationService
import br.com.fitnesspro.firebase.api.storage.StorageBucketService
import br.com.fitnesspro.local.data.access.dao.AcademyDAO
import br.com.fitnesspro.local.data.access.dao.ApplicationDAO
import br.com.fitnesspro.local.data.access.dao.DeviceDAO
import br.com.fitnesspro.local.data.access.dao.PersonAcademyTimeDAO
import br.com.fitnesspro.local.data.access.dao.PersonDAO
import br.com.fitnesspro.local.data.access.dao.ReportDAO
import br.com.fitnesspro.local.data.access.dao.SchedulerReportDAO
import br.com.fitnesspro.local.data.access.dao.ServiceTokenDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.local.data.access.dao.VideoDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutReportDAO
import br.com.fitnesspro.service.data.access.webclient.general.AuthenticationWebClient
import br.com.fitnesspro.service.data.access.webclient.general.PersonWebClient
import br.com.fitnesspro.service.data.access.webclient.storage.StorageWebClient
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
    ): PersonRepository {
        return PersonRepository(
            personDAO = personDAO,
            userDAO = userDAO,
            firebaseDefaultAuthenticationService = firebaseDefaultAuthenticationService,
            personWebClient = personWebClient,
            context = context,
        )
    }

    @Provides
    fun provideAcademyRepository(
        @ApplicationContext context: Context,
        academyDAO: AcademyDAO,
        personAcademyTimeDAO: PersonAcademyTimeDAO,
    ): AcademyRepository {
        return AcademyRepository(
            context = context,
            academyDAO = academyDAO,
            personAcademyTimeDAO = personAcademyTimeDAO,
        )
    }

    @Provides
    fun provideDeviceRepository(
        @ApplicationContext context: Context,
        deviceDAO: DeviceDAO,
        personRepository: PersonRepository
    ): DeviceRepository {
        return DeviceRepository(
            context = context,
            deviceDAO = deviceDAO,
            personRepository = personRepository
        )
    }

    @Provides
    fun provideApplicationRepository(
        @ApplicationContext context: Context,
        applicationDAO: ApplicationDAO,
    ): ApplicationRepository {
        return ApplicationRepository(
            context = context,
            applicationDAO = applicationDAO,
        )
    }

    @Provides
    fun provideServiceTokenRepository(
        @ApplicationContext context: Context,
        deviceRepository: DeviceRepository,
        applicationRepository: ApplicationRepository,
        serviceTokenDAO: ServiceTokenDAO,
    ): ServiceTokenRepository {
        return ServiceTokenRepository(
            context = context,
            deviceRepository = deviceRepository,
            applicationRepository = applicationRepository,
            serviceTokenDAO = serviceTokenDAO,
        )
    }

    @Provides
    fun provideReportRepository(
        @ApplicationContext context: Context,
        reportDAO: ReportDAO,
        schedulerReportDAO: SchedulerReportDAO,
        personRepository: PersonRepository,
        workoutReportDAO: WorkoutReportDAO
    ): ReportRepository {
        return ReportRepository(
            context = context,
            reportDAO = reportDAO,
            schedulerReportDAO = schedulerReportDAO,
            personRepository = personRepository,
            workoutReportDAO = workoutReportDAO
        )
    }

    @Provides
    fun provideReportStorageExportationRepository(
        @ApplicationContext context: Context,
        reportDAO: ReportDAO,
        storageWebClient: StorageWebClient
    ): ReportStorageExportationRepository {
        return ReportStorageExportationRepository(
            reportDAO = reportDAO,
            storageWebClient = storageWebClient,
            context = context
        )
    }

    @Provides
    fun provideVideoStorageExportationRepository(
        @ApplicationContext context: Context,
        videoDAO: VideoDAO,
        storageWebClient: StorageWebClient
    ): VideoStorageExportationRepository {
        return VideoStorageExportationRepository(
            videoDAO = videoDAO,
            storageWebClient = storageWebClient,
            context = context
        )
    }

    @Provides
    fun provideReportStorageImportationRepository(
        @ApplicationContext context: Context,
        reportDAO: ReportDAO,
        storageBucketService: StorageBucketService
    ): ReportStorageImportationRepository {
        return ReportStorageImportationRepository(
            reportDAO = reportDAO,
            context = context,
            storageService = storageBucketService
        )
    }

    @Provides
    fun provideVideoStorageImportationRepository(
        @ApplicationContext context: Context,
        videoDAO: VideoDAO,
        storageBucketService: StorageBucketService
    ): VideoStorageImportationRepository {
        return VideoStorageImportationRepository(
            videoDAO = videoDAO,
            context = context,
            storageService = storageBucketService
        )
    }

    @Provides
    fun provideGeneralModuleImportationRepository(
        @ApplicationContext context: Context,
    ): GeneralModuleImportationRepository {
        return GeneralModuleImportationRepository(context)
    }

    @Provides
    fun provideGeneralModuleExportationRepository(
        @ApplicationContext context: Context,
    ): GeneralModuleExportationRepository {
        return GeneralModuleExportationRepository(context)
    }
}