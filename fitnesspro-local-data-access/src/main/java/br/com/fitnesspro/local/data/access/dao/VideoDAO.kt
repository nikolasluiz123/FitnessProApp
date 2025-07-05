package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.fitnesspro.local.data.access.dao.common.IntegratedMaintenanceDAO
import br.com.fitnesspro.local.data.access.dao.common.filters.ExportPageInfos
import br.com.fitnesspro.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.Video
import java.util.StringJoiner

@Dao
abstract class VideoDAO: IntegratedMaintenanceDAO<Video>() {

    @Query(" select exists(select 1 from video where id = :id) ")
    abstract suspend fun hasEntityWithId(id: String): Boolean

    suspend fun getExportationData(pageInfos: ExportPageInfos, personId: String): List<Video> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select v.id as id, ")
            add("        v.extension as extension, ")
            add("        v.file_path as filePath, ")
            add("        v.date as date, ")
            add("        v.kb_size as kbSize, ")
            add("        v.seconds as seconds, ")
            add("        v.width as width, ")
            add("        v.height as height ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from video_exercise ve ")
            add(" inner join video v on ve.video_id = v.id ")
            add(" inner join exercise e on ve.exercise_id = e.id ")
            add(" inner join workout_group wg on e.workout_group_id = wg.id ")
            add(" inner join workout w on wg.workout_id = w.id ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where (w.academy_member_person_id = ? or w.personal_trainer_person_id = ?) ")
            add(" and v.transmission_state = '${EnumTransmissionState.PENDING.name}' ")
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
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<Video>

    @Query(" delete from video where id in (:ids) ")
    abstract suspend fun deleteVideos(ids: List<String>)
}