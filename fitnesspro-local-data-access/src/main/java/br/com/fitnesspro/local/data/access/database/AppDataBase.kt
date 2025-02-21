package br.com.fitnesspro.local.data.access.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.RenameColumn
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import br.com.fitnesspro.local.data.access.converters.RoomTypeConverters
import br.com.fitnesspro.local.data.access.dao.AcademyDAO
import br.com.fitnesspro.local.data.access.dao.PersonAcademyTimeDAO
import br.com.fitnesspro.local.data.access.dao.PersonDAO
import br.com.fitnesspro.local.data.access.dao.SchedulerConfigDAO
import br.com.fitnesspro.local.data.access.dao.SchedulerDAO
import br.com.fitnesspro.local.data.access.dao.UserDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutDAO
import br.com.fitnesspro.local.data.access.dao.WorkoutGroupDAO
import br.com.fitnesspro.model.general.Academy
import br.com.fitnesspro.model.general.Person
import br.com.fitnesspro.model.general.PersonAcademyTime
import br.com.fitnesspro.model.general.User
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
import br.com.fitnesspro.model.workout.Exercise
import br.com.fitnesspro.model.workout.Video
import br.com.fitnesspro.model.workout.VideoExercise
import br.com.fitnesspro.model.workout.Workout
import br.com.fitnesspro.model.workout.WorkoutGroup
import br.com.fitnesspro.model.workout.execution.ExerciseExecution
import br.com.fitnesspro.model.workout.execution.VideoExerciseExecution
import br.com.fitnesspro.model.workout.predefinition.ExercisePreDefinition
import br.com.fitnesspro.model.workout.predefinition.VideoExercisePreDefinition
import br.com.fitnesspro.model.workout.predefinition.WorkoutGroupPreDefinition

@Database(
    version = 10,
    entities = [
        User::class, Person::class, Academy::class, PersonAcademyTime::class, PhysicEvaluation::class,
        IngredientPreDefinition::class, MealOptionPreDefinition::class, Diet::class, DayWeekDiet::class, Meal::class,
        MealOption::class, Ingredient::class, Scheduler::class, SchedulerConfig::class, ExerciseExecution::class,
        VideoExerciseExecution::class, ExercisePreDefinition::class, VideoExercisePreDefinition::class,
        WorkoutGroupPreDefinition::class, Exercise::class, Video::class, VideoExercise::class,
        Workout::class, WorkoutGroup::class
    ],
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3, spec = AutoMigrationSpec2To3::class),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8),
        AutoMigration(from = 8, to = 9, spec = AutoMigrationSpec8To9::class),
        AutoMigration(from = 9, to = 10)
    ]
)
@TypeConverters(RoomTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDAO(): UserDAO

    abstract fun personDAO(): PersonDAO

    abstract fun academyDAO(): AcademyDAO

    abstract fun personAcademyTimeDAO(): PersonAcademyTimeDAO

    abstract fun schedulerDAO(): SchedulerDAO

    abstract fun schedulerConfigDAO(): SchedulerConfigDAO

    abstract fun workoutDAO(): WorkoutDAO

    abstract fun workoutGroupDAO(): WorkoutGroupDAO

}

@RenameColumn(tableName = "person_academy_time", fromColumnName = "date_time_start", toColumnName = "time_start")
@RenameColumn(tableName = "person_academy_time", fromColumnName = "date_time_end", toColumnName = "time_end")
class AutoMigrationSpec2To3 : AutoMigrationSpec

@DeleteColumn("scheduler_config", "start_work_time")
@DeleteColumn("scheduler_config", "end_work_time")
@DeleteColumn("scheduler_config", "start_break_time")
@DeleteColumn("scheduler_config", "end_break_time")
class AutoMigrationSpec8To9 : AutoMigrationSpec