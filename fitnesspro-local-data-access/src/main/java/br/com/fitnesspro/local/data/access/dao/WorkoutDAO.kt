package br.com.fitnesspro.local.data.access.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.core.enums.EnumDateTimePatterns.DATE_SQLITE
import br.com.fitnesspro.core.extensions.format
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.filters.RegisterEvolutionWorkoutReportFilter
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.Workout
import br.com.fitnesspro.model.workout.WorkoutGroup
import br.com.fitnesspro.to.TOWorkout
import br.com.fitnesspro.tuple.WorkoutTuple
import br.com.fitnesspro.tuple.reports.evolution.ResumeRegisterEvolutionWorkoutGroupTuple
import br.com.fitnesspro.tuple.reports.evolution.ResumeRegisterEvolutionWorkoutTuple
import java.time.DayOfWeek
import java.util.StringJoiner

@Dao
abstract class WorkoutDAO: IntegratedMaintenanceDAO<Workout>() {

    suspend fun getWorkoutsFromPersonalTrainer(authenticatedPersonId: String, quickFilter: String? = null): List<TOWorkout> {
        val queryParams = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select workout.id as id, ")
            add("        workout.active as active, ")
            add("        workout.date_end as dateEnd, ")
            add("        workout.date_start as dateStart, ")
            add("        workout.personal_trainer_person_id as professionalPersonId, ")
            add("        workout.academy_member_person_id as academyMemberPersonId, ")
            add("        professional.name as professionalName, ")
            add("        member.name as memberName ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from workout ")
            add(" inner join person professional on workout.personal_trainer_person_id = professional.id ")
            add(" inner join person member on workout.academy_member_person_id = member.id ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where workout.personal_trainer_person_id = ? ")
            add(" and workout.active = 1 ")
            add(" and member.active = 1 ")

            queryParams.add(authenticatedPersonId)

            if (!quickFilter.isNullOrBlank()) {
                add(" and lower(member.name) like ? ")
                queryParams.add("%${quickFilter.lowercase()}%")
            }
        }

        val orderBy = StringJoiner(QR_NL).apply {
            add(" order by member.name, workout.date_start desc ")
        }

