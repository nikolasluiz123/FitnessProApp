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

    suspend fun getExportationData(pageInfos: ExportPageInfos): List<Video> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select v.* ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from video v ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where v.transmission_state = '${EnumTransmissionState.PENDING.name}' ")
            add(" limit ? offset ? ")

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

    suspend fun getStorageExportationData(pageInfos: ExportPageInfos): List<Video> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select v.* ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from video v ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where v.storage_transmission_state = '${EnumTransmissionState.PENDING.name}' ")
            add(" limit ? offset ? ")

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

    @Query("""
        select v.*
        from video v
        inner join video_exercise ve on v.id = ve.video_id
        where ve.exercise_id in (:exerciseIds)
        and v.active = 1
    """)
    abstract suspend fun getListVideosActiveFromExercise(exerciseIds: List<String>): List<Video>

    @Query("""
        select v.*
        from video v
        inner join video_exercise_pre_definition ve on v.id = ve.video_id
        where ve.exercise_pre_definition_id in (:exerciseIds)
        and v.active = 1
    """)
    abstract suspend fun getListVideosActiveFromPreDefinition(exerciseIds: List<String>): List<Video>
}