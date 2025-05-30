package br.com.fitnesspro.local.data.access.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.model.workout.predefinition.ExercisePreDefinition
import br.com.fitnesspro.model.workout.predefinition.WorkoutGroupPreDefinition
import br.com.fitnesspro.to.TOExercise
import java.util.StringJoiner

@Dao
abstract class ExercisePreDefinitionDAO: IntegratedMaintenanceDAO<ExercisePreDefinition>() {

    fun getExercisesPreDefinitionFromWorkoutGroup(
        workoutGroupName: String,
        authenticatedPersonId: String,
        simpleFilter: String
    ): PagingSource<Int, TOExercise> {
        val queryParams = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select exercise.id as id, ")
            add("        exercise.name as name, ")
            add("        exercise.duration as duration, ")
            add("        exercise.repetitions as repetitions, ")
            add("        exercise.sets as sets, ")
            add("        exercise.rest as rest, ")
            add("        exercise.observation as observation, ")
            add("        exercise.workout_group_id as workoutGroupId, ")
            add("        exercise.active as active ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from exercise_pre_definition exercise ")
            add(" inner join workout_group_pre_definition workout_group on workout_group.id = exercise.workout_group_pre_definition_id ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where exercise.personal_trainer_person_id = ? ")
            add(" and exercise.active = 1 ")

            queryParams.add(authenticatedPersonId)

            if (workoutGroupName.isNotEmpty()) {
                add(" and lower(workout_group.name) = ? ")
                queryParams.add(workoutGroupName.lowercase())
            }

            if (simpleFilter.isNotEmpty()) {
                add(" and lower(exercise.name) like ? ")
                queryParams.add("%${simpleFilter.lowercase()}%")
            }
        }

        val orderBy = StringJoiner(QR_NL).apply {
            add(" order by exercise.name ")
        }

        val sql = StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
            add(orderBy.toString())
        }

        return executeExercisesFromWorkoutGroup(SimpleSQLiteQuery(sql.toString(), queryParams.toTypedArray()))
    }

    @RawQuery(observedEntities = [ExercisePreDefinition::class, WorkoutGroupPreDefinition::class])
    abstract fun executeExercisesFromWorkoutGroup(query: SupportSQLiteQuery): PagingSource<Int, TOExercise>

    @Query("select * from exercise_pre_definition where lower(name) = lower(:name) and active = 1")
    abstract suspend fun findExercisePreDefinitionByName(name: String): ExercisePreDefinition?

}