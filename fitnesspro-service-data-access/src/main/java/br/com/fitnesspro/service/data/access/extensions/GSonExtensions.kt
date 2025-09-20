package br.com.fitnesspro.service.data.access.extensions

import br.com.fitnesspro.core.adapters.InstantTypeAdapter
import br.com.fitnesspro.core.adapters.LocalDateTimeTypeAdapter
import br.com.fitnesspro.core.adapters.LocalDateTypeAdapter
import br.com.fitnesspro.core.adapters.LocalTimeTypeAdapter
import br.com.fitnesspro.core.adapters.OffsetDateTimeTypeAdapter
import br.com.fitnesspro.core.adapters.ZoneOffsetTypeAdapter
import br.com.fitnesspro.service.data.access.adapters.GenericInterfaceAdapterFactory
import br.com.fitnesspro.shared.communication.dtos.cache.CacheClearConfigDTO
import br.com.fitnesspro.shared.communication.dtos.cache.CacheDTO
import br.com.fitnesspro.shared.communication.dtos.cache.CacheEntryDTO
import br.com.fitnesspro.shared.communication.dtos.cache.interfaces.ICacheClearConfigDTO
import br.com.fitnesspro.shared.communication.dtos.cache.interfaces.ICacheDTO
import br.com.fitnesspro.shared.communication.dtos.cache.interfaces.ICacheEntryDTO
import br.com.fitnesspro.shared.communication.dtos.general.AcademyDTO
import br.com.fitnesspro.shared.communication.dtos.general.AuthenticationDTO
import br.com.fitnesspro.shared.communication.dtos.general.FindPersonDTO
import br.com.fitnesspro.shared.communication.dtos.general.PersonAcademyTimeDTO
import br.com.fitnesspro.shared.communication.dtos.general.PersonDTO
import br.com.fitnesspro.shared.communication.dtos.general.ReportDTO
import br.com.fitnesspro.shared.communication.dtos.general.SchedulerReportDTO
import br.com.fitnesspro.shared.communication.dtos.general.UserDTO
import br.com.fitnesspro.shared.communication.dtos.general.interfaces.IAcademyDTO
import br.com.fitnesspro.shared.communication.dtos.general.interfaces.IAuthenticationDTO
import br.com.fitnesspro.shared.communication.dtos.general.interfaces.IFindPersonDTO
import br.com.fitnesspro.shared.communication.dtos.general.interfaces.IPersonAcademyTimeDTO
import br.com.fitnesspro.shared.communication.dtos.general.interfaces.IPersonDTO
import br.com.fitnesspro.shared.communication.dtos.general.interfaces.IReportDTO
import br.com.fitnesspro.shared.communication.dtos.general.interfaces.ISchedulerReportDTO
import br.com.fitnesspro.shared.communication.dtos.general.interfaces.IUserDTO
import br.com.fitnesspro.shared.communication.dtos.logs.ExecutionLogDTO
import br.com.fitnesspro.shared.communication.dtos.logs.ExecutionLogPackageDTO
import br.com.fitnesspro.shared.communication.dtos.logs.UpdatableExecutionLogInfosDTO
import br.com.fitnesspro.shared.communication.dtos.logs.UpdatableExecutionLogPackageInfosDTO
import br.com.fitnesspro.shared.communication.dtos.logs.interfaces.IExecutionLogDTO
import br.com.fitnesspro.shared.communication.dtos.logs.interfaces.IExecutionLogPackageDTO
import br.com.fitnesspro.shared.communication.dtos.logs.interfaces.IUpdatableExecutionLogInfosDTO
import br.com.fitnesspro.shared.communication.dtos.logs.interfaces.IUpdatableExecutionLogPackageInfosDTO
import br.com.fitnesspro.shared.communication.dtos.notification.GlobalNotificationDTO
import br.com.fitnesspro.shared.communication.dtos.notification.NotificationDTO
import br.com.fitnesspro.shared.communication.dtos.notification.interfaces.IGlobalNotificationDTO
import br.com.fitnesspro.shared.communication.dtos.notification.interfaces.INotificationDTO
import br.com.fitnesspro.shared.communication.dtos.scheduledtask.IScheduledTaskDTO
import br.com.fitnesspro.shared.communication.dtos.scheduledtask.ScheduledTaskDTO
import br.com.fitnesspro.shared.communication.dtos.scheduler.RecurrentSchedulerDTO
import br.com.fitnesspro.shared.communication.dtos.scheduler.SchedulerConfigDTO
import br.com.fitnesspro.shared.communication.dtos.scheduler.SchedulerDTO
import br.com.fitnesspro.shared.communication.dtos.scheduler.interfaces.IRecurrentSchedulerDTO
import br.com.fitnesspro.shared.communication.dtos.scheduler.interfaces.ISchedulerConfigDTO
import br.com.fitnesspro.shared.communication.dtos.scheduler.interfaces.ISchedulerDTO
import br.com.fitnesspro.shared.communication.dtos.serviceauth.ApplicationDTO
import br.com.fitnesspro.shared.communication.dtos.serviceauth.DeviceDTO
import br.com.fitnesspro.shared.communication.dtos.serviceauth.ServiceTokenDTO
import br.com.fitnesspro.shared.communication.dtos.serviceauth.ServiceTokenGenerationDTO
import br.com.fitnesspro.shared.communication.dtos.serviceauth.interfaces.IApplicationDTO
import br.com.fitnesspro.shared.communication.dtos.serviceauth.interfaces.IDeviceDTO
import br.com.fitnesspro.shared.communication.dtos.serviceauth.interfaces.IServiceTokenDTO
import br.com.fitnesspro.shared.communication.dtos.serviceauth.interfaces.IServiceTokenGenerationDTO
import br.com.fitnesspro.shared.communication.dtos.workout.ExerciseDTO
import br.com.fitnesspro.shared.communication.dtos.workout.ExerciseExecutionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.ExercisePreDefinitionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.VideoDTO
import br.com.fitnesspro.shared.communication.dtos.workout.VideoExerciseDTO
import br.com.fitnesspro.shared.communication.dtos.workout.VideoExerciseExecutionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.VideoExercisePreDefinitionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutDTO
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutGroupDTO
import br.com.fitnesspro.shared.communication.dtos.workout.WorkoutGroupPreDefinitionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.health.HealthConnectCaloriesBurnedDTO
import br.com.fitnesspro.shared.communication.dtos.workout.health.HealthConnectHeartRateDTO
import br.com.fitnesspro.shared.communication.dtos.workout.health.HealthConnectHeartRateSamplesDTO
import br.com.fitnesspro.shared.communication.dtos.workout.health.HealthConnectMetadataDTO
import br.com.fitnesspro.shared.communication.dtos.workout.health.HealthConnectSleepSessionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.health.HealthConnectSleepStagesDTO
import br.com.fitnesspro.shared.communication.dtos.workout.health.HealthConnectStepsDTO
import br.com.fitnesspro.shared.communication.dtos.workout.health.SleepSessionExerciseExecutionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.IExerciseDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.IExerciseExecutionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.IExercisePreDefinitionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.IVideoDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.IVideoExerciseDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.IVideoExerciseExecutionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.IVideoExercisePreDefinitionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.IWorkoutDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.IWorkoutGroupDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.IWorkoutGroupPreDefinitionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.health.IHealthConnectCaloriesBurnedDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.health.IHealthConnectHeartRateDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.health.IHealthConnectHeartRateSamplesDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.health.IHealthConnectMetadataDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.health.IHealthConnectSleepSessionDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.health.IHealthConnectSleepStagesDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.health.IHealthConnectStepsDTO
import br.com.fitnesspro.shared.communication.dtos.workout.interfaces.health.ISleepSessionExerciseExecutionDTO
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

