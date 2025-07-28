package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.predefinition.VideoExercisePreDefinition
import java.util.StringJoiner

@Dao
abstract class VideoExercisePreDefinitionDAO: IntegratedMaintenanceDAO<VideoExercisePreDefinition>() {

    @Query("select count(id) from video_exercise_pre_definition where exercise_pre_definition_id = :exercisePreDefinitionId")
    abstract suspend fun getCountVideosPreDefinition(exercisePreDefinitionId: String): Int

    @Query("select exists(select 1 from video_exercise_pre_definition where id = :id)")
    abstract suspend fun hasEntityWithId(id: String): Boolean

    suspend fun getExportationData(pageInfos: ExportPageInfos, personId: String): List<VideoExercisePreDefinition> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select v.* ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from video_exercise_pre_definition v ")
            add(" inner join exercise_pre_definition e on e.id = v.exercise_pre_definition_id ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where e.personal_trainer_person_id = ? ")
            add(" and v.transmission_state = '${EnumTransmissionState.PENDING.name}' ")
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
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<VideoExercisePreDefinition>

    @Query(" select * from video_exercise_pre_definition where exercise_pre_definition_id in (:exerciseIds) and active = 1 ")
    abstract suspend fun getListVideoPreDefinitionActiveFromExercises(exerciseIds: List<String>): List<VideoExercisePreDefinition>
}