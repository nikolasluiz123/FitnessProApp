package br.com.fitnesspro.model.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.android.room.toolkit.model.enums.EnumTransmissionState
import br.com.android.room.toolkit.model.interfaces.sync.IntegratedModel
import java.util.UUID

@Entity(
    tableName = "video_exercise",
)
data class VideoExercise(
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "transmission_state", defaultValue = "PENDING")
    override var transmissionState: EnumTransmissionState = EnumTransmissionState.PENDING,
    @ColumnInfo(name = "exercise_id", index = true)
    var exerciseId: String? = null,
    @ColumnInfo(name = "video_id", index = true)
    var videoId: String? = null,
    @ColumnInfo(name = "active", defaultValue = "true")
    var active: Boolean = true
): IntegratedModel