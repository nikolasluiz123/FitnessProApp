package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.Exercise
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.tuple.reports.evolution.ExerciseInfosTuple
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

    @Query("select * from exercise where workout_group_id in (:listWorkoutGroupId)")
    abstract suspend fun findExercisesFromWorkoutGroup(listWorkoutGroupId: List<String>): List<Exercise>

    @Query("select count(id) from exercise where workout_group_id = :workoutGroupId and active = 1")
    abstract suspend fun getCountExercisesFromWorkoutGroup(workoutGroupId: String): Int

    @Query("select exists(select 1 from exercise where id = :id)")
    abstract suspend fun hasEntityWithId(id: String): Boolean

    suspend fun getExportationData(pageSize: Int, personId: String): List<Exercise> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select e.* ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from exercise e ")
            add(" inner join workout_group wg on e.workout_group_id = wg.id ")
            add(" inner join workout w on wg.workout_id = w.id ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where (w.academy_member_person_id = ? or w.personal_trainer_person_id = ?) ")
            add(" and e.transmission_state = '${EnumTransmissionState.PENDING.name}' ")
            add(" limit ? ")

            params.add(personId)
            params.add(personId)
            params.add(pageSize)
        }

        val sql = StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
        }

        return executeQueryExportationData(SimpleSQLiteQuery(sql.toString(), params.toTypedArray()))
    }

    @RawQuery
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<Exercise>

    @RawQuery
    abstract suspend fun getExerciseInfosTuple(query: SupportSQLiteQuery): List<ExerciseInfosTuple>

    suspend fun getExerciseInfosTuple(workoutGroupId: String): List<ExerciseInfosTuple> {
        val params = mutableListOf<Any>()
        val select = StringJoiner(QR_NL).apply {
            add(" SELECT e.id as id, ")
            add("        e.name as name, ")
            add("        e.repetitions as repetitions, ")
            add("        e.sets as sets, ")
            add("        e.rest as rest, ")
            add("        e.duration as duration ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" FROM exercise e ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" WHERE e.workout_group_id = ? AND e.active = 1 ")
            params.add(workoutGroupId)
        }

        val orderBy = StringJoiner(QR_NL).apply {
            add(" ORDER BY e.exercise_order ")
        }

        val sql = StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
            add(orderBy.toString())
        }

        return getExerciseInfosTuple(SimpleSQLiteQuery(sql.toString(), params.toTypedArray()))
    }
}