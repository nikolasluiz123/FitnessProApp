package br.com.fitnesspro.local.data.access.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.Exercise
import br.com.fitnesspro.model.workout.execution.ExerciseExecution
import br.com.fitnesspro.tuple.ExecutionEvolutionHistoryGroupedTuple
import br.com.fitnesspro.tuple.ExerciseExecutionGroupedTuple
import br.com.fitnesspro.tuple.charts.ExerciseExecutionChartTuple
import java.util.StringJoiner

@Dao
abstract class ExerciseExecutionDAO: IntegratedMaintenanceDAO<ExerciseExecution>() {

    @Query("select * from exercise_execution where id = :id")
    abstract suspend fun findById(id: String): ExerciseExecution?

    @Query("select count(id) + 1 from exercise_execution where exercise_id = :exerciseId and active = 1")
    abstract suspend fun getActualExecutionSet(exerciseId: String): Int

    fun getListExerciseExecutionGrouped(exerciseId: String): PagingSource<Int, ExerciseExecutionGroupedTuple> {
        val queryParams = mutableListOf<Any>()

        val selectDates = StringJoiner(QR_NL).apply {
            add(" select ")
            add("  null as id, ")
            add("  null as duration, ")
            add("  null as repetitions, ")
            add("  null as actualSet, ")
            add("  null as rest, ")
            add("  null as weight, ")
            add("  date(e.date) as date, ")
            add("  0 as sortOrder, ")
            add("  date(e.date) as groupDate ")
        }

        val fromDates = StringJoiner(QR_NL).apply {
            add(" from exercise_execution e")
        }

        val whereDates = StringJoiner(QR_NL).apply {
            add(" where e.exercise_id = ? ")
            add(" and e.active = 1 ")

            queryParams.add(exerciseId)
        }

        val groupByDates = StringJoiner(QR_NL).apply {
            add(" group by date(e.date)")
        }

        val sqlDates = StringJoiner(QR_NL).apply {
            add(selectDates.toString())
            add(fromDates.toString())
            add(whereDates.toString())
            add(groupByDates.toString())
        }

        val selectExecutions = StringJoiner(QR_NL).apply {
            add(" select ")
            add("  e.id as id, ")
            add("  e.duration as duration, ")
            add("  e.repetitions as repetitions, ")
            add("  e.actual_set as actualSet, ")
            add("  e.rest as rest, ")
            add("  e.weight as weight, ")
            add("  e.date as date, ")
            add("  1 as sortOrder, ")
            add("  date(e.date) as groupDate ")
        }

        val fromExecutions = StringJoiner(QR_NL).apply {
            add(" from exercise_execution e ")
        }

        val sqlExecutions = StringJoiner(QR_NL).apply {
            add(selectExecutions.toString())
            add(fromExecutions.toString())
        }

        val whereExecutions = StringJoiner(QR_NL).apply {
            add(" where e.exercise_id = ? ")
            add(" and e.active = 1 ")

            queryParams.add(exerciseId)
        }

        val orderBy = StringJoiner(QR_NL).apply {
            add(" order by groupDate desc, sortOrder asc, actual_set asc, id desc ")
        }

        val sql = StringJoiner(QR_NL).apply {
            add(sqlDates.toString())
            add(" union all ")
            add(sqlExecutions.toString())
            add(whereExecutions.toString())
            add(orderBy.toString())
        }

        return executeExercisesFromWorkoutGroup(SimpleSQLiteQuery(sql.toString(), queryParams.toTypedArray()))
    }

    @RawQuery(observedEntities = [ExerciseExecution::class])
    abstract fun executeExercisesFromWorkoutGroup(query: SupportSQLiteQuery): PagingSource<Int, ExerciseExecutionGroupedTuple>

    @Query("select exists(select 1 from exercise_execution where id = :id)")
    abstract suspend fun hasEntityWithId(id: String): Boolean

    suspend fun getExportationData(pageInfos: ExportPageInfos, personId: String): List<ExerciseExecution> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select e.* ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from exercise_execution e ")
            add(" inner join exercise ex on e.exercise_id = ex.id ")
            add(" inner join workout_group wg on ex.workout_group_id = wg.id ")
            add(" inner join workout w on wg.workout_id = w.id ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where w.academy_member_person_id = ? ")
            add(" and e.transmission_state = '${EnumTransmissionState.PENDING.name}' ")
            add(" limit ? offset ? ")

