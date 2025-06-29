package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.Workout
import br.com.fitnesspro.to.TOWorkout
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

    @Query("select exists(select 1 from workout where id = :id)")
    abstract suspend fun hasEntityWithId(id: String): Boolean

    suspend fun getExportationData(pageInfos: ExportPageInfos, personId: String): List<Workout> {
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
            add(" limit ? offset ? ")

            params.add(personId)
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
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<Workout>
}