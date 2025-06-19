package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.model.workout.Exercise
import br.com.fitnesspro.to.TOExercise
import java.util.StringJoiner

@Dao
abstract class ExerciseDAO: IntegratedMaintenanceDAO<Exercise>() {

    suspend fun getExercisesFromWorkoutGroup(workoutGroupIds: List<String>): List<TOExercise> {
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
            add(" from exercise ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where exercise.workout_group_id in ")
            concatElementsForIn(workoutGroupIds, queryParams)

            add(" and exercise.active = 1 ")

        }

        val orderBy = StringJoiner(QR_NL).apply {
            add(" order by exercise.exercise_order ")
        }

        val sql = StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
            add(orderBy.toString())
        }

        return executeExercisesFromWorkoutGroup(SimpleSQLiteQuery(sql.toString(), queryParams.toTypedArray()))
    }

    @RawQuery
    abstract suspend fun executeExercisesFromWorkoutGroup(query: SupportSQLiteQuery): List<TOExercise>

    @Query("select * from exercise where id = :id")
    abstract suspend fun findById(id: String): Exercise

    @Query("select * from exercise where workout_group_id = :workoutGroupId")
    abstract suspend fun findExerciesFromWorkoutGroup(workoutGroupId: String): List<Exercise>

}