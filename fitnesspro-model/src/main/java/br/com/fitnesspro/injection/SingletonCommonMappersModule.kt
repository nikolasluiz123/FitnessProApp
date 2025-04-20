package br.com.fitnesspro.injection

import br.com.fitnesspro.mappers.AcademyModelMapper
import br.com.fitnesspro.mappers.PersonModelMapper
import br.com.fitnesspro.mappers.SchedulerModelMapper
import br.com.fitnesspro.mappers.WorkoutModelMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SingletonCommonMappersModule {

    @Provides
    fun provideAcademyModelMapper(): AcademyModelMapper {
        return AcademyModelMapper()
    }

    @Provides
    fun providePersonModelMapper(): PersonModelMapper {
        return PersonModelMapper()
    }

    @Provides
    fun provideSchedulerModelMapper(): SchedulerModelMapper {
        return SchedulerModelMapper()
    }

    @Provides
    fun provideWorkoutModelMapper(): WorkoutModelMapper {
        return WorkoutModelMapper()
    }

}