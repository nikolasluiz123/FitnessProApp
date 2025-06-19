package br.com.fitnesspor.service.data.access.injection

import android.content.Context
import br.com.fitnesspor.service.data.access.service.general.IAcademyService
import br.com.fitnesspor.service.data.access.service.general.IAuthenticationService
import br.com.fitnesspor.service.data.access.service.general.IPersonService
import br.com.fitnesspor.service.data.access.service.log.IExecutionLogService
import br.com.fitnesspor.service.data.access.service.scheduler.ISchedulerService
import br.com.fitnesspor.service.data.access.service.workout.IExerciseService
import br.com.fitnesspor.service.data.access.service.workout.IWorkoutService
import br.com.fitnesspor.service.data.access.webclient.ExecutionLogWebClient
import br.com.fitnesspor.service.data.access.webclient.general.AcademyWebClient
import br.com.fitnesspor.service.data.access.webclient.general.AuthenticationWebClient
import br.com.fitnesspor.service.data.access.webclient.general.PersonWebClient
import br.com.fitnesspor.service.data.access.webclient.scheduler.SchedulerWebClient
import br.com.fitnesspor.service.data.access.webclient.workout.ExerciseWebClient
import br.com.fitnesspor.service.data.access.webclient.workout.WorkoutWebClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SingletonWebClientModule {

    @Provides
    fun provideAcademyWebClient(
        @ApplicationContext context: Context,
        academyService: IAcademyService
    ): AcademyWebClient {
        return AcademyWebClient(
            context = context,
            academyService = academyService
        )
    }

    @Provides
    fun provideAuthenticationWebClient(
        @ApplicationContext context: Context,
        authenticationService: IAuthenticationService
    ): AuthenticationWebClient {
        return AuthenticationWebClient(
            context = context,
            authenticationService = authenticationService
        )
    }

    @Provides
    fun providePersonWebClient(
        @ApplicationContext context: Context,
        personService: IPersonService,
    ): PersonWebClient {
        return PersonWebClient(
            context = context,
            personService = personService,
        )
    }

    @Provides
    fun provideSchedulerWebClient(
        @ApplicationContext context: Context,
        schedulerService: ISchedulerService,
    ): SchedulerWebClient {
        return SchedulerWebClient(
            context = context,
            schedulerService = schedulerService,
        )
    }

    @Provides
    fun provideExecutionLogWebClient(
        @ApplicationContext context: Context,
        executionLogService: IExecutionLogService
    ): ExecutionLogWebClient {
        return ExecutionLogWebClient(
            context = context,
            executionLogService = executionLogService
        )
    }

    @Provides
    fun provideExerciseWebClient(
        @ApplicationContext context: Context,
        exerciseService: IExerciseService
    ): ExerciseWebClient {
        return ExerciseWebClient(
            context = context,
            exerciseService = exerciseService
        )
    }

    @Provides
    fun provideWorkoutWebClient(
        @ApplicationContext context: Context,
        workoutService: IWorkoutService
    ): WorkoutWebClient {
        return WorkoutWebClient(
            context = context,
            workoutService = workoutService
        )
    }
}