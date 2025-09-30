package br.com.fitnesspro.local.data.access.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.android.room.toolkit.dao.IntegratedMaintenanceDAO
import br.com.android.room.toolkit.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.predefinition.ExercisePreDefinition
import br.com.fitnesspro.model.workout.predefinition.WorkoutGroupPreDefinition
import br.com.fitnesspro.to.TOWorkoutGroupPreDefinition
import br.com.fitnesspro.tuple.ExercisePredefinitionGroupedTuple
import java.util.StringJoiner

@Dao
abstract class WorkoutGroupPreDefinitionDAO: IntegratedMaintenanceDAO<WorkoutGroupPreDefinition>() {

    @Query("select * from workout_group_pre_definition where id = :workoutGroupPreDefinitionId")
    abstract suspend fun findById(workoutGroupPreDefinitionId: String?): WorkoutGroupPreDefinition?

    @Query("select exists(select 1 from workout_group_pre_definition where id = :workoutGroupPreDefinitionId)")
    abstract suspend fun hasEntityWithId(workoutGroupPreDefinitionId: String?): Boolean

    suspend fun getExportationData(pageSize: Int, personId: String): List<WorkoutGroupPreDefinition> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select w.* ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from workout_group_pre_definition w ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where w.personal_trainer_person_id = ? ")
            add(" and w.transmission_state = '${EnumTransmissionState.PENDING.name}' ")
            add(" limit ? ")

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
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<WorkoutGroupPreDefinition>

    fun getListPreDefinitions(authenticatedPersonId: String, simpleFilter: String = ""): PagingSource<Int, TOWorkoutGroupPreDefinition> {
        val queryParams = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select w.id as id, ")
            add("        w.name as name, ")
            add("        w.personal_trainer_person_id as personalTrainerPersonId, ")
            add("        w.active as active ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from workout_group_pre_definition w ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where w.personal_trainer_person_id = ? ")
            add(" and w.active = 1 ")

            queryParams.add(authenticatedPersonId)

            if (simpleFilter.isNotEmpty()) {
                add(" and lower(w.name) like ? ")
                queryParams.add("%${simpleFilter.lowercase()}%")
            }
        }

        val orderBy = StringJoiner(QR_NL).apply {
            add(" order by w.name ")
        }