fun GsonBuilder.defaultServiceGSon(): Gson {
    return registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeTypeAdapter())
        .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter())
        .registerTypeAdapter(LocalTime::class.java, LocalTimeTypeAdapter())
        .registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeTypeAdapter())
        .registerTypeAdapter(Instant::class.java, InstantTypeAdapter())
        .registerTypeAdapter(ZoneOffset::class.java, ZoneOffsetTypeAdapter())

        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(ICacheClearConfigDTO::class.java, CacheClearConfigDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(ICacheDTO::class.java, CacheDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(ICacheEntryDTO::class.java, CacheEntryDTO::class.java))

        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IAcademyDTO::class.java, AcademyDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IAuthenticationDTO::class.java, AuthenticationDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IFindPersonDTO::class.java, FindPersonDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IPersonAcademyTimeDTO::class.java, PersonAcademyTimeDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IPersonDTO::class.java, PersonDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IReportDTO::class.java, ReportDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(ISchedulerReportDTO::class.java, SchedulerReportDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IUserDTO::class.java, UserDTO::class.java))

        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IExecutionLogDTO::class.java, ExecutionLogDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IExecutionLogPackageDTO::class.java, ExecutionLogPackageDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IUpdatableExecutionLogInfosDTO::class.java, UpdatableExecutionLogInfosDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IUpdatableExecutionLogPackageInfosDTO::class.java, UpdatableExecutionLogPackageInfosDTO::class.java))

        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IGlobalNotificationDTO::class.java, GlobalNotificationDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(INotificationDTO::class.java, NotificationDTO::class.java))

        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IScheduledTaskDTO::class.java, ScheduledTaskDTO::class.java))

        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IRecurrentSchedulerDTO::class.java, RecurrentSchedulerDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(ISchedulerConfigDTO::class.java, SchedulerConfigDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(ISchedulerDTO::class.java, SchedulerDTO::class.java))

        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IApplicationDTO::class.java, ApplicationDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IDeviceDTO::class.java, DeviceDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IServiceTokenDTO::class.java, ServiceTokenDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IServiceTokenGenerationDTO::class.java, ServiceTokenGenerationDTO::class.java))

        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IExerciseDTO::class.java, ExerciseDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IExerciseExecutionDTO::class.java, ExerciseExecutionDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IExercisePreDefinitionDTO::class.java, ExercisePreDefinitionDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IVideoDTO::class.java, VideoDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IVideoExerciseDTO::class.java, VideoExerciseDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IVideoExerciseExecutionDTO::class.java, VideoExerciseExecutionDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IVideoExercisePreDefinitionDTO::class.java, VideoExercisePreDefinitionDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IWorkoutDTO::class.java, WorkoutDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IWorkoutGroupDTO::class.java, WorkoutGroupDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IWorkoutGroupPreDefinitionDTO::class.java,WorkoutGroupPreDefinitionDTO::class.java))

        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IHealthConnectMetadataDTO::class.java, HealthConnectMetadataDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IHealthConnectStepsDTO::class.java, HealthConnectStepsDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IHealthConnectCaloriesBurnedDTO::class.java, HealthConnectCaloriesBurnedDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IHealthConnectHeartRateDTO::class.java, HealthConnectHeartRateDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IHealthConnectHeartRateSamplesDTO::class.java, HealthConnectHeartRateSamplesDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IHealthConnectSleepSessionDTO::class.java, HealthConnectSleepSessionDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(IHealthConnectSleepStagesDTO::class.java, HealthConnectSleepStagesDTO::class.java))
        .registerTypeAdapterFactory(GenericInterfaceAdapterFactory(ISleepSessionExerciseExecutionDTO::class.java, SleepSessionExerciseExecutionDTO::class.java))
        .create()
}