        val sql = StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
            add(orderBy.toString())
        }

        return executeQueryWorkoutsFromPersonalTrainer(SimpleSQLiteQuery(sql.toString(), queryParams.toTypedArray()))
    }

    @RawQuery
    abstract suspend fun executeQueryWorkoutsFromPersonalTrainer(query: SupportSQLiteQuery): List<TOWorkout>

    @Query("select * from workout where id = :id")
    abstract suspend fun findWorkoutById(id: String): Workout?

    @Query("""
        select workout.*
        from workout
        inner join workout_group on workout.id = workout_group.workout_id
        inner join exercise on workout_group.id = exercise.workout_group_id
        where exercise.id = :exerciseId
    """)
    abstract suspend fun findWorkoutByExerciseId(exerciseId: String): Workout?

    @Query("select exists(select 1 from workout where id = :id)")
    abstract suspend fun hasEntityWithId(id: String): Boolean

    suspend fun getExportationData(pageSize: Int, personId: String): List<Workout> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select * ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from workout w ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where (w.academy_member_person_id = ? or w.personal_trainer_person_id = ?) ")
            add(" and w.transmission_state = '${EnumTransmissionState.PENDING.name}' ")
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
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<Workout>

    suspend fun getListWorkoutGroupsFromCurrentWorkout(personId: String): List<WorkoutGroup> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select wg.* ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from workout w ")
            add(" inner join workout_group wg on w.id = wg.workout_id ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where w.academy_member_person_id = ? ")
            add(" and w.date_end >= date('now') ")
            add(" and w.active = 1 ")
            add(" and wg.active = 1 ")

            params.add(personId)
        }

        val orderBy = StringJoiner(QR_NL).apply {
            add(" order by case wg.day_week ")
            add("     when '${DayOfWeek.MONDAY.name}' then 1 ")
            add("     when '${DayOfWeek.TUESDAY.name}' then 2 ")
            add("     when '${DayOfWeek.WEDNESDAY.name}' then 3 ")
            add("     when '${DayOfWeek.THURSDAY.name}' then 4 ")
            add("     when '${DayOfWeek.FRIDAY.name}' then 5 ")
            add("     when '${DayOfWeek.SATURDAY.name}' then 6 ")
            add("     when '${DayOfWeek.SUNDAY.name}' then 7 ")
            add("     else 8 ")
            add(" end ")
        }

        val sql = StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
            add(orderBy.toString())
        }

        return executeQueryCurrentMemberWorkout(SimpleSQLiteQuery(sql.toString(), params.toTypedArray()))
    }

    @RawQuery
    abstract suspend fun executeQueryCurrentMemberWorkout(query: SupportSQLiteQuery): List<WorkoutGroup>

    @Query("""
        select w.*
        from workout w
        where w.academy_member_person_id = :personId
        and w.date_end >= date('now')
        and w.active = 1
        order by w.date_start desc
        limit 1
    """)
    abstract suspend fun getCurrentMemberWorkout(personId: String): Workout?

    @RawQuery
    abstract suspend fun getResumeRegisterEvolutionWorkoutTuple(query: SupportSQLiteQuery): ResumeRegisterEvolutionWorkoutTuple

    @RawQuery
    abstract suspend fun getResumeRegisterEvolutionWorkoutGroupTuple(query: SupportSQLiteQuery): List<ResumeRegisterEvolutionWorkoutGroupTuple>

    suspend fun getResumeRegisterEvolutionWorkoutTuple(filter: RegisterEvolutionWorkoutReportFilter): ResumeRegisterEvolutionWorkoutTuple {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" SELECT w.date_start as dateStart, w.date_end as dateEnd, p.name as professionalPersonName ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" FROM workout w ")
            add(" INNER JOIN person p ON w.personal_trainer_person_id = p.id ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" WHERE w.id = ? AND w.active = 1 ")
            params.add(filter.workoutId)
        }

        val sql = StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
        }

        return getResumeRegisterEvolutionWorkoutTuple(SimpleSQLiteQuery(sql.toString(), params.toTypedArray()))
    }

    suspend fun getResumeRegisterEvolutionWorkoutGroupTuple(filter: RegisterEvolutionWorkoutReportFilter): List<ResumeRegisterEvolutionWorkoutGroupTuple> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" SELECT wg.day_week as dayWeek, GROUP_CONCAT(wg.name, ', ') as name ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" FROM workout_group wg ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" WHERE wg.workout_id = ? AND wg.active = 1 ")
            params.add(filter.workoutId)

            if (filter.dateStart != null || filter.dateEnd != null) {
                add(" and exists ( ")
                add("               select 1 ")
                add("               from exercise_execution ee ")
                add("               inner join exercise e on ee.exercise_id = e.id ")
                add("               where e.workout_group_id = wg.id ")
                add("               and ee.active = 1 ")
                add("               and e.active = 1 ")

                filter.dateStart?.let {
                    add(" and date(ee.execution_start_time) >= ? ")
                    params.add(it.format(DATE_SQLITE))
                }

                filter.dateEnd?.let {
                    add(" and date(ee.execution_end_time) <= ? ")
                    params.add(it.format(DATE_SQLITE))
                }

                add("            ) ")
            }
        }

        val groupBy = StringJoiner(QR_NL).apply {
            add(" GROUP BY wg.day_week ")
        }

        val orderBy = StringJoiner(QR_NL).apply {
            add(" ORDER BY wg.group_order ")
        }

        val sql = StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
            add(groupBy.toString())
            add(orderBy.toString())
        }
        return getResumeRegisterEvolutionWorkoutGroupTuple(SimpleSQLiteQuery(sql.toString(), params.toTypedArray()))
    }

    fun getWorkoutsFromPerson(authenticatedPersonId: String, simpleFilter: String? = null): PagingSource<Int, WorkoutTuple> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select w.id as id, ")
            add("        w.date_start as dateStart, ")
            add("        w.date_end as dateEnd ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from workout w ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where w.active = 1 ")
            add(" and (w.academy_member_person_id = ? or w.personal_trainer_person_id = ?) ")

            params.add(authenticatedPersonId)
            params.add(authenticatedPersonId)
            
            simpleFilter?.let {
                add(" and (w.date_start like ? or w.date_end like ?) ")
                params.add("%${simpleFilter}%")
                params.add("%${simpleFilter}%")
            }
        }

        val orderBy = StringJoiner(QR_NL).apply {
            add(" order by w.date_start desc ")
        }

        val sql = StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
            add(orderBy.toString())
        }

        return executeQueryWorkoutsFromPerson(SimpleSQLiteQuery(sql.toString(), params.toTypedArray()))
    }

    @RawQuery(observedEntities = [Workout::class])
    abstract fun executeQueryWorkoutsFromPerson(query: SupportSQLiteQuery): PagingSource<Int, WorkoutTuple>
}