        val sql = StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
            add(orderBy.toString())
        }

        return executePreDefinitions(SimpleSQLiteQuery(sql.toString(), queryParams.toTypedArray()))
    }

    @RawQuery(observedEntities = [ExercisePreDefinition::class])
    abstract fun executePreDefinitions(query: SupportSQLiteQuery): PagingSource<Int, TOWorkoutGroupPreDefinition>

    fun getListGroupedPredefinitions(authenticatedPersonId: String, simpleFilter: String): PagingSource<Int, ExercisePredefinitionGroupedTuple> {
        val queryParams = mutableListOf<Any>()

        val sqlWorkoutGroups = getSQLWorkoutGroups(
            authenticatedPersonId = authenticatedPersonId,
            simpleFilter = simpleFilter,
            queryParams = queryParams
        )

        val sqlExercisesPredefinitionsFromWorkoutGroup = getSQLExercisesPredefinitionsFromWorkoutGroup(
            authenticatedPersonId = authenticatedPersonId,
            simpleFilter = simpleFilter,
            queryParams = queryParams
        )

        val sqlFakeGroupPredefinitions = getSQLFakeGroupPredefinitions(
            authenticatedPersonId = authenticatedPersonId,
            queryParams = queryParams
        )

        val sqlExercisesPredefinitionWithoutGroup = getSQLExercisesPredefinitionWithoutGroup(
            authenticatedPersonId = authenticatedPersonId,
            simpleFilter = simpleFilter,
            queryParams = queryParams
        )

        val orderBy = StringJoiner(QR_NL).apply {
            add(" order by groupName nulls last, isGroupHeader desc, name ")
        }

        val sql = StringJoiner(QR_NL).apply {
            add(sqlWorkoutGroups.toString())
            add(" union all ")
            add(sqlExercisesPredefinitionsFromWorkoutGroup.toString())
            add(" union all ")
            add(sqlFakeGroupPredefinitions.toString())
            add(" union all ")
            add(sqlExercisesPredefinitionWithoutGroup.toString())
            add(orderBy.toString())
        }

        return executeListGroupedPredefinitions(SimpleSQLiteQuery(sql.toString(), queryParams.toTypedArray()))
    }

    private fun getSQLWorkoutGroups(
        authenticatedPersonId: String,
        simpleFilter: String,
        queryParams: MutableList<Any>
    ): StringJoiner {
        val select = StringJoiner(QR_NL).apply {
            add(" select wg.id as id, ")
            add("        wg.name as name, ")
            add("        null as sets, ")
            add("        null as reps, ")
            add("        null as rest, ")
            add("        null as duration, ")
            add("        null as groupId, ")
            add("        wg.name as groupName, ")
            add("        1 as isGroupHeader ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from workout_group_pre_definition wg ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where wg.personal_trainer_person_id = ? ")
            add(" and wg.active = 1 ")

            queryParams.add(authenticatedPersonId)

            if (simpleFilter.isNotEmpty()) {
                add(" and lower(wg.name) like ? ")
                queryParams.add("%${simpleFilter.lowercase()}%")
            }
        }

        return StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
        }
    }

    private fun getSQLExercisesPredefinitionsFromWorkoutGroup(
        authenticatedPersonId: String,
        simpleFilter: String,
        queryParams: MutableList<Any>
    ): StringJoiner {
        val select = StringJoiner(QR_NL).apply {
            add(" select predef.id as id, ")
            add("        predef.name as name, ")
            add("        predef.sets as sets, ")
            add("        predef.repetitions as reps, ")
            add("        predef.rest as rest, ")
            add("        predef.duration as duration, ")
            add("        predef.workout_group_pre_definition_id as groupId, ")
            add("        wg.name as groupName, ")
            add("        0 as isGroupHeader ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from exercise_pre_definition predef ")
            add(" inner join workout_group_pre_definition wg on wg.id = predef.workout_group_pre_definition_id ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where predef.personal_trainer_person_id = ? ")
            add(" and wg.personal_trainer_person_id = ? ")
            add(" and predef.active = 1 ")
            add(" and wg.active = 1 ")

            queryParams.add(authenticatedPersonId)
            queryParams.add(authenticatedPersonId)

            if (simpleFilter.isNotEmpty()) {
                add(" and (lower(predef.name) like ? or lower(wg.name) like ?) ")
                queryParams.add("%${simpleFilter.lowercase()}%")
                queryParams.add("%${simpleFilter.lowercase()}%")
            }
        }

        return StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
        }
    }

    private fun getSQLFakeGroupPredefinitions(
        authenticatedPersonId: String,
        queryParams: MutableList<Any>
    ): StringJoiner {
        val select = StringJoiner(QR_NL).apply {
            add(" select null as id, ")
            add("        null as name, ")
            add("        null as sets, ")
            add("        null as reps, ")
            add("        null as rest, ")
            add("        null as duration, ")
            add("        null as groupId, ")
            add("        null as groupName, ")
            add("        1 as isGroupHeader ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where exists ( ")
            add("                   select 1 from exercise_pre_definition ")
            add("                   where workout_group_pre_definition_id is null ")
            add("                   and personal_trainer_person_id = ? ")
            add("                   and active = 1 ")
            add("              ) ")

            queryParams.add(authenticatedPersonId)
        }

        return StringJoiner(QR_NL).apply {
            add(select.toString())
            add(where.toString())
        }
    }

    private fun getSQLExercisesPredefinitionWithoutGroup(
        authenticatedPersonId: String,
        simpleFilter: String,
        queryParams: MutableList<Any>
    ): StringJoiner {
        val select = StringJoiner(QR_NL).apply {
            add(" select predef.id as id, ")
            add("        predef.name as name, ")
            add("        predef.sets as sets, ")
            add("        predef.repetitions as reps, ")
            add("        predef.rest as rest, ")
            add("        predef.duration as duration, ")
            add("        null as groupId, ")
            add("        null as groupName, ")
            add("        0 as isGroupHeader ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from exercise_pre_definition predef ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where predef.workout_group_pre_definition_id is null ")
            add(" and predef.personal_trainer_person_id = ? ")
            add(" and predef.active = 1 ")

            queryParams.add(authenticatedPersonId)

            if (simpleFilter.isNotEmpty()) {
                add(" and lower(predef.name) like ? ")
                queryParams.add("%${simpleFilter.lowercase()}%")
            }
        }

        return StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
        }
    }

    @RawQuery(observedEntities = [ExercisePreDefinition::class, WorkoutGroupPreDefinition::class])
    abstract fun executeListGroupedPredefinitions(query: SupportSQLiteQuery): PagingSource<Int, ExercisePredefinitionGroupedTuple>

    @RawQuery(observedEntities = [ExercisePreDefinition::class, WorkoutGroupPreDefinition::class])
    abstract suspend fun executeListGroupedPredefinitions2(query: SupportSQLiteQuery): List<ExercisePredefinitionGroupedTuple>

}