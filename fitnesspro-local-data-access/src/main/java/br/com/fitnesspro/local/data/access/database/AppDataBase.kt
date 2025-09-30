package br.com.fitnesspro.local.data.access.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.android.room.toolkit.converters.DefaultRoomTypeConverters
import br.com.fitnesspro.local.data.access.dao.AcademyDAO
import br.com.fitnesspro.local.data.access.dao.ApplicationDAO
import br.com.fitnesspro.local.data.access.dao.DeviceDAO
import br.com.fitnesspro.local.data.access.dao.ExerciseDAO
import br.com.fitnesspro.local.data.access.dao.ExerciseExecutionDAO
import br.com.fitnesspro.local.data.access.dao.ExercisePreDefinitionDAO
import br.com.fitnesspro.local.data.access.dao.ImportationHistoryDAO
import br.com.fitnesspro.local.data.access.dao.PersonAcademyTimeDAO
import br.com.fitnesspro.local.data.access.dao.PersonDAO
import br.com.fitnesspro.local.data.access.dao.ReportDAO
import br.com.fitnesspro.local.data.access.dao.SchedulerConfigDAO
import br.com.fitnesspro.local.data.access.dao.SchedulerDAO
import br.com.fitnesspro.local.data.access.dao.SchedulerReportDAO
import br.com.fitnesspro.local.data.access.dao.ServiceTokenDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.local.data.access.dao.VideoDAO
import br.com.fitnesspro.local.data.access.dao.VideoExerciseDAO
import br.com.fitnesspro.local.data.access.dao.VideoExerciseExecutionDAO
import br.com.fitnesspro.local.data.access.dao.VideoExercisePreDefinitionDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupPreDefinitionDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutReportDAO
import br.com.fitnesspro.local.data.access.dao.health.HealthConnectCaloriesBurnedDAO
import br.com.fitnesspro.local.data.access.dao.health.HealthConnectHeartRateDAO
import br.com.fitnesspro.local.data.access.dao.health.HealthConnectHeartRateSamplesDAO
import br.com.fitnesspro.local.data.access.dao.health.HealthConnectMetadataDAO
import br.com.fitnesspro.local.data.access.dao.health.HealthConnectSleepSessionDAO
import br.com.fitnesspro.local.data.access.dao.health.HealthConnectSleepStagesDAO
import br.com.fitnesspro.local.data.access.dao.health.HealthConnectStepsDAO
import br.com.fitnesspro.local.data.access.dao.health.SleepSessionExerciseExecutionDAO
import br.com.fitnesspro.model.authentication.Application
import br.com.fitnesspro.model.authentication.Device
import br.com.fitnesspro.model.authentication.ServiceToken
import br.com.fitnesspro.model.general.Academy
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.PersonAcademyTime
import br.com.fitnesspro.model.general.User
import br.com.fitnesspro.model.general.report.Report
import br.com.fitnesspro.model.general.report.SchedulerReport
import br.com.fitnesspro.model.general.report.WorkoutReport
import br.com.fitnesspro.model.nutrition.avaliation.PhysicEvaluation
import br.com.fitnesspro.model.nutrition.diet.DayWeekDiet
import br.com.fitnesspro.model.nutrition.diet.Diet
import br.com.fitnesspro.model.nutrition.diet.Ingredient
import br.com.fitnesspro.model.nutrition.diet.Meal
import br.com.fitnesspro.model.nutrition.diet.MealOption
import br.com.fitnesspro.model.nutrition.diet.predefinition.IngredientPreDefinition
import br.com.fitnesspro.model.nutrition.diet.predefinition.MealOptionPreDefinition
import br.com.fitnesspro.model.scheduler.Scheduler
import br.com.fitnesspro.model.scheduler.SchedulerConfig
import br.com.fitnesspro.model.sync.ImportationHistory
import br.com.fitnesspro.model.workout.Exercise
import br.com.fitnesspro.model.workout.Video
import br.com.fitnesspro.model.workout.VideoExercise
import br.com.fitnesspro.model.workout.Workout
import br.com.fitnesspro.model.workout.WorkoutGroup
import br.com.fitnesspro.model.workout.execution.ExerciseExecution
import br.com.fitnesspro.model.workout.execution.VideoExerciseExecution
import br.com.fitnesspro.model.workout.health.HealthConnectCaloriesBurned
import br.com.fitnesspro.model.workout.health.HealthConnectHeartRate
import br.com.fitnesspro.model.workout.health.HealthConnectHeartRateSamples
import br.com.fitnesspro.model.workout.health.HealthConnectMetadata
import br.com.fitnesspro.model.workout.health.HealthConnectSleepSession
import br.com.fitnesspro.model.workout.health.HealthConnectSleepStages
import br.com.fitnesspro.model.workout.health.HealthConnectSteps
import br.com.fitnesspro.model.workout.health.SleepSessionExerciseExecution
import br.com.fitnesspro.model.workout.predefinition.ExercisePreDefinition
import br.com.fitnesspro.model.workout.predefinition.VideoExercisePreDefinition
import br.com.fitnesspro.model.workout.predefinition.WorkoutGroupPreDefinition

