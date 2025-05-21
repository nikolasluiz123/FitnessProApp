package br.com.fitnesspro.workout.injection

import android.content.Context
import br.com.fitnesspro.common.repository.PersonRepository
import br.com.fitnesspro.local.data.access.dao.WorkoutDAO
import br.com.fitnesspro.workout.repository.WorkoutRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SingletonWorkoutRepositoryModule {

    @Provides
    fun provideWorkoutRepository(
        @ApplicationContext context: Context,
        workoutDAO: WorkoutDAO,
        personRepository: PersonRepository
    ): WorkoutRepository {
        return WorkoutRepository(
            context = context,
            workoutDAO = workoutDAO,
            personRepository = personRepository
        )
    }
}