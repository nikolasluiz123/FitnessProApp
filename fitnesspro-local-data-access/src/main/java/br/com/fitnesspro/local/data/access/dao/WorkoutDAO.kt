package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
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
}