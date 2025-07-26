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
import br.com.fitnesspro.model.workout.predefinition.ExercisePreDefinition
import br.com.fitnesspro.model.workout.predefinition.WorkoutGroupPreDefinition
import br.com.fitnesspro.to.TOExercise
import br.com.fitnesspro.to.TOExercisePreDefinition
import java.util.StringJoiner

@Dao
abstract class ExercisePreDefinitionDAO: IntegratedMaintenanceDAO<ExercisePreDefinition>() {

    fun getExercisesAndPreDefinitions(
        workoutId: String,
        authenticatedPersonId: String,
        simpleFilter: String
    ): PagingSource<Int, TOExercise> {
        val queryParams = mutableListOf<Any>()

        val selectPreDef = StringJoiner(QR_NL).apply {
            add(" select exercisePreDef.id as id, ")
            add("        exercisePreDef.name as name, ")
            add("        exercisePreDef.duration as duration, ")
            add("        exercisePreDef.repetitions as repetitions, ")
            add("        exercisePreDef.sets as sets, ")
            add("        exercisePreDef.rest as rest, ")
            add("        null as observation, ")
            add("        exercisePreDef.workout_group_pre_definition_id as workoutGroupId, ")
            add("        exercisePreDef.active as active, ")
            add("        1 as preDefinition ")
        }

        val fromPreDef = StringJoiner(QR_NL).apply {
            add(" from exercise_pre_definition exercisePreDef ")
        }

        val wherePreDef = StringJoiner(QR_NL).apply {
            add(" where exercisePreDef.personal_trainer_person_id = ? ")
            add(" and exercisePreDef.active = 1 ")

            queryParams.add(authenticatedPersonId)

            if (simpleFilter.isNotEmpty()) {
                add(" and lower(exercisePreDef.name) like ? ")
                queryParams.add("%${simpleFilter.lowercase()}%")
            }
        }

        val sqlPreDef = StringJoiner(QR_NL).apply {
            add(selectPreDef.toString())
            add(fromPreDef.toString())
            add(wherePreDef.toString())
        }

        val selectExercise = StringJoiner(QR_NL).apply {
            add(" select exercise.id as id, ")
            add("        exercise.name as name, ")
            add("        exercise.duration as duration, ")
            add("        exercise.repetitions as repetitions, ")
            add("        exercise.sets as sets, ")
            add("        exercise.rest as rest, ")
            add("        exercise.observation as observation, ")
            add("        exercise.workout_group_id as workoutGroupId, ")
            add("        exercise.active as active, ")
            add("        0 as preDefinition ")
        }

        val fromExercise = StringJoiner(QR_NL).apply {
            add(" from exercise exercise ")
            add(" inner join workout_group workoutGroup on workoutGroup.id = exercise.workout_group_id ")
        }

        val whereExercise = StringJoiner(QR_NL).apply {
            add(" where workoutGroup.workout_id = ? ")
            add(" and exercise.active = 1 ")

            queryParams.add(workoutId)

            if (simpleFilter.isNotEmpty()) {
                add(" and lower(exercise.name) like ? ")
                queryParams.add("%${simpleFilter.lowercase()}%")
            }
        }

        val sqlExercise = StringJoiner(QR_NL).apply {
            add(selectExercise.toString())
            add(fromExercise.toString())
            add(whereExercise.toString())
        }

        val orderBy = StringJoiner(QR_NL).apply {
            add(" order by preDefinition, name ")
        }

        val sql = StringJoiner(QR_NL).apply {
            add(sqlPreDef.toString())
            add(" union all ")
            add(sqlExercise.toString())
            add(orderBy.toString())
        }

        return executeExercisesFromWorkoutGroup(SimpleSQLiteQuery(sql.toString(), queryParams.toTypedArray()))
    }

    fun getListPreDefinitions(authenticatedPersonId: String, simpleFilter: String): PagingSource<Int, TOExercisePreDefinition> {
        val queryParams = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select exercisePreDef.id as id, ")
            add("        exercisePreDef.name as name, ")
            add("        exercisePreDef.duration as duration, ")
            add("        exercisePreDef.repetitions as repetitions, ")
            add("        exercisePreDef.sets as sets, ")
            add("        exercisePreDef.rest as rest, ")
            add("        exercisePreDef.active as active ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from exercise_pre_definition exercisePreDef ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where exercisePreDef.personal_trainer_person_id = ? ")
            add(" and exercisePreDef.workout_group_pre_definition_id is null ")
            add(" and exercisePreDef.active = 1 ")

            queryParams.add(authenticatedPersonId)

            if (simpleFilter.isNotEmpty()) {
                add(" and lower(exercisePreDef.name) like ? ")
                queryParams.add("%${simpleFilter.lowercase()}%")
            }
        }

        val orderBy = StringJoiner(QR_NL).apply {
            add(" order by exercisePreDef.name ")
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
    abstract fun executePreDefinitions(query: SupportSQLiteQuery): PagingSource<Int, TOExercisePreDefinition>

    @RawQuery(observedEntities = [ExercisePreDefinition::class, WorkoutGroupPreDefinition::class])
    abstract fun executeExercisesFromWorkoutGroup(query: SupportSQLiteQuery): PagingSource<Int, TOExercise>

    @Query("select * from exercise_pre_definition where lower(name) = lower(:name) and active = 1")
    abstract suspend fun findExercisePreDefinitionByName(name: String): ExercisePreDefinition?

    @Query("select * from exercise_pre_definition where id = :id")
    abstract suspend fun findExercisePreDefinitionById(id: String): ExercisePreDefinition?

    @Query("select exists(select 1 from exercise_pre_definition where id = :id)")
    abstract suspend fun hasEntityWithId(id: String): Boolean

    suspend fun getExportationData(pageInfos: ExportPageInfos, personId: String): List<ExercisePreDefinition> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select e.* ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from exercise_pre_definition e ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where e.personal_trainer_person_id = ? ")
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
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<ExercisePreDefinition>

}