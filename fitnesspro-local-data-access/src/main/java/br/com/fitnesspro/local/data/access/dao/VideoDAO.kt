package br.com.fitnesspro.local.data.access.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import br.com.android.room.toolkit.dao.IntegratedMaintenanceDAO
import br.com.android.room.toolkit.model.enums.EnumDownloadState
import br.com.android.room.toolkit.model.enums.EnumTransmissionState
import br.com.fitnesspro.model.workout.Video
import java.util.StringJoiner

@Dao
abstract class VideoDAO: IntegratedMaintenanceDAO<Video>() {

    @Query(" select exists(select 1 from video where id = :id) ")
    abstract suspend fun hasEntityWithId(id: String): Boolean

    suspend fun getExportationData(pageSize: Int): List<Video> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select v.* ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from video v ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where v.transmission_state = '${EnumTransmissionState.PENDING.name}' ")
            add(" limit ? ")

            params.add(pageSize)
        }

        val sql = StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
        }

        return executeQueryExportationData(SimpleSQLiteQuery(sql.toString(), params.toTypedArray()))
    }

    suspend fun getStorageExportationData(pageSize: Int): List<Video> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select v.* ")
        }

        val from = StringJoiner(QR_NL).apply {
            add(" from video v ")
        }

        val where = StringJoiner(QR_NL).apply {
            add(" where v.storage_transmission_state = '${EnumTransmissionState.PENDING.name}' ")
            add(" and v.transmission_state = '${EnumTransmissionState.TRANSMITTED.name}' ")
            add(" limit ? ")

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
    abstract suspend fun executeQueryExportationData(query: SupportSQLiteQuery): List<Video>

    suspend fun getStorageImportationData(): List<Video> {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select video.* ")
        }

        val from = getFromStorageImportationData()
        val where = getWhereStorageImportationData()

        val sql = StringJoiner(QR_NL).apply {
            add(select.toString())
            add(from.toString())
            add(where.toString())
        }

        return executeStorageImportationData(SimpleSQLiteQuery(sql.toString(), params.toTypedArray()))
    }

    @RawQuery
    abstract suspend fun executeStorageImportationData(query: SupportSQLiteQuery): List<Video>

    suspend fun getExistsStorageImportationData(): Boolean {
        val params = mutableListOf<Any>()

        val select = StringJoiner(QR_NL).apply {
            add(" select 1 ")
        }

        val from = getFromStorageImportationData()
        val where = getWhereStorageImportationData()

        val sql = StringJoiner(QR_NL).apply {
            add(" select exists ( ")
            add(select.toString())
            add(from.toString())
            add(where.toString())
            add(" ) existe ")
        }

        return executeExistsStorageImportationData(SimpleSQLiteQuery(sql.toString(), params.toTypedArray()))
    }

    private fun getFromStorageImportationData(): StringJoiner {
        return StringJoiner(QR_NL).apply {
            add(" from video ")
        }
    }

    private fun getWhereStorageImportationData(): StringJoiner {
        return StringJoiner(QR_NL).apply {
            add(" where video.storage_url is not null ")
            add(" and video.active = 1 ")
            add(" and video.storage_download_state = '${EnumDownloadState.PENDING.name}' ")
        }
    }

    @RawQuery
    abstract suspend fun executeExistsStorageImportationData(query: SupportSQLiteQuery): Boolean

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

    @Query("""
        select v.*
        from video v
        inner join video_exercise_execution ve on v.id = ve.video_id
        where ve.exercise_execution_id in (:exerciseIds)
        and v.active = 1
    """)
    abstract suspend fun getListVideosActiveFromExecution(exerciseIds: List<String>): List<Video>
}