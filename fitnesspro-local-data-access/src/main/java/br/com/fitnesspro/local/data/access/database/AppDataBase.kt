package br.com.fitnesspro.local.data.access.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.fitnesspro.local.data.access.converters.RoomTypeConverters
import br.com.fitnesspro.local.data.access.dao.AcademyDAO
import br.com.fitnesspro.local.data.access.dao.ImportationHistoryDAO
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
import br.com.fitnesspro.model.sync.ImportationHistory
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
    version = 14,
    entities = [
        User::class, Person::class, Academy::class, PersonAcademyTime::class, PhysicEvaluation::class,
        IngredientPreDefinition::class, MealOptionPreDefinition::class, Diet::class, DayWeekDiet::class, Meal::class,
        MealOption::class, Ingredient::class, Scheduler::class, SchedulerConfig::class, ExerciseExecution::class,
        VideoExerciseExecution::class, ExercisePreDefinition::class, VideoExercisePreDefinition::class,
        WorkoutGroupPreDefinition::class, Exercise::class, Video::class, VideoExercise::class,
        Workout::class, WorkoutGroup::class, ImportationHistory::class
    ],
    exportSchema = true
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

    abstract fun syncHistoryDAO(): ImportationHistoryDAO

}