            params.add(personId)
            params.add(pageInfos.pageSize)
            params.add(pageInfos.pageSize * pageInfos.pageNumber)
        }

        val sql = StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
        }

        return executeQueryExportationData(SimpleSQLiteQuery(sql.toString(), params.toTypedArray()))
    }

    @RawQuery
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<ExerciseExecution>

    @Query(
"""
            select * 
            from exercise_execution
            where exercise_id = :exerciseId
            and date(date) = :date
            and active = 1
        """
    )
    abstract suspend fun getListActiveExerciseExecution(exerciseId: String, date: String): List<ExerciseExecution>

    fun getListExecutionHistoryGrouped(personMemberId: String, authenticatedPersonId: String, simpleFilter: String): PagingSource<Int, ExecutionEvolutionHistoryGroupedTuple> {
        val queryParams = mutableListOf<Any>()

        val selectDates = StringJoiner(QR_NL).apply {
            add(" select ")
            add("  null as exerciseId, ")
            add("  0 as sortOrder, ")
            add("  w.date_start as dateStart, ")
            add("  w.date_end as dateEnd, ")
            add("  null as exerciseName, ")
            add("  null as exerciseOrder ")
        }

        val fromDates = getFromExecutionHistoryGrouped()

        val whereDates = getWhereExecutionHistoryGrouped(
            queryParams = queryParams,
            authenticatedPersonId = authenticatedPersonId,
            personMemberId = personMemberId,
            simpleFilter = simpleFilter
        )

        val groupByDates = StringJoiner(QR_NL).apply {
            add(" group by w.date_start")
        }

        val sqlDates = StringJoiner(QR_NL).apply {
            add(selectDates.toString())
            add(fromDates.toString())
            add(whereDates.toString())
            add(groupByDates.toString())
        }

        val selectExecutions = StringJoiner(QR_NL).apply {
            add(" select ")
            add("  exercise.id as exerciseId, ")
            add("  1 as sortOrder, ")
            add("  w.date_start as dateStart, ")
            add("  w.date_end as dateEnd, ")
            add("  exercise.name as exerciseName, ")
            add("  exercise.exercise_order as exerciseOrder ")
        }

        val fromExecutions = getFromExecutionHistoryGrouped()

        val whereExecutions = getWhereExecutionHistoryGrouped(
            queryParams = queryParams,
            authenticatedPersonId = authenticatedPersonId,
            personMemberId = personMemberId,
            simpleFilter = simpleFilter
        )

        val groupByExecutions = StringJoiner(QR_NL).apply {
            add(" group by w.date_start, exercise.id ")
        }

        val sqlExecutions = StringJoiner(QR_NL).apply {
            add(selectExecutions.toString())
            add(fromExecutions.toString())
            add(whereExecutions.toString())
            add(groupByExecutions.toString())
        }

        val orderBy = StringJoiner(QR_NL).apply {
            add(" order by dateStart desc, sortOrder asc, exerciseOrder asc, exerciseId desc ")
        }

        val sql = StringJoiner(QR_NL).apply {
            add(sqlDates.toString())
            add(" union all ")
            add(sqlExecutions.toString())
            add(orderBy.toString())
        }

        return executeExecutionHistoryGrouped(SimpleSQLiteQuery(sql.toString(), queryParams.toTypedArray()))
    }

    private fun getFromExecutionHistoryGrouped(): StringJoiner = StringJoiner(QR_NL).apply {
        add(" from exercise_execution execution ")
        add(" inner join exercise on exercise.id = execution.exercise_id ")
        add(" inner join workout_group wg on wg.id = exercise.workout_group_id ")
        add(" inner join workout w on w.id = wg.workout_id ")
    }

    private fun getWhereExecutionHistoryGrouped(
        queryParams: MutableList<Any>,
        authenticatedPersonId: String,
        personMemberId: String,
        simpleFilter: String
    ): StringJoiner = StringJoiner(QR_NL).apply {
        add(" where (w.personal_trainer_person_id = ? or w.academy_member_person_id = ?) ")
        add(" and w.academy_member_person_id = ? ")
        add(" and execution.active = 1 ")
        add(" and exercise.active = 1 ")
        add(" and wg.active = 1 ")
        add(" and w.active = 1 ")

        queryParams.add(authenticatedPersonId)
        queryParams.add(authenticatedPersonId)
        queryParams.add(personMemberId)

        if (simpleFilter.isNotEmpty()) {
            add(" and lower(exercise.name) like ? or execution.date like ? ")
            queryParams.add("%${simpleFilter.lowercase()}%")
            queryParams.add("%${simpleFilter}%")
        }
    }

    @RawQuery(observedEntities = [ExerciseExecution::class, Exercise::class])
    abstract fun executeExecutionHistoryGrouped(query: SupportSQLiteQuery): PagingSource<Int, ExecutionEvolutionHistoryGroupedTuple>

    @Query(
        """
        select date(execution.date) as date,
               execution.weight as weight,
               execution.repetitions as reps,
               execution.rest as rest,
               execution.duration as duration
        from exercise_execution execution
        where execution.exercise_id = :exerciseId
        and execution.active = 1
        order by execution.date
        """
    )
    abstract suspend fun getListExerciseExecutionGroupedBarChartTuple(exerciseId: String): List<ExerciseExecutionChartTuple>

}