@Database(
    version = 52,
    entities = [
        User::class, Person::class, Academy::class, PersonAcademyTime::class, PhysicEvaluation::class,
        IngredientPreDefinition::class, MealOptionPreDefinition::class, Diet::class, DayWeekDiet::class, Meal::class,
        MealOption::class, Ingredient::class, Scheduler::class, SchedulerConfig::class, ExerciseExecution::class,
        VideoExerciseExecution::class, ExercisePreDefinition::class, VideoExercisePreDefinition::class,
        WorkoutGroupPreDefinition::class, Exercise::class, Video::class, VideoExercise::class,
        Workout::class, WorkoutGroup::class, ImportationHistory::class, ServiceToken::class, Device::class,
        Application::class, Report::class, SchedulerReport::class, HealthConnectCaloriesBurned::class,
        HealthConnectHeartRate::class, HealthConnectHeartRateSamples::class, HealthConnectMetadata::class,
        HealthConnectSleepSession::class, HealthConnectSleepStages::class, HealthConnectSteps::class,
        SleepSessionExerciseExecution::class, WorkoutReport::class
    ],
    exportSchema = true
)
@TypeConverters(DefaultRoomTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDAO(): UserDAO

    abstract fun personDAO(): PersonDAO

    abstract fun academyDAO(): AcademyDAO

    abstract fun personAcademyTimeDAO(): PersonAcademyTimeDAO

    abstract fun schedulerDAO(): SchedulerDAO

    abstract fun schedulerConfigDAO(): SchedulerConfigDAO

    abstract fun workoutDAO(): WorkoutDAO

    abstract fun workoutGroupDAO(): WorkoutGroupDAO

    abstract fun syncHistoryDAO(): ImportationHistoryDAO

    abstract fun deviceDAO(): DeviceDAO

    abstract fun applicationDAO(): ApplicationDAO

    abstract fun serviceTokenDAO(): ServiceTokenDAO

    abstract fun exerciseDAO(): ExerciseDAO

    abstract fun exercisePreDefinitionDAO(): ExercisePreDefinitionDAO

    abstract fun workoutGroupPreDefinitionDAO(): WorkoutGroupPreDefinitionDAO

    abstract fun videoDAO(): VideoDAO

    abstract fun videoExerciseDAO(): VideoExerciseDAO

    abstract fun exerciseExecutionDAO(): ExerciseExecutionDAO

    abstract fun videoExerciseExecutionDAO(): VideoExerciseExecutionDAO

    abstract fun videoExercisePreDefinitionDAO(): VideoExercisePreDefinitionDAO

    abstract fun reportDAO(): ReportDAO

    abstract fun schedulerReportDAO(): SchedulerReportDAO

    abstract fun healthConnectMetadataDAO(): HealthConnectMetadataDAO

    abstract fun healthConnectStepsDAO(): HealthConnectStepsDAO

    abstract fun healthConnectCaloriesBurnedDAO(): HealthConnectCaloriesBurnedDAO

    abstract fun healthConnectHeartRateDAO(): HealthConnectHeartRateDAO

    abstract fun healthConnectHeartRateSamplesDAO(): HealthConnectHeartRateSamplesDAO

    abstract fun healthConnectSleepSessionDAO(): HealthConnectSleepSessionDAO

    abstract fun healthConnectSleepStagesDAO(): HealthConnectSleepStagesDAO

    abstract fun sleepSessionExerciseExecutionDAO(): SleepSessionExerciseExecutionDAO

    abstract fun workoutReportDAO(): WorkoutReportDAO

}